package com.chen.guo.crawler.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class CrawlerConfigUtilTest {
  @Test
  public void getYear() throws Exception {
    assertEquals(4, CrawlerConfigUtil.getYear());
  }

}