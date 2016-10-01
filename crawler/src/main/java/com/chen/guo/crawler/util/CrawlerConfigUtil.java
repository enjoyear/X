package com.chen.guo.crawler.util;

import com.chen.guo.crawler.CrawlerConfig;

public class CrawlerConfigUtil {
  public static int getYear() {
    return CrawlerConfig.getConfig().getInt("source.cfi.number_of_years");
  }
}
