package chen.guo.X.storm.example.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.storm.tuple.Tuple;

import java.io.Serializable;
import java.util.List;

public class RankableObjectWithFields implements Rankable, Serializable {

  private static final long serialVersionUID = -9102878650001058090L;

  private final Object obj;
  private final long count;
  private final ImmutableList<Object> fields;

  public RankableObjectWithFields(Object obj, long count, Object... otherFields) {
    if (obj == null)
      throw new IllegalArgumentException("The object must not be null");
    if (count < 0)
      throw new IllegalArgumentException("The count must be >= 0");

    this.obj = obj;
    this.count = count;
    fields = ImmutableList.copyOf(otherFields);
  }

  /**
   * Convert a tuple to RankableObject
   */
  public static RankableObjectWithFields from(Tuple tuple) {
    List<Object> otherFields = Lists.newArrayList(tuple.getValues());
    Object obj = otherFields.remove(0);
    Long count = (Long) otherFields.remove(0);
    return new RankableObjectWithFields(obj, count, otherFields.toArray());
  }

  public Object getObject() {
    return obj;
  }

  public long getCount() {
    return count;
  }

  public List<Object> getFields() {
    return fields;
  }

  @Override
  public int compareTo(Rankable other) {
    long delta = getCount() - other.getCount();
    if (delta > 0) return 1;
    else if (delta < 0) return -1;
    else return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof RankableObjectWithFields))
      return false;
    RankableObjectWithFields other = (RankableObjectWithFields) o;
    return obj.equals(other.obj) && count == other.count;
  }

  @Override
  public int hashCode() {
    int result = 17;
    int countHash = (int) (count ^ (count >>> 32));
    result = 31 * result + countHash;
    result = 31 * result + obj.hashCode();
    return result;
  }

  public String toString()  {
    StringBuffer buf = new StringBuffer();
    buf.append("[");
    buf.append(obj);
    buf.append("|");
    buf.append(count);
    for (Object field : fields) {
      buf.append("|");
      buf.append(field);
    }
    buf.append("]");
    return buf.toString();
  }

  /**
   * Note: We do not defensively copy the wrapped object and any accompanying fields.
   * We do guarantee, however, do return a defensive (shallow) copy of the List object
   * that is wrapping any accompanying fields.
   *
   * @return
   */
  @Override
  public Rankable copy() {
    List<Object> shallowCopyOfFields = ImmutableList.copyOf(getFields());
    return new RankableObjectWithFields(getObject(), getCount(), shallowCopyOfFields);
  }

}
