# Pattern Blueprint: Binary Search

## 1. System Design Mapping
* **Macro System Component:** LSM-Tree SSTable Index Lookup
* **How it leverages this DSA Pattern:** How this micro algorithmic pattern optimizes the macro system component's bottlenecks.
* **Data Flow Architecture:**
  ```text
  Input Data ──> [Algorithm Bounds] ──> Output Action / Optimization
  ```

## 2. High-Yield Performance Tricks (Java Specific)
* **Memory & Collection Strategy:** Avoid boxed types, prefer arrays or flat primitive structures, pre-allocate buffers.
* **Time/Space Constraints:** High-throughput performance limits.

## 3. The Core Structural Trick (Mental Model)
> Internalize the key loop invariant or pointer configuration.

## 4. The 9-Problem Mastery Ladder

### Phase 1: Easy Warmups
1. **[Easy]** Exact Search (Key Index Lookup) - [🛑 Todo]
2. **[Easy]** Insertion Position Finder (Start Block Locator) - [🛑 Todo]

### Phase 2: Medium System Integration
3. **[Medium]** Range Query Lookup (SSTable Lower Bound) - [🔄 In Progress]
4. **[Medium]** Rotated Index Buffer Search (Hash Ring Locator) - [🛑 Todo]
5. **[Medium]** Rotated Index Boundary Finder (Min element in ring) - [🛑 Todo]
6. **[Medium]** Peak Load Anomaly Detector (Find peak element) - [🛑 Todo]
7. **[Medium]** Capacity Planner (Search Space Binary Search) - [🛑 Todo]

### Phase 3: Hard Scale & Stress
8. **[Hard]** Cross-Partition Median Key Find (Split-brain sync) - [🛑 Todo]
9. **[Hard]** Maximum Metric Load Allocator (Minimizing max load) - [🛑 Todo]

## 5. Production-Ready Java Blueprint (To be written by User)
*Once you solve a problem, copy your clean implementation here.*
