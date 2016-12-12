package com.chen.guo.models.crawl

import com.chen.guo.crawler.source.Scraper
import com.chen.guo.crawler.source.cfi.{CfiScraper, CfiScrapingTaskHistoricalNPImpl}

import scala.collection.JavaConverters._

object HistoricalDataFetcher extends App {
  val scraper: Scraper = new CfiScraper()
  val allPages = scraper.getProfilePages.asScala
  println("Size of all stocks: " + allPages.size)

  val nameMap = allPages.map(x => (x.getName, x)).toMap
  val name2Code = allPages.map(x => (x.getName, x.getCode)).toMap

  val stockName = "捷成股份"
  val page = nameMap(stockName)
  val task = new CfiScrapingTaskHistoricalNPImpl(2013)
  scraper.doScraping(List(page).asJava, task)
  val scraped = task.getTaskResults.asScala
  println(scraped.get(name2Code(stockName)))
}
