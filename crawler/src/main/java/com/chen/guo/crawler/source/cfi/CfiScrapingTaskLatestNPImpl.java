package com.chen.guo.crawler.source.cfi;

import org.jsoup.nodes.Element;

import java.io.IOException;

public class CfiScrapingTaskLatestNPImpl extends CfiScrapingTask {
  public static void main(String[] args) throws IOException {
    new CfiScrapingTaskLatestNPImpl().scrape("http://quote.cfi.cn/cwfxzb/11576/300182.html");
  }

  @Override
  void scrape(String baseUrl) throws IOException {
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
