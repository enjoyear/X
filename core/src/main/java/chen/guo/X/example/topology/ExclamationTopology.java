package chen.guo.X.example.topology;

import chen.guo.X.example.bolt.ExclamationBolt;
import chen.guo.X.example.spout.TestWordSpout;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

public class ExclamationTopology {

  public static StormTopology build() {
    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("WordSpoutId", new TestWordSpout(), 2);
    builder.setBolt("ExclaimBolt1", new ExclamationBolt(), 1).shuffleGrouping("WordSpoutId");
    builder.setBolt("ExclaimBolt2", new ExclamationBolt(), 2).shuffleGrouping("ExclaimBolt1");

    return builder.createTopology();
  }
}
