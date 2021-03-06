package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CfiScrapingCapitalizationTaskHist extends ScrapingTask<String, Pair<String, String>> {
  private static final Logger logger = Logger.getLogger(CfiScrapingCapitalizationTaskHist.class);
  private static final String ROOT_URL = "http://quote.cfi.cn";
  private final int _startYear;
  private final CfiScrapingMenuTask _menuTask;

  /**
   * @param startYear denotes the oldest year we care about. This startYear is inclusive
   */
  public CfiScrapingCapitalizationTaskHist(int startYear) {
    _startYear = startYear;
    _menuTask = new CfiScrapingMenuTask("nodea21", "股本结构");
  }

  @Override
  public void scrape(StockWebPage stockWebPage) throws IOException {
    WebAccessUtil client = WebAccessUtil.getInstance();
    String url = _menuTask.getMenuPage(stockWebPage);
    Document doc = client.getPageContent(url);
    Element content = doc.getElementById("content");
    TreeMap<String, Pair<String, String>> capitalMap = new TreeMap<>(Comparator.reverseOrder());
    updateCapMap(content, capitalMap);

    Element funFunc = content.getElementsByTag("script").first();
    String script = funFunc.childNode(0).toString();
    String redirectPart = script.substring(script.indexOf("window.location="), script.indexOf("+sel.options"));
    String redirectUrl = redirectPart.substring(redirectPart.indexOf("'") + 1, redirectPart.lastIndexOf("'"));

    Element dropDown = doc.getElementById("sel");
    Elements options = dropDown.children();
    List<String> optionStrings = options.stream().map(x -> x.text()).collect(Collectors.toList());
    assert optionStrings.get(0).equals("最新");

    for (int i = 1; i < options.size(); ++i) {
      String year = optionStrings.get(i);
      if (Integer.valueOf(year) < _startYear) {
        break;
      }
      String newUrl = ROOT_URL + redirectUrl + year;
      Document newDoc = client.getPageContent(newUrl);
      Element newContent = newDoc.getElementById("content");
      updateCapMap(newContent, capitalMap);
    }

    results.put(stockWebPage.getCode(), capitalMap);
  }

  private void updateCapMap(Element table, TreeMap<String, Pair<String, String>> capMap) {
    Elements dates = table.getElementsContainingOwnText("截止日期").first().parent().children();
    Elements totalCapitals = table.getElementsContainingOwnText("总股本(股)").first().parent().parent().children();
    Elements reasons = table.getElementsContainingOwnText("股本变动原因说明").first().parent().parent().children();

    //Starting from 1 to skip first naming column
    for (int i = 1; i < dates.size(); ++i) {
      String date = dates.get(i).text();
      String capital = totalCapitals.get(i).text();
      if (capMap.isEmpty()) {
        capMap.put(date, Pair.of(capital, reasons.get(i).text()));
        continue;
      }

      Map.Entry<String, Pair<String, String>> lastChange = capMap.lastEntry();
      String lastDate = lastChange.getKey();
      if (date.compareTo(lastDate) < 0) {
        //update the last change date if capitalization equals.
        Pair<String, String> lastCap = lastChange.getValue();
        if (capital.equals(lastCap.getLeft())) {
          capMap.remove(lastDate);
          capMap.put(date, Pair.of(capital, reasons.get(i).text()));
        } else if (!capital.equals(lastCap.getLeft())) {
          capMap.put(date, Pair.of(capital, reasons.get(i).text()));
        }
      }
    }
  }
}
