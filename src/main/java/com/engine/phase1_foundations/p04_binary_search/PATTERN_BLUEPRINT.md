# Pattern Blueprint: Binary Search

## 1. System Design Mapping
* **Macro System Component:** LSM-Tree SSTable Index Lookup & Search Space Capacity Optimizer
* **How it leverages this DSA Pattern:**
  High-performance databases (like RocksDB, Cassandra) use SSTables (Sorted String Tables) stored on disk. Searching linear index arrays for key boundaries or computing optimal instance capacities without full scans requires $O(\log N)$ or $O(\log(\text{Range}))$ search space reduction.
* **Data Flow Architecture:**
  ```text
  Sorted Key Array / Range ──> [Overflow-Safe Midpoint (low + high) >>> 1] ──> [Bound Branch Pruning] ──> O(log N) Result
  ```

## 2. High-Yield Performance Tricks (Java Specific)
* **Overflow-Safe Midpoint Shift:**
  Always calculate midpoints using unsigned right shift `int mid = (low + high) >>> 1;` instead of `(low + high) / 2` to prevent 32-bit integer overflow when arrays exceed $10^7$ items.
* **Zero Heap Allocation Loops:**
  Avoid allocating objects inside binary search loops. Keep all index variables (`low`, `high`, `mid`) as primitive integers.

3. **The Core Structural Invariants (Mental Models):**
   * **Floor vs Ceiling Duality:** When `while (low <= high)` finishes without an exact match:
     * `low` = Smallest element $\ge$ `target` (Ceiling / Insertion position).
     * `high` (`low - 1`) = Largest element $\le$ `target` (Floor / SSTable lower bound).
   * **Rotated Array Invariants (`keys[mid] vs keys[high]`):**
     * **`keys[mid] < keys[high]`**: Right half `[mid..high]` is strictly sorted. Minimum element lives in left half (including `mid`).
     * **`keys[mid] > keys[high]`**: Left half `[low..mid]` is strictly sorted. Minimum element lives in right half (`low = mid + 1`).
     * **Rotated Maximum Element**: The maximum element is at `(min_index - 1 + N) % N` (the peak right before the cliff drop).
   * **Slope-Following Peak Detection:**
     * Finding a local peak on an unsorted array reduces to Binary Search because evaluating local slope `metrics[mid]` vs `metrics[mid + 1]` determines whether `mid` sits on an ascending ($\nearrow$) or descending ($\searrow$) slope.
     * Stepping uphill discards 50% of the search space at each step until `low == high` pin-points a local maximum!
   * **Binary Search on Answer Space (Monotonicity Duality):**
     * Any problem asking for *"Find the min/max capacity X that satisfies condition Y"* reduces to Binary Search if the feasibility check $f(X)$ is **monotonic** (i.e. transitions cleanly from `[False, False, ..., True, True]`).
     * **Lower Bound (`low`)**: Absolute minimum feasible candidate (e.g. `max(taskLoads)` — single largest item that must fit).
     * **Upper Bound (`high`)**: Absolute maximum feasible candidate (e.g. `sum(taskLoads)` — total workload on 1 instance).
     * **Feasibility Check**: Binary search tests `mid`. If `isFeasible(mid)` is true, search left (`high = mid`) for a smaller valid capacity. If false, search right (`low = mid + 1`).
   * **Cross-Partition Virtual Cut Invariants (2-Array Median Search):**
     * **Array Order Swap Guard:** Always swap inputs up front (`keysA.length <= keysB.length`) so forced cut $j = (M + N + 1) / 2 - i$ never yields negative or out-of-bound indices.
     * **Unified Left Half Size Formula:** `(M + N + 1) / 2` unifies odd and even combined length math. For odd $M+N$, median is `max(maxALeft, maxBLeft)`. For even $M+N$, median is `(max(maxALeft, maxBLeft) + min(minARight, minBRight)) / 2.0`.

---

## 4. The 11-Problem Mastery Ladder

### Phase 1: Easy Warmups
1. **[Easy] Exact Search (Key Index Lookup)**
   * *System Mapping:* MemTable Exact Match (finding exact key in index blocks).
   * *Description:* Find exact index of target key in sorted array, or -1 if absent.
2. **[Easy] Insertion Position Finder (Start Block Locator)**
   * *System Mapping:* Log Append boundary finder (finding index to insert new record).
   * *Description:* Find index where target should be inserted to maintain order.
3. **[Easy] First and Last Occurrence Range Search**
   * *System Mapping:* SSTable duplicate key range locator (finding start & end offset of duplicate keys).
   * *Description:* Find starting and ending index of target key in sorted array containing duplicates.

### Phase 2: Medium System Integration
4. **[Medium] Range Query Lookup (SSTable Lower Bound)**
   * *System Mapping:* LSM-Tree SSTable Block Indexer (finding data block offset for target key).
   * *Description:* Find largest key $\le$ targetKey to retrieve correct block offset.
5. **[Medium] Rotated Index Buffer Search (Hash Ring Locator)**
   * *System Mapping:* Consistent Hashing Ring search (finding nodes in a shifted circle).
   * *Description:* Search for target key in a sorted array rotated by an unknown offset.
6. **[Medium] Rotated Index Boundary Finder (Min element in ring)**
   * *System Mapping:* Shifted circular boundary detector (finding the ring's pivot element).
   * *Description:* Find minimum element in a sorted rotated array.
7. **[Medium] Peak Load Anomaly Detector (Find peak element)**
   * *System Mapping:* Telemetry Spike detector (finding local peak workloads).
   * *Description:* Find a local peak element index in an unsorted metrics array.
8. **[Medium] Capacity Planner (Search Space Binary Search)**
   * *System Mapping:* Resource Scheduler (finding minimum instance capacity to complete workloads).
   * *Description:* Find minimum capacity required to complete workloads within instance limit.
9. **[Medium] 2D SSTable Matrix Search (Partition Grid Search)**
   * *System Mapping:* Multi-SSTable partition grid search (searching across 2D sorted partition grid).
   * *Description:* Search for target key in a 2D sorted matrix.

### Phase 3: Hard Scale & Stress
10. **[Hard] Cross-Partition Median Key Find (Split-brain sync)**
    * *System Mapping:* Distributed database sync (finding median partition key in log streams).
    * *Description:* Find median of two sorted arrays of different sizes in $O(\log(\min(M, N)))$ time.
11. **[Hard] Maximum Metric Load Allocator (Minimizing max load)**
    * *System Mapping:* Router Traffic Allocator (partitioning loads to minimize maximum bottleneck).
    * *Description:* Partition array into K subarrays such that maximum subarray sum is minimized.
