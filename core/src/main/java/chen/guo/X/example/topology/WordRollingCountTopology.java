package chen.guo.X.example.topology;

import chen.guo.X.example.bolt.RollingCountBolt;
import chen.guo.X.example.spout.TestWordSpout;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class WordRollingCountTopology {

  public static StormTopology build() {
    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("WordSpoutId", new TestWordSpout(), 1);
    builder.setBolt("RollingCount", new RollingCountBolt(5, 2), 2)
      .fieldsGrouping("WordSpoutId", new Fields("word"));

    return builder.createTopology();
  }
}
