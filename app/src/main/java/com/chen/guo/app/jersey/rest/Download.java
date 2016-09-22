package com.chen.guo.app.jersey.rest;

import com.chen.guo.app.jersey.service.DownloadService;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Path("home")
public class Download {

  private DownloadService downloadService = new DownloadService();

  @GET
  @Path("download")
  @Produces(MediaType.TEXT_HTML)
  public InputStream doGet() throws FileNotFoundException {
    //http://localhost:8080/home/download
    System.out.println("here");
    File f = new File("app/src/main/webapp/WEB-INF/html/download.html");
    return new FileInputStream(f);
  }

  @POST
  @Path("download")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response uploadDownload(@FormDataParam("content") String content,
                                 @FormDataParam("option") String option,
                                 @FormDataParam("fileStream") InputStream fileStream,
                                 @FormDataParam("checkbox") String checkBox) {
    //curl --data "content=hi,there" localhost:8080/home/download
    System.out.println("downloading...");
    StreamingOutput stream = os -> {
      StringBuilder sb = new StringBuilder();
      sb.append("Content:,").append(content).append("\n");
      sb.append("Option:,").append(option).append("\n");
      sb.append("CheckBox:,").append(checkBox).append("\n"); //returns "on" or null

      final int bufferSize = 1024;
      final char[] buffer = new char[bufferSize];
      final StringBuilder out = new StringBuilder();
      Reader reader = new InputStreamReader(fileStream, "UTF-8");
      while (true) {
        int size = reader.read(buffer, 0, buffer.length);
        if (size < 0)
          break;
        out.append(buffer, 0, size);
      }

      sb.append("Uploaded File:,").append(out.toString()).append("\n");
      downloadService.download(os, sb.toString());
    };
    //String mediaType = download != null ? "text/csv" : MediaType.TEXT_PLAIN;
    return Response.ok(stream, "text/csv").build();
  }
}
