package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CfiScrapingTask implements ScrapingTask {

  protected ConcurrentHashMap<String, TreeMap<Integer, Double>> results = new ConcurrentHashMap<>();

  protected Element getMainTable(String baseUrl) throws IOException {
    Document doc = WebAccessUtil.getInstance().getPageContent(baseUrl);
    Element content = doc.getElementById("content");
    return content.getElementsByTag("table").first().getElementsByTag("tbody").first();
  }

  @Override
  public Map<String, TreeMap<Integer, Double>> getTaskResults() {
    return results;
  }
}
