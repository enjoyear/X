package chen.guo.X;

import chen.guo.X.example.topology.ExclamationTopology;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.utils.Utils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Main {
  //private static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Config conf = new Config();
    conf.setDebug(true);

    LocalCluster cluster = new LocalCluster();
    cluster.submitTopology("ExclamationTopology", conf, ExclamationTopology.build());
    Utils.sleep(10000);
    cluster.killTopology("ExclamationTopology");
    cluster.shutdown();
  }
}
