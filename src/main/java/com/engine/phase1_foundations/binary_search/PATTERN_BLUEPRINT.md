# Pattern Blueprint: Binary Search

## 1. System Design Mapping
* **Macro System Component:** LSM-Tree SSTable Index Lookup & Distributed Shard Allocation
* **How it leverages this DSA Pattern:**
  SSTable block indexes contain keys sorted in lexicographical order. Searching for a specific database key uses binary search on the index blocks to locate the appropriate offset on disk. At scale, binary search coordinates query distributions across distributed hash rings, cluster node partitions, and elastic scheduler resource allocations.
* **Data Flow Architecture:**
  ```text
  Query Key / Capacity Constraint ──> [Binary Search Partitioning] ──> Locate Target Shard / Solve Optimal Bounds
  ```

## 2. High-Yield Performance Tricks (Java Specific)
* **Memory & Collection Strategy:**
  Avoid object wrappers. Store indices as flat primitive `int[]` or `byte[]` arrays.
* **Midpoint Calculation:**
  To prevent integer overflow errors, use the unsigned right shift operator: `int mid = (low + high) >>> 1;` instead of `(low + high) / 2`.
* **Time/Space Constraints:**
  * Time Complexity: $O(\log N)$
  * Space Complexity: $O(1)$ auxiliary space.

## 3. The Core Structural Trick (Mental Model)
> Continually half the search space based on sorted properties. The invariant is that the target key resides in the interval `[low, high]`.

## 4. The 9-Problem Mastery Ladder

### Phase 1: Easy Warmups
1. **[Easy] Exact Search (Key Index Lookup)** - [🛑 Todo]
   * *System Mapping:* Search index for exact key.
   * *LeetCode Equivalent:* LeetCode 704 - Binary Search
   * *Mental Model Cue:* Half the search space, checking if mid == target.
2. **[Easy] Insertion Position Finder** - [🛑 Todo]
   * *System Mapping:* Finding the start block of a partition index range.
   * *LeetCode Equivalent:* LeetCode 35 - Search Insert Position
   * *Mental Model Cue:* Return `low` (the insertion index) when the search loop terminates.

### Phase 2: Medium System Integration
3. **[Medium] Range Query Lookup (Lower Bound)** - [🔄 In Progress]
   * *System Mapping:* SSTable Block Start Key Lookup (Greatest key <= target).
   * *LeetCode Equivalent:* LeetCode 34 - Find First and Last Position of Element in Sorted Array / Lower Bound
   * *Mental Model Cue:* Cache potential answer when keys[mid] <= target, then scan right.
4. **[Medium] Rotated Index Buffer Search** - [🛑 Todo]
   * *System Mapping:* Distributed hash ring node lookup.
   * *LeetCode Equivalent:* LeetCode 33 - Search in Rotated Sorted Array
   * *Mental Model Cue:* Check which half of the array is sorted, then check if target falls inside that sorted half.
5. **[Medium] Rotated Index Boundary Finder** - [🛑 Todo]
   * *System Mapping:* Identifying the partition offset boundaries.
   * *LeetCode Equivalent:* LeetCode 153 - Find Minimum in Rotated Sorted Array
   * *Mental Model Cue:* If keys[mid] > keys[high], the pivot/minimum lies to the right; else it lies to the left (including mid).
6. **[Medium] Peak Load Anomaly Detector** - [🛑 Todo]
   * *System Mapping:* Finding peak metrics under wave-like load spikes.
   * *LeetCode Equivalent:* LeetCode 162 - Find Peak Element
   * *Mental Model Cue:* If metrics[mid] < metrics[mid+1], the peak lies to the right; else it lies to the left (including mid).
7. **[Medium] Capacity Planner** - [🛑 Todo]
   * *System Mapping:* Elastic Auto-Scaler instance calculator.
   * *LeetCode Equivalent:* LeetCode 1011 - Capacity To Ship Packages Within D Days
   * *Mental Model Cue:* Binary search on the *capacity* range [maxLoad, sumLoads]. Helper method checks if capacity can execute all tasks in <= numInstances.

### Phase 3: Hard Scale & Stress
8. **[Hard] Cross-Partition Median Key Find** - [🛑 Todo]
   * *System Mapping:* Split-brain metadata sync mediator.
   * *LeetCode Equivalent:* LeetCode 4 - Median of Two Sorted Arrays
   * *Mental Model Cue:* Binary search the partition point in the smaller array such that elements on the left are <= elements on the right.
9. **[Hard] Maximum Metric Load Allocator** - [🛑 Todo]
   * *System Mapping:* Dynamic partition shard balance controller (Minimizing max load).
   * *LeetCode Equivalent:* LeetCode 410 - Split Array Largest Sum
   * *Mental Model Cue:* Binary search the *max load* range [maxVal, sumVal]. Helper method verifies if array can be partitioned into <= numShards sub-arrays where each sub-array sum is <= max load.

## 5. Production-Ready Java Blueprint (To be written by User)
*Once you solve a problem, copy your clean implementation here.*
