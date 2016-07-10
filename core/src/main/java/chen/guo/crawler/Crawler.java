package chen.guo.crawler;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
