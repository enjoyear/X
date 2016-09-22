package com.chen.guo.crawler.source.cfi;


import com.chen.guo.common.Exception.UnexpectedCaseException;
import com.chen.guo.crawler.StockPage;
import com.chen.guo.common.Exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

public class Seed {
  private static final Logger logger = Logger.getLogger(Seed.class);
  private static final int CFI_CONNECTION_DEFAULT_TIME_OUT = 2000;

  public static void main(String[] args) {

    try {
      Stream<StockPage> stockPageStream = getBasePages();
      stockPageStream.forEach(System.out::println);
    } catch (IOException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
    }
  }

  private static Stream<StockPage> getBasePages() throws IOException {
    Connection connect = Jsoup.connect("http://quote.cfi.cn/stockList.aspx");
    connect.timeout(CFI_CONNECTION_DEFAULT_TIME_OUT);
    Document listPage = connect.get();
    Element content = listPage.getElementById("divcontent");
    Element table = content.getElementsByTag("table").first().getElementsByTag("tbody").first();
    Elements rows = table.getElementsByTag("tr");
    Stream<StockPage> stream = rows.stream().flatMap(row ->
        row.children().stream().map(
            col -> {
              String nameCode = col.text();
              int index = nameCode.indexOf("(");
              return new StockPage(nameCode.substring(0, index).trim(),
                  nameCode.substring(index + 1, nameCode.length() - 1).trim(),
                  col.getElementsByTag("a").get(0).absUrl("href"));
            }))
        .filter(s -> {
          String code = s.getCode();
          boolean wanted = code.startsWith("0") || code.startsWith("6") || code.startsWith("3");
          if (wanted && code.length() != 6) {
            throw new UnexpectedCaseException(String.format("Unexpected length of code for %s", s));
          }
          return wanted;
        });
    return stream;
  }
}
