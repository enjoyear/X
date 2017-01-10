package com.chen.guo.util.fetcher;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class AnalyzeDataSet {
  public final static AnalyzeDataSet EMPTY = new AnalyzeDataSet(new TreeMap<>(), "");
  private final TreeMap<Integer, TreeMap<String, Double>> _yearMonthAccMap;
  private final String _sourceUrl;
  private final TreeMap<Integer, TreeMap<String, Double>> _netIncomeMap;
  private final TreeMap<Integer, TreeMap<String, Double>> _netIncomeGrowthMap;

  public AnalyzeDataSet(TreeMap<Integer, Double> netIncome, String sourceUrl) {
    _yearMonthAccMap = convertToYearMonthAccMap(netIncome);
    _sourceUrl = sourceUrl;
    _netIncomeMap = doYearMonthDiff(_yearMonthAccMap);
    _netIncomeGrowthMap = createGrowthMap(_netIncomeMap);
  }

  private static TreeMap<Integer, TreeMap<String, Double>> convertToYearMonthAccMap(TreeMap<Integer, Double> yearMonthMap) {
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

  private TreeMap<Integer, TreeMap<String, Double>> createGrowthMap(TreeMap<Integer, TreeMap<String, Double>> netIncomeMap) {
    TreeMap<Integer, TreeMap<String, Double>> ret = new TreeMap<>();

    Iterator<Map.Entry<Integer, TreeMap<String, Double>>> iterator = netIncomeMap.entrySet().iterator();

    Map.Entry<Integer, TreeMap<String, Double>> prev = null;
    if (iterator.hasNext()) {
      prev = iterator.next();
    }

    while (iterator.hasNext()) {
      Map.Entry<Integer, TreeMap<String, Double>> current = iterator.next();
      TreeMap<String, Double> growthMap = new TreeMap<>();
      ret.put(current.getKey(), growthMap);
      for (Map.Entry<String, Double> monthNetIncome : current.getValue().entrySet()) {
        String month = monthNetIncome.getKey();
        growthMap.put(month, monthNetIncome.getValue() / prev.getValue().get(month));
      }
      prev = current;
    }

    return ret;
  }

  public TreeMap<Integer, TreeMap<String, Double>> get_yearMonthAccMap() {
    return _yearMonthAccMap;
  }

  public String get_sourceUrl() {
    return _sourceUrl;
  }

  public TreeMap<Integer, TreeMap<String, Double>> get_netIncomeMap() {
    return _netIncomeMap;
  }

  public TreeMap<Integer, TreeMap<String, Double>> get_netIncomeGrowthMap() {
    return _netIncomeGrowthMap;
  }
}


