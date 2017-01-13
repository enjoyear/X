package com.chen.guo.util.fetcher;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.cfi.CfiScraper;
import com.chen.guo.crawler.source.cfi.task.CfiScrapingNetIncomeTaskHistorical;
import play.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HistoricalDataFetcher {
  public static final String SERIALIZATION_FILE = "/tmp/chen_x";
  private Scraper scraper = new CfiScraper();
  private Map<String, StockWebPage> codeMap;
  private Map<String, String> name2Code;

  public HistoricalDataFetcher() throws InterruptedException, IOException, ClassNotFoundException {
    init(true);
  }

  /**
   * @param readSerialization determines whether to read serialization file while getting all pages.
   */
  private void init(boolean readSerialization) throws IOException, ClassNotFoundException, InterruptedException {
    List<StockWebPage> allPages = getPages(readSerialization);
    Logger.info("Size of all stocks: " + allPages.size());
    codeMap = allPages.stream().collect(Collectors.toMap(
        page -> page.getCode(),
        page -> page,
        (page1, page2) -> {
          Logger.warn(String.format("codeMap: duplicate key found with different values: %s, %s", page1, page2));
          return page1;
        }));
    name2Code = allPages.stream().collect(Collectors.toMap(
        page -> page.getName(),
        page -> page.getCode(),
        (code1, code2) -> {
          Logger.warn(String.format("name2Code: duplicate key found with different values: %s, %s", code1, code2));
          return code1;
        }
    ));
  }

  public AnalyzeDataSet getData(String codeOrName) throws IOException, ClassNotFoundException, InterruptedException {
    return getData(codeOrName, false);
  }

  private AnalyzeDataSet getData(String codeOrName, boolean fileAlreadyUpdated) throws IOException, InterruptedException, ClassNotFoundException {
    char firstChar = codeOrName.charAt(0);
    String code;
    if (firstChar >= '0' && firstChar <= '9') {
      //Request by code
      code = codeOrName;
    } else {
      //Request by name
      code = name2Code.get(codeOrName);
      if (code == null) {
        if (fileAlreadyUpdated) {
          throw new RuntimeException(codeOrName + " doesn't exist. Double check your name.");
        } else {
          return reInitAndGet(codeOrName);
        }
      }
    }

    StockWebPage page = codeMap.get(code);
    if (page == null) {
      if (fileAlreadyUpdated) {
        throw new RuntimeException(code + " doesn't exist. Double check your code.");
      } else {
        return reInitAndGet(codeOrName);
      }
    }

    CfiScrapingNetIncomeTaskHistorical task = new CfiScrapingNetIncomeTaskHistorical(2013);
    scraper.doScraping(Arrays.asList(page), task);
    Map<String, TreeMap<Integer, Double>> scraped = task.getTaskResults();
    return new AnalyzeDataSet(scraped.get(code), page.getUrl());
  }

  private AnalyzeDataSet reInitAndGet(String codeOrName) throws InterruptedException, IOException, ClassNotFoundException {
    Logger.info("Cached stock pages seems to be outdated. Updating...");
    //TODO: No need to update for error input. Update at most once per day.
    init(false); //update serialization file
    return getData(codeOrName, true);
  }

  /**
   * First try to restore from serialization file.
   * If not found, fetch new and write to serialization file.
   */
  private List<StockWebPage> getPages(boolean readFile) throws IOException, ClassNotFoundException, InterruptedException {
    if (readFile && new File(SERIALIZATION_FILE).exists()) {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE));
      List<StockWebPage> pagesRead = (List<StockWebPage>) ois.readObject();
      ois.close();
      return pagesRead;
    } else {
      return fetchAndWriteSerialization();
    }
  }

  private List<StockWebPage> fetchAndWriteSerialization() throws IOException, InterruptedException {
    List<StockWebPage> updated = null;
    while (updated == null) {
      try {
        Logger.info("Try getting all pages...");
        updated = scraper.getProfilePages();
      } catch (Exception e) {
        Thread.sleep(1000);
      }
    }
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE));
    oos.writeObject(updated);
    oos.close();
    return updated;
  }
}
