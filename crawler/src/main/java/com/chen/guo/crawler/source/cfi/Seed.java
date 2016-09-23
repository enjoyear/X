package com.chen.guo.crawler.source.cfi;


import com.chen.guo.common.Exception.ExceptionUtils;
import com.chen.guo.crawler.StockPage;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public class Seed {
  private static final Logger logger = Logger.getLogger(Seed.class);
  private static final int CFI_CONNECTION_DEFAULT_TIME_OUT = 2000;

  public static void main(String[] args) {

    try {
      List<StockPage> stockPageStream = getBasePages();
      stockPageStream.forEach(System.out::println);
    } catch (IOException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
    }
  }

  private static List<StockPage> getBasePages() throws IOException {
    Connection connect = Jsoup.connect("http://quote.cfi.cn/stockList.aspx");
    connect.timeout(CFI_CONNECTION_DEFAULT_TIME_OUT);
    Document listPage = connect.get();
    Element content = listPage.getElementById("divcontent");
    Element table = content.getElementsByTag("table").first().getElementsByTag("tbody").first();
    Elements rows = table.getElementsByTag("tr");

    List<StockPage> interestedPages = new ArrayList<>(4000);
    for (Element row : rows) {
      for (Element col : row.children()) {
        String nameCode = col.text();
        int index = nameCode.indexOf("(");
        StockPage sp = new StockPage(nameCode.substring(0, index).trim(),
            nameCode.substring(index + 1, nameCode.length() - 1).trim(),
            col.getElementsByTag("a").get(0).absUrl("href"));
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
