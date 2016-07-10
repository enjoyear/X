package chen.guo.crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;


public class LinkFinder implements Runnable {

  private String url;
  private LinkHandler linkHandler;
  private static final long t0 = System.currentTimeMillis();

  public LinkFinder(String url, LinkHandler handler) {
    this.url = url;
    this.linkHandler = handler;
  }

  @Override
  public void run() {
    getSimpleLinks(url);
  }

  private void getSimpleLinks(String url) {
    //if not already visited
    if (!linkHandler.visited(url)) {
      try {
        URL uriLink = new URL(url);
        Parser parser = new Parser(uriLink.openConnection());
        NodeList childrenLinks = parser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
        List<String> urls = new ArrayList<>();

        for (int i = 0; i < childrenLinks.size(); i++) {
          LinkTag link = (LinkTag) childrenLinks.elementAt(i);
          if (!link.getLink().isEmpty() &&
            !linkHandler.visited(link.getLink())) {
            urls.add(link.getLink());
          }
        }
        linkHandler.addVisited(url);

        if (linkHandler.size() == 1000) {
          System.out.println(String.format(
            "Time to visit 1000 distinct links = %ds",
            (System.currentTimeMillis() - t0) / 1000));
          System.exit(0);
        }

        for (String l : urls) {
          //from each url, start the new job
          linkHandler.queueLink(l);
        }

      } catch (Exception e) {
      }
    }
  }
}
