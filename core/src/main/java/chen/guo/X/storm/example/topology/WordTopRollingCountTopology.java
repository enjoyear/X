package chen.guo.X.storm.example.topology;

import chen.guo.X.storm.example.bolt.IntermediateRankingsBolt;
import chen.guo.X.storm.example.bolt.RollingCountBolt;
import chen.guo.X.storm.example.bolt.TotalRankingsBolt;
import chen.guo.X.storm.example.spout.TestWordSpout;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class WordTopRollingCountTopology {

  public static StormTopology build() {
    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("WordSpoutId", new TestWordSpout(), 1);
    builder.setBolt("RollingCount", new RollingCountBolt(4, 1), 2)
      .fieldsGrouping("WordSpoutId", new Fields("word"));
    builder.setBolt("IntermediateRanker", new IntermediateRankingsBolt(2, 1), 2)
      .fieldsGrouping("RollingCount", new Fields("obj"));

    builder.setBolt("TotalRanker", new TotalRankingsBolt(3, 1)).globalGrouping("IntermediateRanker");
    return builder.createTopology();
  }
}
