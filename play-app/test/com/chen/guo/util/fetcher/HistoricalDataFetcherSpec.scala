package com.chen.guo.util.fetcher

import org.scalatest.FlatSpec

class HistoricalDataFetcherSpec extends FlatSpec {
  "codeToName" should "convert code to name" in {
    val name = HistoricalDataFetcher.codeToName(Map("123" -> "Hi"), "123").get
    assert(name == "Hi")
  }

  "codeToName" should "keep name" in {
    val name = HistoricalDataFetcher.codeToName(Map("123" -> "Hi"), "AA").get
    assert(name == "AA")
  }
}
