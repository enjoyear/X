package com.chen.guo;


import com.chen.guo.common.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import org.apache.log4j.Logger;

public class Seed {
  private static final Logger LOG = Logger.getLogger(Seed.class);

  public static void main(String[] args) {

    try {
      Document doc = Jsoup.connect("http://quote.cfi.cn/stockList.aspx11").get();
      //Element content = doc.getElementById("content");

    } catch (IOException e) {
      LOG.error(ExceptionUtils.getStackTrace(e));
    }
  }
}
