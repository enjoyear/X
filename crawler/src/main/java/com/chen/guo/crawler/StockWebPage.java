package com.chen.guo.crawler;

public class StockWebPage {
  private final String name;
  private final String code;
  private final String url;

  public StockWebPage(String name, String code, String url) {

    this.name = name;
    this.code = code;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return "StockWebPage{" +
        "name='" + name + '\'' +
        ", code='" + code + '\'' +
        ", url='" + url + '\'' +
        '}';
  }
}
