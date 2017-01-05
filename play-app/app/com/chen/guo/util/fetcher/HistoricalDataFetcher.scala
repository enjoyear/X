package com.chen.guo.util.fetcher

import java.io._
import java.lang.Double
import java.util

import com.chen.guo.crawler.model.StockWebPage
import com.chen.guo.crawler.source.Scraper
import com.chen.guo.crawler.source.cfi.{CfiScraper, CfiScrapingTaskHistoricalNPImpl}
import play.api.Logger

import scala.collection.JavaConverters._
import scala.util.control.Breaks._

/**
  * 1. Read serialization file upon start-up.
  * 2. If stock cannot be found. Update serialization file.
  */
object HistoricalDataFetcher {
  val SERIALIZATION_FILE = "/tmp/chen_x"

  var scraper: Scraper = new CfiScraper()
  var codeMap: Map[String, StockWebPage] = Map()
  var name2Code: Map[String, String] = Map()

  /**
    * @param readSerialization determines whether to read serialization file while getting all pages.
    */
  def init(readSerialization: Boolean): Unit = {
    val allPages = getPages(readSerialization)
    Logger.info("Size of all stocks: " + allPages.size)
    codeMap = allPages.map(x => (x.getCode, x)).toMap
    name2Code = allPages.map(x => (x.getName, x.getCode)).toMap
  }

  def getData(codeOrName: String): util.TreeMap[Integer, Double] = {
    if (codeMap.isEmpty) {
      init(true)
    }
    getData(codeOrName, false)
  }

  private def getData(codeOrName: String, fileAlreadyUpdated: Boolean): util.TreeMap[Integer, Double] = {
    val firstChar = codeOrName.charAt(0)
    val code = if (firstChar >= '0' && firstChar <= '9') {
      //Request by code
      codeOrName
    }
    else {
      //Request by name
      val code = name2Code.get(codeOrName)
      if (code.isEmpty) {
        if (fileAlreadyUpdated) {
          throw new RuntimeException(codeOrName + " doesn't exist. Double check your name.")
        }
        else {
          return reInitAndGet(codeOrName)
        }
      }
      else {
        code.get
      }
    }

    val page: StockWebPage = codeMap.getOrElse(code, {
      if (fileAlreadyUpdated) {
        throw new RuntimeException(code + " doesn't exist. Double check your code.")
      }
      else {
        return reInitAndGet(codeOrName)
      }
    })

    val task = new CfiScrapingTaskHistoricalNPImpl(2013)
    scraper.doScraping(List(page).asJava, task)
    val scraped = task.getTaskResults.asScala
    scraped(code)
  }

  private def reInitAndGet(codeOrName: String): util.TreeMap[Integer, Double] = {
    Logger.info("Cached stock pages seems to be outdated. Updating...")
    //TODO: No need to update for error input. Update at most once per day.
    init(false) //update serialization file
    getData(codeOrName, true)
  }

  /**
    * First try to restore from serialization file.
    * If not found, fetch new and write to serialization file.
    */
  private def getPages(readFile: Boolean): List[StockWebPage] = {
    if (readFile && new File(SERIALIZATION_FILE).exists()) {
      val ois = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE))
      val pagesRead = ois.readObject().asInstanceOf[List[StockWebPage]]
      ois.close()
      pagesRead
    }
    else {
      fetchAndWrite()
    }
  }

  private def fetchAndWrite(): List[StockWebPage] = {
    var updated: List[StockWebPage] = null
    breakable {
      while (updated == null) {
        try {
          Logger.info(s"Try getting all pages...")
          updated = scraper.getProfilePages.asScala.toList
        }
        catch {
          case e: Exception =>
            Thread.sleep(1000)
        }
      }
    }

    val oos = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE))
    oos.writeObject(updated)
    oos.close()
    updated
  }
}
