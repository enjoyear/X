package com.chen.guo.util.fetcher

import java.lang.Double
import java.util

import com.chen.guo.crawler.model.StockWebPage
import com.chen.guo.crawler.source.Scraper
import com.chen.guo.crawler.source.cfi.{CfiScraper, CfiScrapingTaskHistoricalNPImpl}

import scala.collection.JavaConverters._
import scala.collection.mutable

object HistoricalDataFetcher extends App {
  var scraper: Scraper = _
  var allPages: mutable.Buffer[StockWebPage] = _
  var nameMap: Map[String, StockWebPage] = _
  var name2Code: Map[String, String] = _

  def init(): Unit = {
    scraper = new CfiScraper()
    allPages = scraper.getProfilePages.asScala
    println("Size of all stocks: " + allPages.size)
    nameMap = allPages.map(x => (x.getName, x)).toMap
    name2Code = allPages.map(x => (x.getName, x.getCode)).toMap
  }

  def getSingle(codeOrName: String): Option[util.TreeMap[Integer, Double]] = {
    if (scraper == null) {
      init()
    }

    codeToName(name2Code, codeOrName) match {
      case None => throw new RuntimeException(codeOrName + " doesn't exist. Double check your code.")
      case Some(name) => {
        val page: StockWebPage = nameMap.getOrElse(name, {
          println("Refresh cache for updated names")
          init()
          nameMap.getOrElse(name, null)
        })

        if (page == null) {
          throw new RuntimeException(name + " doesn't exist. Double check your name.")
        }

        val task = new CfiScrapingTaskHistoricalNPImpl(2013)
        scraper.doScraping(List(page).asJava, task)
        val scraped = task.getTaskResults.asScala
        scraped.get(name2Code(name))
      }
    }
  }

  def codeToName(map: Map[String, String], codeOrName: String): Option[String] = {
    val firstChar = codeOrName.charAt(0)
    if (firstChar >= '0' && firstChar <= '9') {
      map.get(codeOrName)
    }
    else {
      Some(codeOrName)
    }
  }
}
