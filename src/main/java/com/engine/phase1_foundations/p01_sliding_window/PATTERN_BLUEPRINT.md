# Pattern Blueprint: Sliding Window

## 1. System Design Mapping
* **Macro System Component:** Rate Limiter (Sliding Window Counter)
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
1. **[Easy]** Max Sum Subarray of Size K - [🛑 Todo]
2. **[Easy]** Smallest Subarray with a Greater Sum - [🛑 Todo]

### Phase 2: Medium System Integration
3. **[Medium]** Rate Limiter sliding window count (SSTable logs) - [🔄 In Progress]
4. **[Medium]** Longest Substring with K Distinct Characters - [🛑 Todo]
5. **[Medium]** Permutation in a String - [🛑 Todo]

### Phase 3: Hard Scale & Stress
8. **[Hard]** Minimum Window Substring - [🛑 Todo]
9. **[Hard]** Sliding Window Maximum (Metric buffer queues) - [🛑 Todo]

## 5. Production-Ready Java Blueprint (To be written by User)
*Once you solve a problem, copy your clean implementation here.*
