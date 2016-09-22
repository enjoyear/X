package com.chen.guo;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import org.apache.log4j.Logger;

public class Seed {
  private static final Logger LOG = Logger.getLogger(Seed.class);

  public static void main(String[] args) {
//    Document doc = null;
//    try {
//      doc = Jsoup.connect("http://quote.cfi.cn/stockList.aspx").get();
//      Element content = doc.getElementById("content");
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    LOG.info("Hello");

  }
}
