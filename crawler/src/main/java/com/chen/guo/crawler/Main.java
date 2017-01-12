package com.chen.guo.crawler;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.FundamentalScrapingTask;
import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.source.cfi.task.CfiScrapingNetIncomeTaskHistorical;
import com.chen.guo.crawler.source.cfi.task.CfiScrapingNetIncomeTaskLatest;
import com.chen.guo.crawler.source.cfi.task.CfiScrapingQuoteTask;
import com.chen.guo.crawler.util.CrawlerConfigUtil;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;

import java.net.ConnectException;
import java.util.Arrays;

public class Main {
  public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ConnectException {
    ConfigList jobs = CrawlerConfig.getConfig().getList("job");
    ConfigObject firstJob = (ConfigObject) jobs.get(0);
    String scraperClass = (String) firstJob.get("scraper").unwrapped();
    String taskClass = (String) firstJob.get("task").unwrapped();

    Scraper scraper = (Scraper) Class.forName(scraperClass).newInstance();

    //TODO: Refactor this block: move constructor arguments to config objects.
    FundamentalScrapingTask task = null;
    if (taskClass.equals("com.chen.guo.crawler.source.cfi.task.CfiScrapingNetIncomeTaskHistorical")) {
      task = new CfiScrapingNetIncomeTaskHistorical(CrawlerConfigUtil.getStartingYear());
    } else if (taskClass.equals("")) {
      task = new CfiScrapingNetIncomeTaskLatest();
    }
    // ======== Refactoring END ========

    scraper.doScraping(Arrays.asList(new StockWebPage("捷成股份", "300182", "http://quote.cfi.cn/300182.html")), task);

    System.out.println(task.getTaskResults().keySet());
  }
}
