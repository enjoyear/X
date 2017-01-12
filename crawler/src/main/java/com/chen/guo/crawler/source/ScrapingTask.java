package com.chen.guo.crawler.source;

import com.chen.guo.crawler.model.StockWebPage;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ScrapingTask<TKey, TValue> {
  protected ConcurrentHashMap<String, TreeMap<TKey, TValue>> results = new ConcurrentHashMap<>();

  public abstract void scrape(StockWebPage stockWebPage) throws IOException;

  public Map<String, TreeMap<TKey, TValue>> getTaskResults() {
    return results;
  }
}
