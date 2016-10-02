package com.chen.guo.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

public class ExceptionUtils {

  public static String getStackTrace(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return System.lineSeparator() + sw.toString();
  }

  public static void error(Logger logger, Exception e) {
    logger.error(getStackTrace(e));
  }

  public static void error(Logger logger, Exception e, String extraMsg) {
    logger.error(extraMsg + System.lineSeparator() + getStackTrace(e));
  }
}
