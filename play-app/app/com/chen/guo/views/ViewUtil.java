package com.chen.guo.views;

import java.text.DecimalFormat;
import java.util.*;

import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;

public class ViewUtil {
  public static String displayAmountsInMillions(Double number) {
    DecimalFormat formatter = new java.text.DecimalFormat("#,###.00");
    return formatter.format(number / 1000000);
  }

  public static String displayPercentages(Double number) {
    DecimalFormat formatter = new java.text.DecimalFormat("##.##%");
    return formatter.format(number - 1);
  }

  public static String calculateGeoAverage(TreeMap<Integer, TreeMap<String, Double>> percentages) {
    return calculateGeoAverage(percentages, 8);
  }

  public static String calculateGeoAverage(TreeMap<Integer, TreeMap<String, Double>> percentages, int limit) {
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
    double avg = new GeometricMean().evaluate(doubles);
    if (Double.isNaN(avg))
      return "NAN";
    return displayPercentages(avg);
  }
}
