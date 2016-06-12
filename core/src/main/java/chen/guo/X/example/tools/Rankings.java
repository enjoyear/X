package chen.guo.X.example.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Rankings implements Serializable {
  private static final long serialVersionUID = -1549827195410578903L;
  private final int maxSize;
  private final List<Rankable> rankedItems = Lists.newArrayList();

  public Rankings(int topN) {
    if (topN < 1)
      throw new IllegalArgumentException("topN must be >= 1");
    maxSize = topN;
  }

  public Rankings(Rankings other) {
    this(other.maxSize());
    updateWith(other);
  }

  public int maxSize() {
    return maxSize;
  }

  public int size() {
    //return current size
    return rankedItems.size();
  }

  /**
   * The returned defensive copy is only "somewhat" defensive.
   * We do, for instance, return a defensive copy of the
   * enclosing List instance, and we do try to defensively copy any contained Rankable objects, too.  However, the
   * contract of Rankable.copy does not guarantee that any Object's embedded within
   * a Rankable will be defensively copied, too.
   *
   * @return a somewhat defensive copy of ranked items
   */
  public List<Rankable> getRankings() {
    List<Rankable> copy = Lists.newLinkedList();
    for (Rankable r : rankedItems) {
      copy.add(r.copy());
    }
    return ImmutableList.copyOf(copy);
  }

  public void updateWith(Rankings other) {
    other.getRankings().forEach(this::updateWith);
  }

  public void updateWith(Rankable r) {
    //This implementation is bad. Should simply insert it to the right place.
    //Or use priority queue to keep topN.
    synchronized (rankedItems) {
      Integer rank = findRankOf(r);
      if (rank != null)
        rankedItems.set(rank, r);
      else
        rankedItems.add(r);
      //rerank
      Collections.sort(rankedItems);
      Collections.reverse(rankedItems);
      //shrinkRankingsIfNeeded
      if (rankedItems.size() > maxSize) {
        rankedItems.remove(maxSize);
      }
    }
  }

  private Integer findRankOf(Rankable r) {
    Object tag = r.getObject();
    for (int rank = 0; rank < rankedItems.size(); rank++) {
      Object cur = rankedItems.get(rank).getObject();
      if (cur.equals(tag)) {
        return rank;
      }
    }
    return null;
  }

  /**
   * Removes ranking entries that have a count of zero.
   */
  public void pruneZeroCounts() {
    int i = 0;
    while (i < rankedItems.size()) {
      if (rankedItems.get(i).getCount() == 0) {
        rankedItems.remove(i);
      } else {
        i++;
      }
    }
  }

  public String toString() {
    return rankedItems.toString();
  }

  /**
   * Creates a (defensive) copy of itself.
   */
  public Rankings copy() {
    return new Rankings(this);
  }
}
