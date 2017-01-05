package com.chen.guo.models.valuation

import java.util

import scala.collection.JavaConverters._
import scala.collection.immutable.TreeMap


case class AnalyzeDataSet(netIncome: util.TreeMap[Integer, java.lang.Double]) {
  val yearMonthMap = createYearMonthMap(netIncome)

  def createYearMonthMap(netIncome: util.TreeMap[Integer, java.lang.Double]): TreeMap[Int, TreeMap[String, Double]] = {
    var ret = TreeMap[Int, TreeMap[String, Double]]()

    for (kvp <- netIncome.entrySet().asScala) {
      val ym = kvp.getKey.toString
      val year = ym.substring(0, 4).toInt
      val month = ym.substring(4)

      ret.get(year) match {
        case None =>
          ret += (year -> TreeMap[String, Double](month -> kvp.getValue))
        case Some(monthMap) =>
          ret += (year -> (monthMap + (month -> kvp.getValue)))
      }
    }

    ret
  }
}
