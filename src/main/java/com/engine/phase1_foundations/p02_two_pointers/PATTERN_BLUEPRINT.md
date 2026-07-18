# Pattern Blueprint: Two Pointers

## 1. System Design Mapping
* **Macro System Component:** Stream Compaction, LSM-Tree MemTable Compaction, and Ledger Transaction Reconciliation.
* **How it leverages this DSA Pattern:** 
  High-throughput storage engines (like Cassandra or RocksDB) and stream processing engines (like Apache Flink) must continuously clean up tombstones, merge sorted logs, or reconcile transaction ledgers. Allocating temporary data structures or arrays during these operations triggers frequent Garbage Collection cycles, reducing system throughput. The **Two Pointers** pattern allows us to traverse, partition, or merge data in a single linear pass ($O(N)$ time) with **O(1) auxiliary space**, processing inputs in-place.
* **Data Flow Architecture:**
  ```text
  Unsorted Input Stream ──> [Sort (if required)] ──> [Two Pointers: Opposing Ends / Fast-Slow] ──> In-place Compacted Buffer
  ```

## 2. High-Yield Performance Tricks (Java Specific)
* **Zero-Allocation Array Compaction:**
  When filtering or deduplicating, do not allocate a new array. Instead, use a slow pointer (`write`) and a fast pointer (`read`). Overwrite elements at `write` index and return the active compacted length:
  ```java
  int write = 0;
  for (int read = 0; read < arr.length; read++) {
      if (shouldKeep(arr[read])) {
          arr[write++] = arr[read];
      }
  }
  return write; // Returns new logical array size
  ```
* **Avoid Autoboxing in Lists:**
  For problems like Triplet/Quadruple Sum, returning nested lists (`List<List<Integer>>`) can allocate many wrapper objects. If performance is critical in production, use primitive multi-dimensional arrays (`int[][]`) or flat serialized buffers.

## 3. The Core Structural Trick (Mental Model)
Maintain two indices: `left` and `right`.
* **Converging Pointers:** Start at `0` and `length - 1`. Move them inward (`left++` or `right--`) based on a comparison to narrow down targets in sorted arrays.
* **Fast & Slow Pointers (Tandem):** Start both at `0` (or offset by 1). The fast pointer scans ahead, and the slow pointer only advances when a modification condition (like finding a unique element) is met.

---

## 4. The 11-Problem Mastery Ladder

### Phase 1: Easy Warmups
1. **[Easy] Pair with Target Sum**
   * *System Mapping:* Stream deduplication index mapper (finding matching sequence pairs).
   * *Description:* Find a pair in a sorted array that adds up to a target sum.
2. **[Easy] Remove Duplicates (Compaction)**
   * *System Mapping:* In-place telemetry log compaction.
   * *Description:* Remove duplicate elements in-place from a sorted array.
3. **[Easy] Squaring a Sorted Array**
   * *System Mapping:* Batch load balancer squaring index bounds.
   * *Description:* Return sorted array of squares from a sorted array containing negative values.

### Phase 2: Medium System Integration
4. **[Medium] Triplet Sum to Zero**
   * *System Mapping:* Three-way ledger transaction reconciliation.
   * *Description:* Find all unique triplets that sum to zero.
5. **[Medium] Subarrays with Product Less than Target**
   * *System Mapping:* Product throughput window calculator.
   * *Description:* Count contiguous subarrays whose product is less than a target.
6. **[Medium] Compacting SSTable logs in-place**
   * *System Mapping:* Active MemTable tombstone compaction.
   * *Description:* Consolidate active and tombstoned logs in-place using two runner pointers.
7. **[Medium] Min Window Sort**
   * *System Mapping:* Buffer sort boundary identification.
   * *Description:* Shortest subarray length that, when sorted, makes the entire array sorted.
8. **[Medium] Quadruple Sum to Target**
   * *System Mapping:* Four-way ledger balance reconciler.
   * *Description:* Find all unique quadruplets that sum to target.
9. **[Medium] Dutch National Flag Problem (Partitioning)**
   * *System Mapping:* Three-tiered cache bucket partitioner.
   * *Description:* Sort an array of 0s, 1s, and 2s in-place in linear time.

### Phase 3: Hard Scale & Stress
10. **[Hard] Trapping Rain Water**
    * *System Mapping:* Telemetry metric packet buffer volume calculation.
    * *Description:* Compute how much water can be trapped in an elevation map.
11. **[Hard] Shortest Subarray with Sum at Least K**
    * *System Mapping:* Multi-partition transaction window search.
    * *Description:* Find shortest contiguous subarray with sum >= K (supports negative values).

---

## 5. Production-Ready Java Blueprint
*Once you solve a problem, copy your clean implementation here.*
