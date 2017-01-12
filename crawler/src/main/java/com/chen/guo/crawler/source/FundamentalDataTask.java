package com.chen.guo.crawler.source;

import java.util.Map;
import java.util.TreeMap;

public interface FundamentalDataTask {
  /**
   * The taskResults will collect all scraped data.
   *
   * @return Sticker -> (YearMonth -> Value)
   */
  Map<String, TreeMap<Integer, Double>> getTaskResults();
}
