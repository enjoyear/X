package com.chen.guo.views;

import java.text.DecimalFormat;
import java.util.*;

import com.chen.guo.crawler.source.cfi.task.CfiScrapingQuoteTask;
import com.chen.guo.util.fetcher.AnalyzeDataSet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;

public class ViewUtil {
  public static String displayAmountsInMillions(Double number) {
    if (Double.isNaN(number))
      return "NAN";
    DecimalFormat formatter = new java.text.DecimalFormat("#,###.00");
    return formatter.format(number / 1000000);
  }

  public static String displayPercentages(Double number) {
    if (Double.isNaN(number))
      return "NAN";
    DecimalFormat formatter = new java.text.DecimalFormat("##.##%");
    return formatter.format(number - 1);
  }

  public static String displayDoubles(Double number) {
    if (Double.isNaN(number))
      return "NAN";
    DecimalFormat formatter = new java.text.DecimalFormat("#.###");
    return formatter.format(number);
  }

  public static double calculateGeoAverage(TreeMap<Integer, TreeMap<String, Double>> percentages) {
    return calculateGeoAverage(percentages, 8);
  }

  public static double calculateGeoAverage(TreeMap<Integer, TreeMap<String, Double>> percentages, int limit) {
    List<Double> numbers = new ArrayList<>(limit);

    NavigableMap<Integer, TreeMap<String, Double>> yearMap = percentages.descendingMap();
    Iterator<Map.Entry<Integer, TreeMap<String, Double>>> yearIter = yearMap.entrySet().iterator();
    while (yearIter.hasNext()) {
      NavigableMap<String, Double> monthMap = yearIter.next().getValue().descendingMap();
      Iterator<Map.Entry<String, Double>> monthIter = monthMap.entrySet().iterator();
      while (monthIter.hasNext() && limit-- > 0) {
        numbers.add(monthIter.next().getValue());
      }

      if (limit <= 0) {
        break;
      }
    }

    int size = numbers.size();
    double[] doubles = new double[size];
    for (int i = 0; i < size; ++i) {
      doubles[i] = numbers.get(i);
    }
    return new GeometricMean().evaluate(doubles);
  }

  public static double[] calculateScore(AnalyzeDataSet dataSet, double growth) {
    TreeMap<String, String> quoteMap = dataSet.get_quoteMap();
    String cap = dataSet.get_capMap().firstEntry().getValue().getLeft();

    String quoteWithArrow = quoteMap.get(CfiScrapingQuoteTask.LAST_QUOTE);
    char lastChar = quoteWithArrow.charAt(quoteWithArrow.length() - 1);
    double price = 0;
    if (lastChar >= '0' && lastChar <= '9') {
      price = Double.parseDouble(quoteWithArrow);
    } else {
      price = Double.parseDouble(quoteWithArrow.substring(0, quoteWithArrow.length() - 1));
    }


    List<Triple<Integer, String, Double>> netIncomes = AnalyzeDataSet.flattenTreeMap(dataSet.get_netIncomeMap());
    int len = netIncomes.size();
    Double last4Sum = netIncomes.get(len - 1).getRight() + netIncomes.get(len - 2).getRight() + netIncomes.get(len - 3).getRight() + netIncomes.get(len - 4).getRight();

    double capital = Double.parseDouble(cap);

    double eps = last4Sum / capital;
    double trailingPE = price / eps;
    double score = trailingPE / ((growth - 1) * 100);
    return new double[]{eps, trailingPE, score};
  }
}
