package com.chen.guo.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
  public static String getStackTrace(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return System.lineSeparator() + sw.toString();
  }
}
