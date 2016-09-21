package com.chen.guo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CfiScrapper {
  public static void main(String[] args) throws IOException {
    Document doc = Jsoup.connect("http://quote.cfi.cn/cwfxzb/11576/300182.html").get();
    Element content = doc.getElementById("content");
    Element table = content.getElementsByTag("table").first().getElementsByTag("tbody").first();
    Element yearMonthTr = table.getElementsByTag("tr").get(1);
    if (yearMonthTr.getElementsContainingOwnText("截止日期").size() != 1)
      throw new RuntimeException("Didn't get correct line for 截止日期");
    yearMonthTr.children().forEach(c -> System.out.println(c.text()));

    Element netProfitTr = table.getElementsContainingOwnText("归属母公司净利润").first();
    //Get from main page.
    netProfitTr.parent().parent().children().forEach(c -> System.out.println(c.text()));

    //Get all historical
    Document netProfitPage = Jsoup.connect(netProfitTr.absUrl("href")).get();
    Elements rows = netProfitPage.getElementsByTag("tr");
    rows.forEach(r -> {
      r.children().forEach(c -> {
        System.out.print(c.text());
        System.out.print("\t");
      });
      System.out.print("\n");
    });
  }
}
