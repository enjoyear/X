package com.chen.guo.crawler.source;

import com.chen.guo.crawler.model.StockWebPage;

import java.io.IOException;

public interface ScrapingTask {
  void scrape(StockWebPage stockWebPage) throws IOException;
}
