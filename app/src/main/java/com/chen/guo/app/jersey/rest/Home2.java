package com.chen.guo.app.jersey.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("home")
public class Home2 {

  @GET
  @Path("get-param")
  @Produces(MediaType.TEXT_PLAIN)
  public String paramMethod(@QueryParam("name") String name) {
    //http://localhost:8080/home/get-param?name=me
    return "Get - " + name;
  }

  @POST
  @Path("post-param")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_HTML)
  public String postMethod(@FormParam("name") String name) {
    //http://localhost:8080/
    //curl --data "name=abc" localhost:8080/home/post-param
    System.out.println("In home/post-param");
    return "<h2>Hello, " + name + "</h2>";
  }

  @POST
  @Path("post-param-2")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_HTML)
  public String postMethod2(@FormDataParam("name") String name) {
    //http://localhost:8080/home/post-param-2
    //curl --form "name=abc" localhost:8080/home/post-param-2
    System.out.println("In home/post-param-2");
    return "<h2>Hello-2, " + name + "</h2>";
  }
}
