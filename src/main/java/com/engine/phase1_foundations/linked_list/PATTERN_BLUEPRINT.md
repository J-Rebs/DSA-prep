# Pattern Blueprint: Linked Lists & Pointers

## 1. System Design Mapping
* **Macro System Component:** Cache Controllers (LRU/LFU) & DB Write Paths (MemTables)
* **How it leverages this DSA Pattern:**
  LRU caching requires $O(1)$ evictions of the oldest keys when reaching capacity boundaries (Least Recently Used policy). A doubly linked list facilitates $O(1)$ node detachment and head splicing when paired with a hash map. Fast/slow pointers resolve cycle traps in network routing tracing.
* **Data Flow Architecture:**
  ```text
  Get/Put Event ──> [Hash Map Lookup] ──> Locate Node ──> [Detached and Splice to Head of DLL]
  ```

## 2. High-Yield Performance Tricks (Java Specific)
* **Memory & Collection Strategy:**
  To reduce GC pressure and reference-chasing in high-throughput JVM environments, implement custom doubly linked node logic in-place rather than relying on `java.util.LinkedList`.
* **Time/Space Constraints:**
  * Time Complexity: $O(1)$ for LRU cache updates.
  * Space Complexity: $O(N)$ for node list storage.

## 3. The Core Structural Trick (Mental Model)
> Standardize pointer swaps by using a dummy node sentinel at the head and tail, eliminating boundary null checks when splicing nodes.

## 4. The 9-Problem Mastery Ladder

### Phase 1: Easy Warmups
1. **[Easy] Reverse Linked List** - [🛑 Todo]
   * *System Mapping:* Reversing execution logs or event trails.
   * *LeetCode Equivalent:* LeetCode 206
   * *Mental Model Cue:* Maintain three pointers: `prev`, `curr`, and `nextTemp`. Advance step-by-step to invert the links in a single pass.
2. **[Easy] Middle of Linked List** - [🛑 Todo]
   * *System Mapping:* Midpoint partitioning in divide-and-conquer processing.
   * *LeetCode Equivalent:* LeetCode 876
   * *Mental Model Cue:* Advance the `fast` pointer two steps for every one step of the `slow` pointer; when `fast` hits the end, `slow` is at the middle.

### Phase 2: Medium System Integration
3. **[Medium] Cycle Detection (Floyd's Algorithm)** - [🛑 Todo]
   * *System Mapping:* Resolving network routing cycles / tracing loops.
   * *LeetCode Equivalent:* LeetCode 142
   * *Mental Model Cue:* If fast and slow runners meet, a cycle exists. To find the cycle entrance, reset slow to the head and advance both one step at a time until they meet again.
4. **[Medium] LRU Cache** - [🛑 Todo]
   * *System Mapping:* In-memory Cache Controller.
   * *LeetCode Equivalent:* LeetCode 146
   * *Mental Model Cue:* Pair a Doubly Linked List with a Hash Map. Move accessed nodes to the head and evict from the tail.
5. **[Medium] LFU Cache** - [🛑 Todo]
   * *System Mapping:* Frequency-based cache manager.
   * *LeetCode Equivalent:* LeetCode 460
   * *Mental Model Cue:* Map frequencies to separate Doubly Linked Lists, maintaining a `minFrequency` pointer to quickly identify eviction targets.
6. **[Medium] Deep Copy Node Buffer** - [🛑 Todo]
   * *System Mapping:* Clone graph-structured metadata states.
   * *LeetCode Equivalent:* LeetCode 138
   * *Mental Model Cue:* Interleave cloned nodes directly behind original nodes (e.g., `A -> A' -> B -> B'`), resolving random pointers in a second pass before separation.
7. **[Medium] Arbitrary Precision Arithmetic** - [🛑 Todo]
   * *System Mapping:* Arbitrary-precision financial arithmetic streams.
   * *LeetCode Equivalent:* LeetCode 2
   * *Mental Model Cue:* Traverse both lists simultaneously, maintaining a `carry` variable and appending sum digits to a new node chain.

### Phase 3: Hard Scale & Stress
8. **[Hard] Merge K Sorted Lists** - [🛑 Todo]
   * *System Mapping:* Compaction index merger in database MemTables.
   * *LeetCode Equivalent:* LeetCode 23
   * *Mental Model Cue:* Use a Min-Heap (Priority Queue) containing the head nodes of all K lists to iteratively select the smallest node, or divide and conquer.
9. **[Hard] Reverse Nodes in K-Group** - [🛑 Todo]
   * *System Mapping:* Batch-level stream log reversals.
   * *LeetCode Equivalent:* LeetCode 25
   * *Mental Model Cue:* Check if K nodes remain; if so, reverse that segment in-place, link it to the previous tail, and recurse/iterate on the next segment.

## 5. Production-Ready Java Blueprint (To be written by User)
*Once you solve a problem, copy your clean implementation here.*
