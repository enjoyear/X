package chen.guo.X.storm.example.tools;

import java.io.Serializable;
import java.util.Map;

/**
 * This class counts objects in a sliding window fashion.
 * 1) give multiple "producer" threads write access to the counter
 * 2) give a single "consumer" thread read access to the counter.
 * <p>
 * Sliding window counts of an single object over time
 * Minute (timeline):
 * 1    2   3   4   5   6   7   8
 * Observed counts per minute:
 * 1    1   1   1   0   0   0   0
 * Counts returned by counter:
 * 1    2   3   4   4   3   2   1
 */
public final class SlidingWindowCounter<T> implements Serializable {

  private static final long serialVersionUID = -2645063988768785810L;

  private SlotBasedCounter<T> counter;
  private int currentSlot;
  private int windowLen;

  /**
   * @param windowLen window length == number of slots
   */
  public SlidingWindowCounter(int windowLen) {
    if (windowLen < 2)
      throw new IllegalArgumentException("Window length in slots must be at least two");

    this.windowLen = windowLen;
    this.counter = new SlotBasedCounter<>(this.windowLen);

    this.currentSlot = 0;
  }

  public void incrementCount(T obj) {
    counter.incrementCount(obj, currentSlot);
  }

  /**
   * Return the current total counts of all tracked objects, then advance the window.
   * This means that the consumer thread indirectly controls where writes of the producer threads
   * will go to. Also, by itself this class will not advance the current slot.
   */
  public Map<T, Long> getCountsThenAdvanceWindow() {
    /*
    From algorithm perspective, this can be improved.
    No need to recalculate the total sum. We just need to add the difference
    of (new counts - exiting counts).
     */
    Map<T, Long> counts = counter.getTotalCounts();
    counter.untrackZeroCountObjects();
    //The slot that exits the sliding window.
    int vanishingSlot = (currentSlot + 1) % windowLen;
    counter.wipeSlot(vanishingSlot);
    currentSlot = vanishingSlot;
    return counts;
  }

}
