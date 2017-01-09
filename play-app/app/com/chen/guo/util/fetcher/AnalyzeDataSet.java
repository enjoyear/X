package com.chen.guo.util.fetcher;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class AnalyzeDataSet {
  public final static AnalyzeDataSet EMPTY = new AnalyzeDataSet(new TreeMap<>(), "");
  private final TreeMap<Integer, TreeMap<String, Double>> _yearMonthAccMap;
  private final String _sourceUrl;
  private final TreeMap<Integer, TreeMap<String, Double>> _yearMonthMap;

  public AnalyzeDataSet(TreeMap<Integer, Double> netIncome, String sourceUrl) {
    _yearMonthAccMap = createYearMonthAccMap(netIncome);
    _sourceUrl = sourceUrl;
    _yearMonthMap = doYearMonthDiff(_yearMonthAccMap);
  }

  private static TreeMap<Integer, TreeMap<String, Double>> createYearMonthAccMap(TreeMap<Integer, Double> yearMonthMap) {
    TreeMap<Integer, TreeMap<String, Double>> ret = new TreeMap<>();

    yearMonthMap.entrySet().forEach(kvp -> {
      String ym = kvp.getKey().toString();
      Integer year = Integer.parseInt(ym.substring(0, 4));
      String month = ym.substring(4);

      TreeMap<String, Double> yearMap = ret.computeIfAbsent(year, k -> new TreeMap<>());
      yearMap.put(month, kvp.getValue());
    });

    return ret;
  }

  private static TreeMap<Integer, TreeMap<String, Double>> doYearMonthDiff(TreeMap<Integer, TreeMap<String, Double>> yearMonthAccMap) {
    TreeMap<Integer, TreeMap<String, Double>> ret = new TreeMap<>();
    yearMonthAccMap.entrySet().forEach(kvp -> {
      TreeMap<String, Double> quarterMap = new TreeMap<>();

      TreeMap<String, Double> accMap = kvp.getValue();
      Iterator<Map.Entry<String, Double>> iterator = accMap.entrySet().iterator();
      double prev = 0.0;
      while (iterator.hasNext()) {
        Map.Entry<String, Double> current = iterator.next();
        quarterMap.put(current.getKey(), current.getValue() - prev);
        prev = current.getValue();
      }

      ret.put(kvp.getKey(), quarterMap);
    });

    return ret;
  }

  public TreeMap<Integer, TreeMap<String, Double>> getYearMonthAccMap() {
    return _yearMonthAccMap;
  }

  public TreeMap<Integer, TreeMap<String, Double>> getYearMonthMap() {
    return _yearMonthMap;
  }

  public String getSourceUrl() {
    return _sourceUrl;
  }
}


