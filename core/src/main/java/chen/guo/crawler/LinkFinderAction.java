package chen.guo.crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;


public class LinkFinderAction extends RecursiveAction {

  private String url;
  private LinkHandler linkHandler;
  private static final long t0 = System.currentTimeMillis();

  public LinkFinderAction(String url, LinkHandler handler) {
    this.url = url;
    this.linkHandler = handler;
  }

  @Override
  public void compute() {
    if (!linkHandler.visited(url)) {
      try {
        List<RecursiveAction> actions = new ArrayList<>();
        URL uriLink = new URL(url);
        Parser parser = new Parser(uriLink.openConnection());
        NodeList childrenLinks = parser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));

        for (int i = 0; i < childrenLinks.size(); i++) {
          LinkTag link = (LinkTag) childrenLinks.elementAt(i);
          if (!link.extractLink().isEmpty() &&
            !linkHandler.visited(link.extractLink())) {
            actions.add(new LinkFinderAction(link.extractLink(), linkHandler));
          }
        }
        linkHandler.addVisited(url);

        if (linkHandler.size() == 1000) {
          System.out.println(String.format(
            "Time to visit 1000 distinct links = %ds",
            (System.currentTimeMillis() - t0) / 1000));
          System.exit(0);
        }

        //invoke recursively
        invokeAll(actions);
      } catch (Exception e) {
        //ignore 404, unknown protocol or other server errors
      }
    }
  }
}
