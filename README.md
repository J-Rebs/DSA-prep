# High-Throughput DSA & System Design Engine

A production-grade Java repository mapping advanced algorithmic patterns directly to large-scale infrastructure components. 

> [!TIP]
> Before starting, review the [Study Protocol & Recalibration Guide](./docs/study_protocol.md) to understand the 6–8 problem mastery ladder and the weekly testing protocol.

## 🗺️ The Tiered Interview Roadmap

```mermaid
graph TD
    subgraph Phase 1: Foundations [Linear Boundaries & Search Space]
        A[Sliding Window] --> B[Two Pointers]
        B --> C[Linked Lists & Pointers]
        C --> CA[Binary Search / Divide & Conquer]
    end

    subgraph Phase 2: Structural [State, Priorities & Hierarchies]
        D[Monotonic Stack] --> E[Heaps / Priority Queue]
        E --> F[Trie / Prefix Tree]
        F --> G[Segment Tree / Fenwick Tree]
    end

    subgraph Phase 3: Distributed & Networks [Connectivity, Routing & Consensus]
        H[Graph BFS/DFS] --> I[Topological Sort]
        I --> J[Union-Find / DSU]
        J --> K[Dijkstra / Shortest Path]
    end

    subgraph Phase 4: Optimization & Search [Decision Engines & Hashing]
        L[Dynamic Programming] --> M[Backtracking / Pruning]
        M --> N[Greedy / Intervals]
        N --> O[Bit Manipulation & Math]
    end

    CA --> D
    G --> H
    K --> L
```

## 📈 System Progress Tracker

| Phase | Pattern | Target System Component | LeetCode Tier | Status | Link to Blueprint |
| --- | --- | --- | --- | --- | --- |
| **Phase 1** | Sliding Window | Rate Limiter (Sliding Window Counter) | Medium / Hard | 🟢 Mastered | [View](./src/main/java/com/engine/phase1_foundations/p01_sliding_window/PATTERN_BLUEPRINT.md) |
| **Phase 1** | Two Pointers | Real-time Data Deduplication / Merging | Medium | 🔄 In Progress | [View](./src/main/java/com/engine/phase1_foundations/p02_two_pointers/PATTERN_BLUEPRINT.md) |
| **Phase 1** | Linked Lists & Pointers | LRU Cache / MemTable Compactor | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase1_foundations/p03_linked_list/PATTERN_BLUEPRINT.md) |
| **Phase 1** | Binary Search | LSM-Tree SSTable Index Lookup | Medium | 🔄 In Progress | [View](./src/main/java/com/engine/phase1_foundations/p04_binary_search/PATTERN_BLUEPRINT.md) |
| **Phase 2** | Monotonic Stack | Event Metric Streaming (Next Max Spike) | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase2_structural/p05_monotonic_stack/PATTERN_BLUEPRINT.md) |
| **Phase 2** | Heaps / PQ | Distributed Top-K Heavy Hitters Tracker | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase2_structural/p06_heaps_and_priority/PATTERN_BLUEPRINT.md) |
| **Phase 2** | Trie | Longest Prefix Match (IP Router) | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase2_structural/p07_trie/PATTERN_BLUEPRINT.md) |
| **Phase 2** | Segment Tree | Telemetry Metric Range Query Engine | Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase2_structural/p08_segment_tree/PATTERN_BLUEPRINT.md) |
| **Phase 3** | Graph BFS/DFS | Microservice Call Graph Trace Optimizer | Medium | 🛑 Todo | [View](./src/main/java/com/engine/phase3_distributed/p09_graph_traversals/PATTERN_BLUEPRINT.md) |
| **Phase 3** | Topological Sort | Distributed Task Scheduler / CI-CD Engine | Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase3_distributed/p10_topological_sort/PATTERN_BLUEPRINT.md) |
| **Phase 3** | Union-Find (DSU) | Distributed Network Partition Detector | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase3_distributed/p11_union_find/PATTERN_BLUEPRINT.md) |
| **Phase 3** | Dijkstra / Shortest Path | Service Mesh RPC Traffic Routing | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase3_distributed/p12_shortest_paths/PATTERN_BLUEPRINT.md) |
| **Phase 4** | Dynamic Programming | Cloud Instance Load Balancer | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase4_optimization/p13_dynamic_programming/PATTERN_BLUEPRINT.md) |
| **Phase 4** | Backtracking | SQL Relational Query Execution Planner | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase4_optimization/p14_backtracking/PATTERN_BLUEPRINT.md) |
| **Phase 4** | Greedy / Intervals | Thread Pool Task Scheduler (SJF/EDF) | Medium | 🛑 Todo | [View](./src/main/java/com/engine/phase4_optimization/p15_greedy_intervals/PATTERN_BLUEPRINT.md) |
| **Phase 4** | Bit Manipulation / Math | Bloom Filter & HyperLogLog Estimation | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase4_optimization/p16_bit_manipulation/PATTERN_BLUEPRINT.md) |

*Status options: 🟢 Mastered | 🔄 In Progress | 🛑 Todo*

---

## 🖥️ Interactive Study Dashboard

The repository includes a modern, self-contained interactive web page [index.html](file:///Users/joe/Documents/projects/DSA-prep/index.html) at the root of the repository to help you visualize pointer states, track your problem ladders, and access Java optimization cheatsheets.

### 1. Running Locally
Simply double-click the [index.html](file:///Users/joe/Documents/projects/DSA-prep/index.html) file, or open it in any web browser! No local servers, Node.js, or package installations are required.

### 2. Live GitHub Pages Site
Because the compiled code is purely static, you can serve it directly using the default branch source setting:
1. Go to your repository settings on GitHub ➔ **Pages** ➔ **Build and deployment**.
2. Set **Source** to **`Deploy from a branch`**.
3. Set **Branch** to **`main`** and the folder to **`/ (root)`**.
4. Click **Save**.

Your live study dashboard will be live at:
**`https://J-Rebs.github.io/DSA-prep/`** (or `https://J-Rebs.github.io/DSA-prep/index.html`)

