package com.chen.guo.common.Exception;

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

  public static void logExceptionLong(Logger logger, Exception e) {
    logger.error(getStackTrace(e));
  }

  public static void logExceptionLong(Logger logger, Exception e, String extraMsg) {
    logger.error(extraMsg + System.lineSeparator() + getStackTrace(e));
  }

  public static void logExceptionShort(Logger logger, Exception e) {
    logger.error(e.getMessage());
  }

  public static void logExceptionShort(Logger logger, Exception e, String extraMsg) {
    logger.error(extraMsg + System.lineSeparator() + e.getMessage());
  }
}
