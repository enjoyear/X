package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CfiScrapingNetIncomeTask extends CfiScrapingTask {

  @Override
  public void scrape(StockWebPage page) throws IOException {
    //Try to get 财务分析指标 page
    String rootUrl = page.getUrl();
    Element pageFoundamentalIndicators = WebAccessUtil.getInstance().getPageContent(rootUrl)
        .getElementById("nodea1");
    Element nonbreakableFI = pageFoundamentalIndicators.getElementsByTag("nobr").first();
    if (!"财务分析指标".equals(nonbreakableFI.text()))
      throw new UnexpectedException("Didn't get the correct 财务分析指标 page for " + rootUrl);
    scrape(page.getCode(), WebAccessUtil.getHyperlink(nonbreakableFI));
  }

  protected abstract void scrape(String ticker, String baseUrl) throws IOException;

}
