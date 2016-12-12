package com.chen.guo.models.crawl

import com.chen.guo.crawler.source.Scraper
import com.chen.guo.crawler.source.cfi.CfiScraper
import collection.JavaConverters._

object HistoricalDataFetcher extends App {
  val scraper: Scraper = new CfiScraper()
  val allPages = scraper.getProfilePages.asScala
  println("Size of all stocks: " + allPages.size)

  val codeMap = allPages.map(x => (x.getCode, x.getUrl)).toMap
  val nameMap = allPages.map(x => (x.getName, x.getUrl)).toMap


}
