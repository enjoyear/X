package com.chen.guo.crawler.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebPageUtil {
  private static final int CFI_CONNECTION_DEFAULT_TIME_OUT = 5000;

  public static Document getPageContent(String url) throws IOException {
    return getPageContent(url, CFI_CONNECTION_DEFAULT_TIME_OUT);
  }

  public static Document getPageContent(String url, int connectionTimeout) throws IOException {
    Connection connect = Jsoup.connect(url);
    connect.timeout(connectionTimeout);
    return connect.get();
  }

  public static String getHyperlink(Element element) {
    return element.getElementsByTag("a").get(0).absUrl("href");
  }
}
