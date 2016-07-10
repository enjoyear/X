package chen.guo.crawler;

public interface LinkHandler {

  /**
   * Places the link in the queue
   */
  void queueLink(String link) throws Exception;

  /**
   * Returns the number of visited links
   */
  int size();

  /**
   * Checks if the link was already visited
   */
  boolean visited(String link);

  /**
   * Marks this link as visited
   */
  void addVisited(String link);
}
