package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.util.WebAccessUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CfiScrapingTaskHistoricalNPImpl extends CfiScrapingTask {
  private static final Logger logger = Logger.getLogger(CfiScrapingTaskHistoricalNPImpl.class);
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(("yyyy-MM-dd"));
  private final int startYear;

  /**
   * @param startYear denotes the oldest year we care about. This startYear is inclusive
   */
  public CfiScrapingTaskHistoricalNPImpl(int startYear) {
    this.startYear = startYear;
  }

  @Override
  public void scrape(String ticker, String baseUrl) throws IOException {
    logger.info("Scraping page: " + baseUrl);
    Element netProfitTr = getMainTable(baseUrl).getElementsContainingOwnText("归属母公司净利润").first();
    String npPage = netProfitTr.absUrl("href");
    Document netProfitPage = WebAccessUtil.getInstance().getPageContent(npPage);
    //Get all historical
    Elements rows = netProfitPage.getElementById("content").getElementsByTag("tbody").first().children();

    Element header = rows.get(1);
    if (!header.child(0).text().equals("报告期") || !header.child(1).text().equals("归属母公司净利润（元）"))
      throw new UnexpectedException("Layout of the table seem to be changed for: " + npPage);
    //Skip first two header rows.
    for (int r = 2; r < rows.size(); ++r) {
      Element row = rows.get(r);
      Elements children = row.children();
      LocalDate date = getDate(children.get(0).text());
      if (date.getYear() >= startYear) {
        System.out.print(ticker);
        System.out.print("\t");
        System.out.print(date.getYear() * 100 + date.getMonthValue());
        System.out.print("\t");
        System.out.print(children.get(1).text());
        System.out.println();
      } else
        break;
    }
  }

  private static LocalDate getDate(String dateString) {
    return LocalDate.parse(dateString, formatter);
  }

}
