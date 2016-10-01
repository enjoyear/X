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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class CfiSeeds {
  private static final Logger logger = Logger.getLogger(CfiSeeds.class);
  private static final Integer MAX_THREAD_COUNT = 8; //Should be configured to use the max count of cores of the machine
  private static final WebPageUtil WEB_PAGE_UTIL = WebPageUtil.getInstance();

  public static void main(String[] args) {

    try {
      ArrayList<StockWebPage> allPages = getBasePages();
      logger.info("Total number of quote base urls: " + allPages.size());
      long startTime = System.currentTimeMillis();

      WebPageUtil localWebUtil = WEB_PAGE_UTIL;
      WebPageUtil webUtil20 = new WebPageUtil(20);
      ForkJoinPool pool = new ForkJoinPool(MAX_THREAD_COUNT);
      int retryCount = 3;
      while (!allPages.isEmpty() && retryCount > 0) {
        ConcurrentLinkedQueue<StockWebPage> failedPages = new ConcurrentLinkedQueue<>();
        pool.invoke(new CfiScrapingTask(allPages, failedPages, localWebUtil));
        logger.info(String.format("%d out of %d pages failed", failedPages.size(), allPages.size()));

        if (!failedPages.isEmpty()) {
          --retryCount;
          localWebUtil = webUtil20; //set longer connection time
          allPages.clear();
          failedPages.forEach(allPages::add); //Add failed pages to allPages and try again.
          logger.warn(String.format("Retries remaining %d times. Failed pages are as follows: ", retryCount));
          logger.warn(String.join(",", allPages.stream().map(StockWebPage::toString).collect(Collectors.toList())));
        }
      }

      logger.info("Whole process took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds to finish");
      if (!allPages.isEmpty() && retryCount == 0) {
        throw new java.net.ConnectException(String.format(
            "Failed to connect to %d pages:\n%s", allPages.size(),
            String.join(",", allPages.stream().map(StockWebPage::toString).collect(Collectors.toList()))));
      }
    } catch (IOException e) {
      ExceptionUtils.error(logger, e);
    }
  }

  private static ArrayList<StockWebPage> getBasePages() throws IOException {
    Document listPage = WEB_PAGE_UTIL.getPageContent("http://quote.cfi.cn/stockList.aspx");
    Element content = listPage.getElementById("divcontent");
    Element table = content.getElementsByTag("table").first().getElementsByTag("tbody").first();
    Elements rows = table.getElementsByTag("tr");

    ArrayList<StockWebPage> interestedPages = new ArrayList<>(4000);
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
