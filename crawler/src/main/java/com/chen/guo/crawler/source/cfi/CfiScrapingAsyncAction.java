package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;

class CfiScrapingTask extends RecursiveAction {
  private static final Logger logger = Logger.getLogger(CfiScrapingTask.class);
  private ConcurrentLinkedQueue<StockWebPage> failedPages;
  private final WebAccessUtil webUtil;
  private final ArrayList<StockWebPage> pages;
  private final int low;
  private final int high;
  private final static int TASK_COUNT_PER_THREAD = 2; //TASK_COUNT_PER_THREAD >= 1

  /**
   * @param pages       Keep all tasks to do. pages must be random accessible.
   * @param failedPages As a return value
   */
  public CfiScrapingTask(ArrayList<StockWebPage> pages, ConcurrentLinkedQueue<StockWebPage> failedPages) {
    this(pages, 0, pages.size(), failedPages, WebAccessUtil.getInstance());
  }

  /**
   * @param pages       Keep all tasks to do. pages must be random accessible.
   * @param failedPages As a return value
   */
  public CfiScrapingTask(ArrayList<StockWebPage> pages, ConcurrentLinkedQueue<StockWebPage> failedPages, WebAccessUtil webUtil) {
    this(pages, 0, pages.size(), failedPages, webUtil);
  }

  /**
   * @param pages       Keep all tasks to do. pages must be random accessible.
   * @param low         Inclusive low end
   * @param high        Exclusive high end
   * @param failedPages As a return value
   */
  public CfiScrapingTask(ArrayList<StockWebPage> pages, int low, int high,
                         ConcurrentLinkedQueue<StockWebPage> failedPages, WebAccessUtil webUtil) {
    this.pages = pages;
    this.low = low;
    this.high = high;
    this.failedPages = failedPages;
    this.webUtil = webUtil;
  }

  @Override
  protected void compute() {
    if (high - low <= TASK_COUNT_PER_THREAD) {
      for (int i = low; i < high; ++i) {
        StockWebPage page = pages.get(i);
        String rootUrl = page.getUrl();
        try {
          //Try to get 财务分析指标 page
          Element pageFoundamentalIndicators = webUtil.getPageContent(rootUrl)
              .getElementById("nodea1");
          Element nonbreakableFI = pageFoundamentalIndicators.getElementsByTag("nobr").first();
          if (!"财务分析指标".equals(nonbreakableFI.text()))
            throw new UnexpectedException("Didn't get the correct 财务分析指标 page for " + rootUrl);
          System.out.println(WebAccessUtil.getHyperlink(nonbreakableFI));
        } catch (IOException e) {
          failedPages.add(page);
          logger.error("Current URL: " + rootUrl + System.lineSeparator() + e.getMessage());
        }
      }
    } else {
      int mid = (low + high) >>> 1;
      //Divide and conquer
      invokeAll(
          new CfiScrapingTask(pages, low, mid, failedPages, webUtil),
          new CfiScrapingTask(pages, mid, high, failedPages, webUtil));
    }
  }
}