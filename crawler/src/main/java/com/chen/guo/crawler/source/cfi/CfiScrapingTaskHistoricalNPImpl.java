package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.util.WebAccessUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CfiScrapingTaskHistoricalNPImpl extends CfiScrapingTask {
  private static final Logger logger = Logger.getLogger(CfiScrapingTaskHistoricalNPImpl.class);

  @Override
  void scrape(String baseUrl) throws IOException {
    logger.info("Scraping page: " + baseUrl);
    Element netProfitTr = getMainTable(baseUrl).getElementsContainingOwnText("归属母公司净利润").first();
    //Get all historical
    Document netProfitPage = WebAccessUtil.getInstance().getPageContent(netProfitTr.absUrl("href"));
    Elements rows = netProfitPage.getElementById("content").getElementsByTag("tbody").first().children();

    rows.forEach(r -> {
      r.children().forEach(c -> {
        System.out.print(c.text());
        System.out.print("\t");
      });
      System.out.print("\n");
    });
  }

}
