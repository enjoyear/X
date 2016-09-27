package com.chen.guo.crawler.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public enum WebPageUtil {
  INSTANCE;

  private static final int CFI_CONNECTION_DEFAULT_TIME_OUT = 2000;

  public Document getPageContent(String url) throws IOException {
    return getPageContent(url, CFI_CONNECTION_DEFAULT_TIME_OUT);
  }

  public Document getPageContent(String url, int connectionTimeout) throws IOException {
    Connection connect = Jsoup.connect(url);
    connect.timeout(connectionTimeout);
    return connect.get();
  }

  public String getHyperlink(Element element) {
    return element.getElementsByTag("a").get(0).absUrl("href");
  }
}
