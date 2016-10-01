package com.chen.guo.crawler.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebPageUtil {
  private final int connectionTimeout;
  private static WebPageUtil INSTANCE_DEFAULT = null;

  static {
    INSTANCE_DEFAULT = new WebPageUtil();
  }

  public static WebPageUtil getInstance() {
    return INSTANCE_DEFAULT;
  }

  private WebPageUtil() {
    this(5);  //default timeout for internet connection is 5 second
  }

  public WebPageUtil(Integer internetTimeoutSecond) {
    this.connectionTimeout = internetTimeoutSecond * 1000;
  }

  public Document getPageContent(String url) throws IOException {
    Connection connect = Jsoup.connect(url);
    connect.timeout(connectionTimeout);
    return connect.get();
  }

  public static String getHyperlink(Element element) {
    return element.getElementsByTag("a").get(0).absUrl("href");
  }
}
