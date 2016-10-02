package com.chen.guo.crawler.source;

import java.io.IOException;

public interface ScrapingTask {
  void scrape(String ticker, String baseUrl) throws IOException;
}
