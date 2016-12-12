package com.chen.guo.crawler.source;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public interface ScrapingTask {
  void scrape(String ticker, String baseUrl) throws IOException;

  /**
   * The taskResults will collect all scraped data.
   *
   * @return Sticker -> (YearMonth -> Value)
   */
  Map<String, TreeMap<Integer, Double>> getTaskResults();
}
