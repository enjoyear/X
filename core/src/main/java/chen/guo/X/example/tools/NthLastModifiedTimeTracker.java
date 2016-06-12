/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chen.guo.X.example.tools;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.storm.utils.Time;

/**
 * This class tracks the last ${numTimesToTrack} modifications in a rolling fashion.
 */
public class NthLastModifiedTimeTracker {

  private static final int MILLIS_IN_SEC = 1000;

  /**
   * CircularFifoBuffer of length(3) behavior:
   * add(1) => 1
   * add(2) => 1,2
   * add(3) => 1,2,3
   * add(4) => 2,3,4
   * get()  => 2,3,4
   * remove() => 3,4
   * add(5) => 3,4,5
   */
  private final CircularFifoBuffer lastModifiedTimesMillis;

  public NthLastModifiedTimeTracker(int numTimesToTrack) {
    if (numTimesToTrack < 1) {
      throw new IllegalArgumentException(
        "numTimesToTrack must be greater than zero (you requested " + numTimesToTrack + ")");
    }
    lastModifiedTimesMillis = new CircularFifoBuffer(numTimesToTrack);
    long nowCached = Time.currentTimeMillis();
    for (int i = 0; i < lastModifiedTimesMillis.maxSize(); i++) {
      lastModifiedTimesMillis.add(nowCached);
    }
  }

  public int secondsSinceOldestModification() {
    long modifiedTimeMillis = (Long) lastModifiedTimesMillis.get();
    return (int) ((Time.currentTimeMillis() - modifiedTimeMillis) / MILLIS_IN_SEC);
  }

  public void markAsModified() {
    lastModifiedTimesMillis.add(Time.currentTimeMillis());
  }
}
