package com.chen.guo.crawler.source.cfi;


import com.chen.guo.common.Exception.ExceptionUtils;
import com.chen.guo.crawler.StockWebPage;
import com.chen.guo.crawler.util.WebPageUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

class CfiScrapingTask extends RecursiveAction {
  private static final Logger logger = Logger.getLogger(CfiScrapingTask.class);
  private ConcurrentLinkedQueue<StockWebPage> failedPages;
  private final List<StockWebPage> pages;
  private final int low;
  private final int high;
  private static int TASK_COUNT_PER_THREAD = 2; //TASK_COUNT_PER_THREAD >= 1

  /**
   * @param pages all tasks to do
   * @param low   inclusive low end
   * @param high  exclusive high end
   */
  CfiScrapingTask(List<StockWebPage> pages, int low, int high, ConcurrentLinkedQueue<StockWebPage> failedPages) {
    this.pages = pages;
    this.low = low;
    this.high = high;
    this.failedPages = failedPages;
  }

  @Override
  protected void compute() {
    if (high - low <= TASK_COUNT_PER_THREAD) {
      for (int i = low; i < high; ++i) {
        StockWebPage page = pages.get(i);
        String rootUrl = page.getUrl();
        try {
          Element pageFoundamentalIndicators = WebPageUtil.getPageContent(rootUrl)
              .getElementById("nodea1");
          Element nonbreakableFI = pageFoundamentalIndicators.getElementsByTag("nobr").first();
          if (!"财务分析指标".equals(nonbreakableFI.text()))
            throw new UnexpectedException("Didn't get the correct 财务分析指标 page for " + rootUrl);
          System.out.println(WebPageUtil.getHyperlink(nonbreakableFI));
        } catch (IOException e) {
          failedPages.add(page);
          ExceptionUtils.logExceptionShort(logger, e, "Current URL: " + rootUrl);
        }
      }
    } else {
      int mid = (low + high) >>> 1;
      invokeAll(new CfiScrapingTask(pages, low, mid, failedPages), new CfiScrapingTask(pages, mid, high, failedPages));
    }
  }
}

public class CfiSeeds {
  private static final Logger logger = Logger.getLogger(CfiSeeds.class);
  private static final Integer MAX_THREAD_COUNT = 8; //Should be configured to use the max count of cores of the machine

  public static void main(String[] args) {

    try {
      List<StockWebPage> basePages = getBasePages();
      logger.info("Total number of quote base urls: " + basePages.size());
      long startTime = System.currentTimeMillis();
      ForkJoinPool pool = new ForkJoinPool(MAX_THREAD_COUNT);

      ConcurrentLinkedQueue<StockWebPage> failedPages = new ConcurrentLinkedQueue<>();
      while (!basePages.isEmpty()) {
        pool.invoke(new CfiScrapingTask(Collections.unmodifiableList(basePages), 0, basePages.size(), failedPages));
        StockWebPage[] nextRound = (StockWebPage[]) failedPages.toArray();
        basePages = Arrays.asList(nextRound);
        failedPages.clear();
        if (!basePages.isEmpty()) {
          logger.info("Number of pages for next round is: " + basePages.size());
          logger.info("The pages are as follows: ");
          basePages.forEach(page -> logger.info(page.toString()));
        }
      }
      logger.info("Whole process took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds to finish");
    } catch (IOException e) {
      ExceptionUtils.logExceptionLong(logger, e);
    }
  }

  private static List<StockWebPage> getBasePages() throws IOException {
    Document listPage = WebPageUtil.getPageContent("http://quote.cfi.cn/stockList.aspx");
    Element content = listPage.getElementById("divcontent");
    Element table = content.getElementsByTag("table").first().getElementsByTag("tbody").first();
    Elements rows = table.getElementsByTag("tr");

    List<StockWebPage> interestedPages = new ArrayList<>(4000);
    for (Element row : rows) {
      for (Element col : row.children()) {
        String nameCode = col.text();
        int index = nameCode.indexOf("(");
        StockWebPage sp = new StockWebPage(nameCode.substring(0, index).trim(),
            nameCode.substring(index + 1, nameCode.length() - 1).trim(),
            WebPageUtil.getHyperlink(col));
        String code = sp.getCode();
        if (code.startsWith("0") || code.startsWith("6") || code.startsWith("3")) {
          interestedPages.add(sp);
          if (code.length() != 6) {
            throw new UnexpectedException(String.format("Unexpected length of code starting with 0,3,6. The code is %s", sp));
          }
        }
      }
    }
    return interestedPages;
  }
}
