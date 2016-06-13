package chen.guo.X.example.bolt;

import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.TimeCacheMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SingleJoinBolt extends BaseRichBolt {
  private static final Logger logger = Logger.getLogger(SingleJoinBolt.class);
  private OutputCollector _collector;
  private Fields _idField;  //keeps all src fields.
  private Fields _boltOutFields;
  private int _numSources;
  private TimeCacheMap<List<Object>, Map<GlobalStreamId, Tuple>> _unjoinedPendingTuples;
  private Map<String, GlobalStreamId> _fieldLocations;

  public SingleJoinBolt(Fields outFields) {
    _boltOutFields = outFields;
  }

  @Override
  public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
    _fieldLocations = new HashMap<>();
    _collector = collector;
    int timeout = ((Number) conf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS)).intValue();
    _unjoinedPendingTuples = new TimeCacheMap<>(timeout, new ExpireCallback());
    _numSources = context.getThisSources().size();
    Set<String> idFields = null;
    for (GlobalStreamId source : context.getThisSources().keySet()) {
      Fields srcOutFields = context.getComponentOutputFields(source.get_componentId(), source.get_streamId());
      Set<String> setFields = new HashSet<>(srcOutFields.toList());
      if (idFields == null)
        idFields = setFields;
      else
        //retain all keeps the intersection of two sets.
        idFields.retainAll(setFields);

      for (String boltOutField : _boltOutFields) {
        for (String srcOutField : srcOutFields) {
          if (boltOutField.equals(srcOutField)) {
            _fieldLocations.put(boltOutField, source);
          }
        }
      }
    }
    _idField = new Fields(new ArrayList<>(idFields));

    if (_fieldLocations.size() != _boltOutFields.size()) {
      throw new RuntimeException("Cannot find all outfields among sources");
    }
  }

  @Override
  public void execute(Tuple tuple) {
    //Here the srcOutFieldNames is "id"
    List<Object> keysForJoin = tuple.select(_idField);
    GlobalStreamId streamId = new GlobalStreamId(tuple.getSourceComponent(), tuple.getSourceStreamId());
    if (!_unjoinedPendingTuples.containsKey(keysForJoin)) {
      _unjoinedPendingTuples.put(keysForJoin, new HashMap<>());
    }

    Map<GlobalStreamId, Tuple> parts = _unjoinedPendingTuples.get(keysForJoin);
    if (parts.containsKey(streamId))
      throw new RuntimeException("Received same side of single join twice");
    parts.put(streamId, tuple);

    //Printing
    StringBuilder sb = new StringBuilder();
    //tuple: e.g. [1, female], [49, age_49]
    tuple.getValues().forEach(x -> {
      sb.append(x.toString());
      sb.append("===");
    });
    //idField: e.g. 1
    _idField.forEach(x -> {
      sb.append(x);
      sb.append("---");
    });
    for (Object obj : keysForJoin) {
      sb.append(obj.toString());
      sb.append("###");
    }
    //streamId example: GlobalStreamId(componentId:age, streamId:default)
    sb.append(streamId.toString());
    sb.append("@");
    sb.append(parts.size());
    logger.info(sb.toString());
    //Printing

    if (parts.size() == _numSources) {
      _unjoinedPendingTuples.remove(keysForJoin);
      List<Object> joinResult = new ArrayList<>();
      for (String outField : _boltOutFields) {
        GlobalStreamId loc = _fieldLocations.get(outField);
        joinResult.add(parts.get(loc).getValueByField(outField));
      }
      _collector.emit(new ArrayList<>(parts.values()), joinResult);

      for (Tuple part : parts.values()) {
        _collector.ack(part);
      }
    }

  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(_boltOutFields);
  }

  private class ExpireCallback implements TimeCacheMap.ExpiredCallback<List<Object>, Map<GlobalStreamId, Tuple>> {
    @Override
    public void expire(List<Object> id, Map<GlobalStreamId, Tuple> tuples) {
      for (Tuple tuple : tuples.values()) {
        _collector.fail(tuple);
      }
    }
  }
}
