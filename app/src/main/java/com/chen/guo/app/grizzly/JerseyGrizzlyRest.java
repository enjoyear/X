package com.chen.guo.app.grizzly;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class JerseyGrizzlyRest {
  // Base URI the Grizzly HTTP server will listen on
  public static final String BASE_URI = "http://localhost:8080/chen/";

  /**
   * Create and start a Grizzly HTTP server instance exposing JAX-RS resources
   *
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer() {
    // create a resource config that scans for JAX-RS resources and providers in chen.guo.example package
    final ResourceConfig rc = new ResourceConfig().packages("com/chen/guo/grizzly/rest");
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
  }

  public static void main(String[] args) throws IOException {
    final HttpServer server = startServer();
    System.out.println(String.format("Jersey app started with WADL available at "
      + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
    System.in.read();
    server.shutdownNow();
  }
}
