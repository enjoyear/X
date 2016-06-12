package chen.guo.X.example.bolt;

import chen.guo.X.example.tools.NthLastModifiedTimeTracker;
import chen.guo.X.example.tools.SlidingWindowCounter;
import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This bolt performs rolling counts of incoming objects, i.e. sliding window based counting.
 */
public class RollingCountBolt extends BaseRichBolt {

  private static final long serialVersionUID = 5537727428628598519L;
  private static final Logger logger = Logger.getLogger(RollingCountBolt.class);

  private final int windowLengthInSeconds;
  private final int emitFrequencyInSeconds;
  private SlidingWindowCounter<Object> counter;
  private OutputCollector collector;
  /**
   * lastModifiedTracker is used to track the time that this bolt emit tuple,
   * or equivalently the time the bolt receives a system tick.
   */
  private NthLastModifiedTimeTracker lastModifiedTracker;

  /**
   * For instance, if the window length is set to an equivalent of 300s
   * and the emit frequency to 60s, then the bolt will
   * output the latest five-minute sliding window every minute
   *
   * This implementation CANNOT count correctly if emitFrequencyInSeconds
   * is not a divisor of windowLengthInSeconds, in which case the actual window
   * length will always be
   * (int)(windowLengthInSeconds/emitFrequencyInSeconds) * emitFrequencyInSeconds.
   *
   * @param windowLengthInSeconds  indicates the length of the sliding window
   * @param emitFrequencyInSeconds indicates how often this bolt emit the aggregated sum of
   *                               this sliding window.
   */
  public RollingCountBolt(int windowLengthInSeconds, int emitFrequencyInSeconds) {
    logger.info("Constructing RollingCountBolt...");
    this.windowLengthInSeconds = windowLengthInSeconds;
    this.emitFrequencyInSeconds = emitFrequencyInSeconds;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    logger.info("Preparing RollingCountBolt...");
    this.collector = collector;
    int len = windowLengthInSeconds / emitFrequencyInSeconds;
    counter = new SlidingWindowCounter<>(len);
    lastModifiedTracker = new NthLastModifiedTimeTracker(len);
  }

  @Override
  public void execute(Tuple tuple) {
    //Check getComponentConfiguration()
    if (TupleUtils.isTick(tuple)) {
      logger.debug("Received tick tuple, triggering Emit-of-Current-Window-Counts");

      Map<Object, Long> counts = counter.getCountsThenAdvanceWindow();
      int actualWindowLengthInSeconds = lastModifiedTracker.secondsSinceOldestModification();
      lastModifiedTracker.markAsModified();
      if (actualWindowLengthInSeconds != windowLengthInSeconds) {
        logger.warn(String.format("Actual window length is %d seconds when it should be %d seconds (you can safely ignore this warning during the startup phase)", actualWindowLengthInSeconds, windowLengthInSeconds));
      }
      for (Entry<Object, Long> entry : counts.entrySet()) {
        Object obj = entry.getKey();
        Long count = entry.getValue();
        collector.emit(new Values(obj, count, actualWindowLengthInSeconds));
      }
    } else {
      Object obj = tuple.getValue(0);
      counter.incrementCount(obj);
      collector.ack(tuple);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    /**
     *The latactualWindowLengthInSecondster is included in case the expected sliding window length
     *is different from the actual length, e.g. due to high system load.
     */
    declarer.declare(new Fields("obj", "count", "actualWindowLengthInSeconds"));
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    Map<String, Object> conf = new HashMap<>();
    //How often a tick tuple from the "__system" component and "__tick" stream
    //should be sent to tasks.
    conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
    return conf;
  }
}
