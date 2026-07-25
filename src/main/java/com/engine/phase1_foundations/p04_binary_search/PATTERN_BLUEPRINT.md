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

## 3. The Core Structural Trick (Mental Model)
1. **Index Binary Search:** Halve a sorted array range `[low, high]` based on value comparisons.
2. **Search Space Binary Search:** Binary search on the *result space* `[minPossible, maxPossible]` by testing feasibility of candidate midpoints.
3. **The `low` vs `high` Symmetrical Duality Property:**
   When standard binary search loop (`while (low <= high)`) terminates without an exact match:
   * **`low`** = Index of the **first element strictly greater** than `target` (Insertion position).
   * **`high` (`low - 1`)** = Index of the **largest element strictly less** than `target` (Floor / SSTable lower bound).

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
