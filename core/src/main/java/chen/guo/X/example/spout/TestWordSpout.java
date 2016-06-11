package chen.guo.X.example.spout;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

public class TestWordSpout extends BaseRichSpout {

  private static Logger logger = LoggerFactory.getLogger(TestWordSpout.class);
  private SpoutOutputCollector _collector;

  @Override
  public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
    logger.info("Opening SimulatedWordSpout...");
    _collector = spoutOutputCollector;
  }

  @Override
  public void nextTuple() {
    Utils.sleep(100);
    final String[] words = new String[]{"California", "Illinois", "Florida", "Washington"};
    final Random rand = new Random();
    final String word = words[rand.nextInt(words.length)];
    _collector.emit(new Values(word));
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("word"));
  }
}

