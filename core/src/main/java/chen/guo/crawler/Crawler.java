package chen.guo.crawler;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Performance Comparison:
 * <p>
 * Preparation:
 * 1. Change Collections.synchronizedSet to Collections.synchronizedList
 * 2. visited should always return false.
 * <p>
 * <p>
 * Java 7's ForkJoinPool implementation is 1.5x times faster than the Java 6's ExecutorService.
 */
public abstract class Crawler implements LinkHandler {

  private final Collection<String> visitedLinks = Collections.synchronizedSet(new HashSet<>());
  private String url;

  public Crawler(String startingURL) {
    this.url = startingURL;
  }

  @Override
  public void queueLink(String link) throws Exception {
    startNewThread(link);
  }

  @Override
  public int size() {
    return visitedLinks.size();
  }

  @Override
  public void addVisited(String s) {
    System.out.println(String.format("%d: %s", size(), s));
    visitedLinks.add(s);
  }

  @Override
  public boolean visited(String s) {
    return visitedLinks.contains(s);
  }


  protected abstract void startNewThread(String link) throws Exception;

  protected void startCrawling() throws Exception {
    startNewThread(this.url);
  }

}
