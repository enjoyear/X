package chen.guo.X.storm.example.tools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class provides per-slot counts of the occurrences of objects.
 *
 * @param <T> The type of those objects we want to count.
 */
public final class SlotBasedCounter<T> implements Serializable {

  private static final long serialVersionUID = 4858185737378394432L;
  /**
   * T is the object, long[] is the counts per slot.
   * long[] length is number of slots
   */
  private final Map<T, long[]> objToCounts = new HashMap<>();
  private final int numSlots;

  public SlotBasedCounter(int numSlots) {
    if (numSlots <= 0)
      throw new IllegalArgumentException("Number of slots must be greater than zero)");
    this.numSlots = numSlots;
  }

  public void incrementCount(T obj, int slot) {
    long[] counts = objToCounts.get(obj);
    if (counts == null) {
      counts = new long[this.numSlots];
      objToCounts.put(obj, counts);
    }
    ++counts[slot];
  }

  public long getCount(T obj, int slot) {
    long[] counts = objToCounts.get(obj);
    if (counts == null) {
      return 0;
    } else {
      return counts[slot];
    }
  }

  public Map<T, Long> getTotalCounts() {
    Map<T, Long> result = new HashMap<>();
    for (Map.Entry<T, long[]> entry : objToCounts.entrySet()) {
      result.put(entry.getKey(), sumArray(entry.getValue()));
    }
    return result;
  }

  private long sumArray(long[] curr) {
    long total = 0;
    for (long l : curr) {
      total += l;
    }
    return total;
  }

  public void wipeSlot(int slot) {
    for (T obj : objToCounts.keySet()) {
      long[] counts = objToCounts.get(obj);
      counts[slot] = 0;
    }
  }

  public void untrackZeroCountObjects() {
    Set<T> objToBeRemoved = new HashSet<>();
    for (Map.Entry<T, long[]> entry : objToCounts.entrySet()) {
      if (sumArray(entry.getValue()) == 0) {
        objToBeRemoved.add(entry.getKey());
      }
    }
    objToBeRemoved.forEach(objToCounts::remove);
  }
}
