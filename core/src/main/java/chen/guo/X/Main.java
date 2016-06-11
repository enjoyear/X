package chen.guo.X;

import chen.guo.X.common.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {

    logger.info("Hello World");
    logger.info(Test.NAME);
  }
}
