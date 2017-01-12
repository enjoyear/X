package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.FundamentalScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class CfiScrapingQuoteTask extends FundamentalScrapingTask {

  @Override
  public void scrape(StockWebPage page) throws IOException {
    //Try to get 财务分析指标 page
    String rootUrl = page.getUrl();
    Document url行情首页 = WebAccessUtil.getInstance().getPageContent(rootUrl);
    Document url = url行情首页;
    Element quoteBlock = url.getElementById("last");
    String quote = quoteBlock.text();
    Element changeBlock = url.getElementById("chg");

    String valueChange = changeBlock.childNode(0).toString();
    String percentageChange = changeBlock.childNode(2).toString();


  }
}
