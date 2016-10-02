package com.chen.guo.crawler.source.cfi;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class CfiScrapingTaskLatestNPImpl extends CfiScrapingTask {
  private static final Logger logger = Logger.getLogger(CfiScrapingTaskLatestNPImpl.class);

  @Override
  public void scrape(String baseUrl) throws IOException {
    logger.info("Scraping page: " + baseUrl);
    Element table = getMainTable(baseUrl);
    Element yearMonthTr = table.getElementsByTag("tr").get(1);
    if (yearMonthTr.getElementsContainingOwnText("截止日期").size() != 1)
      throw new RuntimeException("Didn't get correct line for 截止日期");
    yearMonthTr.children().forEach(c -> System.out.println(c.text()));
    Element netProfitTr = table.getElementsContainingOwnText("归属母公司净利润").first();
    //Get from main page.
    netProfitTr.parent().parent().children().forEach(c -> System.out.println(c.text()));
  }

}
