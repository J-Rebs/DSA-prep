# Pattern Blueprint: Sliding Window

## 1. System Design Mapping
* **Macro System Component:** Rate Limiter (Sliding Window Counter) & Telemetry Metrics Buffer
* **How it leverages this DSA Pattern:** 
  High-throughput systems (like API gateways or telemetry aggregators) receive millions of logs or network requests per second. To rate-limit or calculate moving averages, we cannot query database tables repeatedly or re-calculate sums from scratch. The **Sliding Window** pattern allows us to slide a time/element window dynamically over the event stream, adding the new event and subtracting the expired event in constant **O(1) time** and **O(1) memory**.
* **Data Flow Architecture:**
  ```text
  Event Stream ──> [Ingest: Add Right Ptr] ──> [Constraint Check] ──> [Evict: Shrink Left Ptr] ──> Moving Metrics
  ```

## 2. High-Yield Performance Tricks (Java Specific)
* **Zero-Autoboxing Hash Maps:**
  Standard hash maps (`Map<Character, Integer>`) box primitive keys and values, causing heavy heap allocations and Garbage Collection spikes in hot loops. Use a flat primitive array as a direct map:
  ```java
  int[] frequencies = new int[128]; // ASCII direct-address table
  // Incrementing/checking is now simple array indexing:
  frequencies[currentChar]++;
  ```
* **Avoid String Instantiations:**
  When searching substrings, do not call `str.substring(start, end)` inside the sliding loop, as this allocates new String objects on the heap. Instead, maintain `start` and `length` primitive pointers, and only materialize the substring once at the very end of the method.

## 3. The Core Structural Trick (Mental Model)
Maintain two indices: `left` (window start) and `right` (window end). 
1. Expand the window by incrementing `right` and incorporating `right`'s elements into the tracked metrics.
2. While the current window violates the problem constraints (e.g. too many distinct characters, sum exceeds threshold), shrink the window from the left by incrementing `left` and subtracting the `left` element from the tracked metrics.
3. Record the maximum/minimum window size at each valid state.

---

## 4. The 9-Problem Mastery Ladder

### Phase 1: Easy Warmups
1. **[Easy] Max Sum Subarray of Size K**
   * *System Mapping:* Rolling Metric Rollup (e.g. maximum CPU spike in a 3-minute window).
   * *Description:* Find the maximum sum of any contiguous subarray of size `K`.
2. **[Easy] Smallest Subarray with a Greater Sum**
   * *System Mapping:* Stream buffer resize calculations (finding minimum packets to exceed capacity threshold).
   * *Description:* Find the length of the smallest contiguous subarray whose sum is &ge; `target`.

### Phase 2: Medium System Integration
3. **[Medium] Rate Limiter Sliding Window Count**
   * *System Mapping:* SSTable Log Request Rate Limiter (identifying request floods).
   * *Description:* Given sorted request timestamps, return indices of events violating the rate limit (more than `maxRequests` in `windowSize`).
4. **[Medium] Longest Substring with K Distinct Characters**
   * *System Mapping:* Memory Pool Allocation (tracking contiguous blocks of active client session tags).
   * *Description:* Length of the longest substring with at most `K` distinct characters.
5. **[Medium] Permutation in a String**
   * *System Mapping:* Binary log signature matcher (scanning header packet anagrams in raw byte streams).
   * *Description:* Find all starting indices of the pattern's permutations in the string.
6. **[Medium] Jitter Buffer Smoothing (Character Replacement)**
   * *System Mapping:* Packet Jitter Buffer Smoothing (smoothing character dropouts in real-time communication).
   * *Description:* Longest substring of identical characters after replacing at most `K` characters.
7. **[Medium] Telemetry Drop Tolerator (Max Consecutive Ones III)**
   * *System Mapping:* SLA Dropout Checker (finding longest run of successful pings, tolerating up to `K` failures).
   * *Description:* Longest consecutive run of 1s in a binary array after replacing at most `K` 0s.

### Phase 3: Hard Scale & Stress
8. **[Hard] Minimum Window Substring (Log Aggregator)**
   * *System Mapping:* Multi-tag log parser (finding the minimum log slice containing all target log event signatures).
   * *Description:* Smallest substring containing all characters of a target pattern.
9. **[Hard] Sliding Window Maximum (Metric buffer queues)**
   * *System Mapping:* Telemetry Load Spike Queue (sliding window maximum tracker).
   * *Description:* Find the maximum value in each sliding window of size `K`.

---

## 5. Production-Ready Java Blueprint (To be written by User)
*Once you solve a problem, copy your clean implementation here.*
