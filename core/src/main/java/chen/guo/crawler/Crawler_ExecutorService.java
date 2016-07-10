package chen.guo.crawler;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler_ExecutorService extends Crawler {

  private ExecutorService execService;

  public Crawler_ExecutorService(String startingURL, int maxThreads) {
    super(startingURL);
    execService = Executors.newFixedThreadPool(maxThreads);
  }

  @Override
  protected void startNewThread(String link) throws Exception {
    execService.execute(new LinkFinder(link, this));
  }

  public static void main(String[] args) throws Exception {
    new Crawler_ExecutorService("http://www.javaworld.com", 8).startCrawling();
  }

}
