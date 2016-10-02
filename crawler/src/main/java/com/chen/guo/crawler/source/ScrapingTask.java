package com.chen.guo.crawler.source;

import java.io.IOException;

public interface ScrapingTask {
  void scrape(String baseUrl) throws IOException;
}
