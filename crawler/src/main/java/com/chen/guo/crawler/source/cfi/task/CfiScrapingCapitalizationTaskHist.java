package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

public class CfiScrapingCapitalizationTaskHist extends ScrapingTask<Integer, Double> {
  private static final Logger logger = Logger.getLogger(CfiScrapingCapitalizationTaskHist.class);

  private final int _startYear;
  private final CfiScrapingMenuTask _menuTask;

  /**
   * @param startYear denotes the oldest year we care about. This startYear is inclusive
   */
  public CfiScrapingCapitalizationTaskHist(int startYear) {
    _startYear = startYear;
    _menuTask = new CfiScrapingMenuTask("nodea21", "股本结构");
  }

  @Override
  public void scrape(StockWebPage stockWebPage) throws IOException {
    String url = _menuTask.getMenuPage(stockWebPage);
    Document doc = WebAccessUtil.getInstance().getPageContent(url);
    Element content = doc.getElementById("sel");

  }
}
