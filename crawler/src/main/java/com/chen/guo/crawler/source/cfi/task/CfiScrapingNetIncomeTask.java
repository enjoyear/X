package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.rmi.UnexpectedException;

public abstract class CfiScrapingNetIncomeTask extends ScrapingTask<Integer, Double> {
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

  protected abstract void scrape(String ticker, String url财务分析指标) throws IOException;

  protected Element getMainTable(String baseUrl) throws IOException {
    Document doc = WebAccessUtil.getInstance().getPageContent(baseUrl);
    Element content = doc.getElementById("content");
    return content.getElementsByTag("table").first().getElementsByTag("tbody").first();
  }
}
