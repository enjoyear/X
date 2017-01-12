package com.chen.guo.crawler.source;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FundamentalScrapingTask implements ScrapingTask, FundamentalDataTask {

  protected ConcurrentHashMap<String, TreeMap<Integer, Double>> results = new ConcurrentHashMap<>();

  @Override
  public Map<String, TreeMap<Integer, Double>> getTaskResults() {
    return results;
  }
}
