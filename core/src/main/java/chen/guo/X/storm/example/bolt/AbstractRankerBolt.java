package chen.guo.X.storm.example.bolt;

import chen.guo.X.storm.example.tools.Rankings;
import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRankerBolt extends BaseBasicBolt {

  private static final long serialVersionUID = 4931640198501530202L;
  private final int emitFrequencyInSeconds;
  private final Rankings rankings;

  public AbstractRankerBolt(int topN, int emitFrequencyInSeconds) {
    if (topN < 1)
      throw new IllegalArgumentException("topN must be >= 1 (you requested " + topN + ")");
    if (emitFrequencyInSeconds < 1)
      throw new IllegalArgumentException(
        "The emit frequency must be >= 1 seconds (you requested " + emitFrequencyInSeconds + " seconds)");

    this.emitFrequencyInSeconds = emitFrequencyInSeconds;
    rankings = new Rankings(topN);
  }

  protected Rankings getRankings() {
    return rankings;
  }

  @Override
  public final void execute(Tuple tuple, BasicOutputCollector collector) {
    if (TupleUtils.isTick(tuple)) {
      getLogger().debug("Received tick tuple, triggering emit of current rankings");
      collector.emit(new Values(rankings.copy()));
      getLogger().debug("Rankings: " + rankings);
    } else {
      updateRankingsWithTuple(tuple);
    }
  }

  abstract void updateRankingsWithTuple(Tuple tuple);

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("rankings"));
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    Map<String, Object> conf = new HashMap<>();
    conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
    return conf;
  }

  abstract Logger getLogger();
}
