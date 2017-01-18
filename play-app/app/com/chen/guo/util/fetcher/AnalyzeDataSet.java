package com.chen.guo.util.fetcher;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

public class AnalyzeDataSet {
  public final static AnalyzeDataSet EMPTY = new AnalyzeDataSet(new TreeMap<>(), "", new TreeMap<>(), new TreeMap<>());
  private final TreeMap<Integer, TreeMap<String, Double>> _yearMonthAccMap;
  private final String _sourceUrl;
  private final TreeMap<Integer, TreeMap<String, Double>> _netIncomeMap;
  private final TreeMap<Integer, TreeMap<String, Double>> _netIncomeGrowthMap;
  private final TreeMap<String, String> _quoteMap;
  private final TreeMap<String, Pair<String, String>> _capMap;

  public AnalyzeDataSet(TreeMap<Integer, Double> netIncomeMap, String sourceUrl, TreeMap<String, String> quoteMap, TreeMap<String, Pair<String, String>> capMap) {
    _yearMonthAccMap = convertToYearMonthAccMap(netIncomeMap);
    _sourceUrl = sourceUrl;
    _netIncomeMap = doYearMonthDiff(_yearMonthAccMap);
    _netIncomeGrowthMap = createGrowthMap2(_netIncomeMap);
    _quoteMap = quoteMap;
    _capMap = capMap;
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

  /**
   * Compare to last year
   */
  private TreeMap<Integer, TreeMap<String, Double>> createGrowthMap1(TreeMap<Integer, TreeMap<String, Double>> netIncomeMap) {
    List<Triple<Integer, String, Double>> netIncomes = flattenTreeMap(netIncomeMap);

    List<Triple<Integer, String, Double>> growthList = new ArrayList<>();
    for (int i = 4; i < netIncomes.size(); ++i) {
      Triple<Integer, String, Double> current = netIncomes.get(i);
      growthList.add(Triple.of(current.getLeft(), current.getMiddle(), current.getRight() / netIncomes.get(i - 4).getRight()));
    }

    return convertToTreeMap(growthList);
  }

  private List<Triple<Integer, String, Double>> flattenTreeMap(TreeMap<Integer, TreeMap<String, Double>> netIncomeMap) {
    List<Triple<Integer, String, Double>> flattened = new ArrayList<>();
    for (Map.Entry<Integer, TreeMap<String, Double>> yearMap : netIncomeMap.entrySet()) {
      for (Map.Entry<String, Double> monthEntry : yearMap.getValue().entrySet()) {
        flattened.add(Triple.of(yearMap.getKey(), monthEntry.getKey(), monthEntry.getValue()));
      }
    }
    return flattened;
  }

  private TreeMap<Integer, TreeMap<String, Double>> convertToTreeMap(List<Triple<Integer, String, Double>> flattend) {
    TreeMap<Integer, TreeMap<String, Double>> ret = new TreeMap<>();
    for (Triple<Integer, String, Double> growth : flattend) {
      TreeMap<String, Double> monthMap = ret.computeIfAbsent(growth.getLeft(), k -> new TreeMap<>());
      monthMap.put(growth.getMiddle(), growth.getRight());
    }
    return ret;
  }

  /**
   * Compare trailing 4-quarters sum to last year
   */
  private TreeMap<Integer, TreeMap<String, Double>> createGrowthMap2(TreeMap<Integer, TreeMap<String, Double>> netIncomeMap) {
    List<Triple<Integer, String, Double>> netIncomes = flattenTreeMap(netIncomeMap);

    List<Triple<Integer, String, Double>> trailingSum = new ArrayList<>();
    if (netIncomes.size() >= 8) {
      Triple<Integer, String, Double> firstSum = netIncomes.get(3);
      trailingSum.add(Triple.of(firstSum.getLeft(), firstSum.getMiddle(), firstSum.getRight() + netIncomes.get(2).getRight() + netIncomes.get(1).getRight() + netIncomes.get(0).getRight()));
      for (int i = 4; i < netIncomes.size(); ++i) {
        Triple<Integer, String, Double> next = netIncomes.get(i);
        trailingSum.add(Triple.of(next.getLeft(), next.getMiddle(), next.getRight() + trailingSum.get(i - 4).getRight() - netIncomes.get(i - 4).getRight()));
      }
    } else {
      return new TreeMap<>();
    }

    List<Triple<Integer, String, Double>> growthList = new ArrayList<>();
    for (int i = 4; i < trailingSum.size(); ++i) {
      Triple<Integer, String, Double> current = trailingSum.get(i);
      growthList.add(Triple.of(current.getLeft(), current.getMiddle(), current.getRight() / trailingSum.get(i - 4).getRight()));
    }

    return convertToTreeMap(growthList);
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

  public TreeMap<String, String> get_quoteMap() {
    return _quoteMap;
  }

  public TreeMap<String, Pair<String, String>> get_capMap() {
    return _capMap;
  }
}


