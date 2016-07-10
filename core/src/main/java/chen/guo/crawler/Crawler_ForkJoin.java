package chen.guo.crawler;

import java.util.concurrent.ForkJoinPool;

/**
 * ForkJoinPool Javadoc states that the number of threads must be a power of two!
 * <p>
 * The pool invokes a new LinkFinderAction, which will recursively invoke further ForkJoinTasks
 */
public class Crawler_ForkJoin extends Crawler {

  private ForkJoinPool mainPool;

  public Crawler_ForkJoin(String startingURL, int maxThreads) {
    super(startingURL);
    mainPool = new ForkJoinPool(maxThreads);
  }

  @Override
  protected void startNewThread(String link) throws Exception {
    mainPool.invoke(new LinkFinderAction(link, this));
  }

  public static void main(String[] args) throws Exception {
    new Crawler_ForkJoin("http://www.javaworld.com", 8).startCrawling();
  }
}
