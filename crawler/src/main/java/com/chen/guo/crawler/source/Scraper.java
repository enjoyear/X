package com.chen.guo.crawler.source;

import com.chen.guo.crawler.model.StockWebPage;

import java.io.IOException;
import java.util.List;

public interface Scraper {
  void doScraping(ScrapingTask scrapingTask);

  List<StockWebPage> getProfilePages() throws IOException;
}
