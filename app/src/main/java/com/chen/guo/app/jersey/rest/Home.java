package com.chen.guo.app.jersey.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("home")
public class Home {
  @GET
  @Path("hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String helloWorld() {
    return "Hello, world!";
  }
}
