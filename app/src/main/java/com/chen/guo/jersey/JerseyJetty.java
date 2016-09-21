package com.chen.guo.jersey;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class JerseyJetty {

  private static Server createServer() {
    ResourceHandler rh = new ResourceHandler();
    rh.setResourceBase("app/src/main/webapp");

    ResourceConfig resourceConfig = new ResourceConfig();
    resourceConfig.register(MultiPartFeature.class);//This is needed for MediaType.MULTIPART_FORM_DATA
    resourceConfig.packages("com/chen/guo/jersey/rest");
    ServletHolder servlet = new ServletHolder(new ServletContainer(resourceConfig));

    Server server = new Server(8080);
    ServletContextHandler ch = new ServletContextHandler(server, "/*");
    ch.addServlet(servlet, "/*");
    ch.insertHandler(rh);

    return server;
  }

  public static void main(String[] args) {
    Server server = createServer();
    System.out.println("Jersey app started with WADL available at " +
      "http://localhost:8080/application.wadl\n");

    try {
      server.start();
      server.join();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      server.destroy();
    }
  }
}
