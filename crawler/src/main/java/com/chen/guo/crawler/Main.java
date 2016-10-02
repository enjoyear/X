package com.chen.guo.crawler;

import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.source.cfi.CfiScrapingTaskHistoricalNPImpl;
import com.chen.guo.crawler.source.cfi.CfiScrapingTaskLatestNPImpl;
import com.chen.guo.crawler.util.CrawlerConfigUtil;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;

public class Main {
  public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    ConfigList jobs = CrawlerConfig.getConfig().getList("job");
    ConfigObject firstJob = (ConfigObject) jobs.get(0);
    String scraperClass = (String) firstJob.get("scraper").unwrapped();
    String taskClass = (String) firstJob.get("task").unwrapped();

    Scraper scraper = (Scraper) Class.forName(scraperClass).newInstance();

    //TODO: Refactor this block: move constructor arguments to config objects.
    ScrapingTask task = null;
    if (taskClass.equals("com.chen.guo.crawler.source.cfi.CfiScrapingTaskHistoricalNPImpl")) {
      task = new CfiScrapingTaskHistoricalNPImpl(CrawlerConfigUtil.getStartingYear());
    } else if (taskClass.equals("")) {
      task = new CfiScrapingTaskLatestNPImpl();
    }
    // ======== Refactoring END ========

    scraper.doScraping(task);

  }
}
