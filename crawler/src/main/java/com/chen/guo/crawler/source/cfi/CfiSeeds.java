package com.chen.guo.crawler.source.cfi;


import com.chen.guo.common.Exception.ExceptionUtils;
import com.chen.guo.crawler.StockWebPage;
import com.chen.guo.crawler.util.WebPageUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public class CfiSeeds {
  private static final Logger logger = Logger.getLogger(CfiSeeds.class);


  public static void main(String[] args) {

    try {
      List<StockWebPage> quoteBaseUrls = getBasePages();
      Element pageFoundamentalIndicators = WebPageUtil.INSTANCE.getPageContent(
          quoteBaseUrls.get(0).getUrl()).getElementById("nodea1");
      Element nonbreakableFI = pageFoundamentalIndicators.getElementsByTag("nobr").first();
      if (!"财务分析指标".equals(nonbreakableFI.text()))
        throw new UnexpectedException("Didn't get the correct page for 财务分析指标");
      System.out.println(WebPageUtil.INSTANCE.getHyperlink(nonbreakableFI));
      //quoteBaseUrls.forEach(System.out::println);
    } catch (IOException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
    }
  }

  private static List<StockWebPage> getBasePages() throws IOException {
    Document listPage = WebPageUtil.INSTANCE.getPageContent("http://quote.cfi.cn/stockList.aspx");
    Element content = listPage.getElementById("divcontent");
    Element table = content.getElementsByTag("table").first().getElementsByTag("tbody").first();
    Elements rows = table.getElementsByTag("tr");

    List<StockWebPage> interestedPages = new ArrayList<>(4000);
    for (Element row : rows) {
      for (Element col : row.children()) {
        String nameCode = col.text();
        int index = nameCode.indexOf("(");
        StockWebPage sp = new StockWebPage(nameCode.substring(0, index).trim(),
            nameCode.substring(index + 1, nameCode.length() - 1).trim(),
            WebPageUtil.INSTANCE.getHyperlink(col));
        String code = sp.getCode();
        if (code.startsWith("0") || code.startsWith("6") || code.startsWith("3")) {
          interestedPages.add(sp);
          if (code.length() != 6) {
            throw new UnexpectedException(String.format("Unexpected length of code starting with 0,3,6. The code is %s", sp));
          }
        }
      }
    }
    return interestedPages;
  }
}
