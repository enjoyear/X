package chen.guo.X.storm.example.topology;

import chen.guo.X.storm.example.bolt.SingleJoinBolt;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.testing.FeederSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

public class SingleJoinTopology {
  public static FeederSpout genderSpout;
  public static FeederSpout ageSpout;

  public static StormTopology build() {
    genderSpout = new FeederSpout(new Fields("id", "gender"));
    ageSpout = new FeederSpout(new Fields("id", "age"));

    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("gender", genderSpout);
    builder.setSpout("age", ageSpout);
    builder.setBolt("join", new SingleJoinBolt(new Fields("gender", "age")))
      .fieldsGrouping("gender", new Fields("id"))
      .fieldsGrouping("age", new Fields("id"));

    return builder.createTopology();
  }

  public static void feed() {
    for (int i = 0; i < 50; i++) {
      String gender;
      if (i % 2 == 0) {
        gender = "male";
      } else {
        gender = "female";
      }
      Utils.sleep(500);
      genderSpout.feed(new Values(i, gender));
    }

    for (int i = 49; i >= 0; i--) {
      Utils.sleep(500);
      ageSpout.feed(new Values(i, "age_" + i));
    }
  }
}
