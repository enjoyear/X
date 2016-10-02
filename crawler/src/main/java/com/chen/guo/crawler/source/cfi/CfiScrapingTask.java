package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public abstract class CfiScrapingTask {
  abstract void scrape(String baseUrl) throws IOException;

  protected Element getMainTable(String baseUrl) throws IOException {
    Document doc = WebAccessUtil.getInstance().getPageContent(baseUrl);
    Element content = doc.getElementById("content");
    return content.getElementsByTag("table").first().getElementsByTag("tbody").first();
  }
}
