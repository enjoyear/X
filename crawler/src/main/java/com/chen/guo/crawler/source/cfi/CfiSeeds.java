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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

class CfiScrapingTask extends RecursiveAction {
  private static final Logger logger = Logger.getLogger(CfiScrapingTask.class);
  private final List<StockWebPage> pages;
  private final int low;
  private final int high;
  private static int TASK_COUNT_PER_THREAD = 1;

  /**
   * @param pages all tasks to do
   * @param low   inclusive low end
   * @param high  exclusive high end
   */
  CfiScrapingTask(List<StockWebPage> pages, int low, int high) {
    this.pages = pages;
    this.low = low;
    this.high = high;
  }

  @Override
  protected void compute() {
    if (high - low <= TASK_COUNT_PER_THREAD) {
      for (int i = low; i < high; ++i) {
        StockWebPage page = pages.get(i);
        try {
          Element pageFoundamentalIndicators = WebPageUtil.getPageContent(page.getUrl())
              .getElementById("nodea1");
          Element nonbreakableFI = pageFoundamentalIndicators.getElementsByTag("nobr").first();
          if (!"财务分析指标".equals(nonbreakableFI.text()))
            throw new UnexpectedException("Didn't get the correct 财务分析指标 page for " + page.getUrl());
          System.out.println(WebPageUtil.getHyperlink(nonbreakableFI));
        } catch (IOException e) {
          ExceptionUtils.logExceptionShort(logger, e, "Current URL: " + page.getUrl());
        }
      }
    } else {
      int mid = (low + high) >>> 1;
      invokeAll(new CfiScrapingTask(pages, low, mid), new CfiScrapingTask(pages, mid, high));
    }
  }
}

public class CfiSeeds {
  private static final Logger logger = Logger.getLogger(CfiSeeds.class);
  private static final Integer MAX_THREAD_COUNT = 8;

  public static void main(String[] args) {

    try {
      List<StockWebPage> quoteBaseUrls = getBasePages();
      long startTime = System.currentTimeMillis();
      ForkJoinPool pool = new ForkJoinPool(MAX_THREAD_COUNT);
      pool.invoke(new CfiScrapingTask(Collections.unmodifiableList(quoteBaseUrls), 0, quoteBaseUrls.size()));
      System.out.println("That took " + (System.currentTimeMillis() - startTime) + " milliseconds");
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
