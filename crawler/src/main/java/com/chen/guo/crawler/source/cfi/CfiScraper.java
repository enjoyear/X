package com.chen.guo.crawler.source.cfi;

import com.chen.guo.common.Exception.ExceptionUtils;
import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessUtil;
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

public class CfiScraper {
  private static final Logger logger = Logger.getLogger(CfiScraper.class);
  private static final Integer MAX_THREAD_COUNT = 8; //Should be configured to use the max count of cores of the machine
  private static final WebAccessUtil WEB_PAGE_UTIL = WebAccessUtil.getInstance();

  public static void main(String[] args) {

    try {
      ArrayList<StockWebPage> allPages = getBasePages();
      logger.info("Total number of quote base urls: " + allPages.size());
      long startTime = System.currentTimeMillis();

      ForkJoinPool pool = new ForkJoinPool(MAX_THREAD_COUNT);
      WebAccessUtil localWebUtil = WEB_PAGE_UTIL;
      WebAccessUtil webUtil20 = new WebAccessUtil(20);
      int retryCount = 3;
      ArrayList<StockWebPage> workToBeDone = allPages;
      while (!workToBeDone.isEmpty() && retryCount > 0) {
        ConcurrentLinkedQueue<StockWebPage> failedPages = new ConcurrentLinkedQueue<>();
        pool.invoke(new CfiScrapingAsyncAction(new CfiScrapingTaskLatestNPImpl(), workToBeDone, failedPages, localWebUtil));
        logger.info(String.format("%d out of %d pages failed", failedPages.size(), workToBeDone.size()));

        workToBeDone = new ArrayList<>();
        if (!failedPages.isEmpty()) {
          --retryCount;
          localWebUtil = webUtil20; //set longer connection time
          workToBeDone.clear();
          failedPages.forEach(workToBeDone::add); //Add failed pages to workToBeDone and try again.
          logger.warn(String.format("Retries remaining %d times. Failed pages are as follows: ", retryCount));
          logger.warn(String.join(",", workToBeDone.stream().map(StockWebPage::toString).collect(Collectors.toList())));
        }
      }

      logger.info("Whole process took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds to finish");
      if (!workToBeDone.isEmpty() && retryCount == 0) {
        throw new java.net.ConnectException(String.format(
            "Failed to connect to %d pages:\n%s", workToBeDone.size(),
            String.join(",", workToBeDone.stream().map(StockWebPage::toString).collect(Collectors.toList()))));
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
            WebAccessUtil.getHyperlink(col));
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
