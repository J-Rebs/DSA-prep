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
| **Phase 1** | Sliding Window | Rate Limiter (Sliding Window Counter) | Medium / Hard | 🔄 In Progress | [View](./src/main/java/com/engine/phase1_foundations/sliding_window/PATTERN_BLUEPRINT.md) |
| **Phase 1** | Two Pointers | Real-time Data Deduplication / Merging | Medium | 🛑 Todo | [View](./src/main/java/com/engine/phase1_foundations/two_pointers/PATTERN_BLUEPRINT.md) |
| **Phase 1** | Linked Lists & Pointers | LRU Cache / MemTable Compactor | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase1_foundations/linked_list/PATTERN_BLUEPRINT.md) |
| **Phase 1** | Binary Search | LSM-Tree SSTable Index Lookup | Medium | 🔄 In Progress | [View](./src/main/java/com/engine/phase1_foundations/binary_search/PATTERN_BLUEPRINT.md) |
| **Phase 2** | Monotonic Stack | Event Metric Streaming (Next Max Spike) | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase2_structural/monotonic_stack/PATTERN_BLUEPRINT.md) |
| **Phase 2** | Heaps / PQ | Distributed Top-K Heavy Hitters Tracker | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase2_structural/heaps_and_priority/PATTERN_BLUEPRINT.md) |
| **Phase 2** | Trie | Longest Prefix Match (IP Router) | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase2_structural/trie/PATTERN_BLUEPRINT.md) |
| **Phase 2** | Segment Tree | Telemetry Metric Range Query Engine | Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase2_structural/segment_tree/PATTERN_BLUEPRINT.md) |
| **Phase 3** | Graph BFS/DFS | Microservice Call Graph Trace Optimizer | Medium | 🛑 Todo | [View](./src/main/java/com/engine/phase3_distributed/graph_traversals/PATTERN_BLUEPRINT.md) |
| **Phase 3** | Topological Sort | Distributed Task Scheduler / CI-CD Engine | Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase3_distributed/topological_sort/PATTERN_BLUEPRINT.md) |
| **Phase 3** | Union-Find (DSU) | Distributed Network Partition Detector | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase3_distributed/union_find/PATTERN_BLUEPRINT.md) |
| **Phase 3** | Dijkstra / Shortest Path | Service Mesh RPC Traffic Routing | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase3_distributed/shortest_paths/PATTERN_BLUEPRINT.md) |
| **Phase 4** | Dynamic Programming | Cloud Instance Load Balancer | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase4_optimization/dynamic_programming/PATTERN_BLUEPRINT.md) |
| **Phase 4** | Backtracking | SQL Relational Query Execution Planner | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase4_optimization/backtracking/PATTERN_BLUEPRINT.md) |
| **Phase 4** | Greedy / Intervals | Thread Pool Task Scheduler (SJF/EDF) | Medium | 🛑 Todo | [View](./src/main/java/com/engine/phase4_optimization/greedy_intervals/PATTERN_BLUEPRINT.md) |
| **Phase 4** | Bit Manipulation / Math | Bloom Filter & HyperLogLog Estimation | Medium / Hard | 🛑 Todo | [View](./src/main/java/com/engine/phase4_optimization/bit_manipulation/PATTERN_BLUEPRINT.md) |

*Status options: 🟢 Mastered | 🔄 In Progress | 🛑 Todo*

---

## 🖥️ Interactive Study Dashboard

The repository includes a modern, interactive React + Vite single-page dashboard to help you visualize pointer states, track your problem ladders, and access Java optimization cheatsheets.

### 1. Running Locally
To launch the dashboard on your machine:
```bash
cd dashboard
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

### 2. Live GitHub Pages Site
A GitHub Actions workflow is fully configured in [.github/workflows/deploy.yml](file:///Users/joe/Documents/projects/DSA-prep/.github/workflows/deploy.yml).

Whenever you push commits to the `main` branch:
1. GitHub Actions automatically checks out the repository.
2. It builds the production-ready static site inside `dashboard/dist/`.
3. It deploys the build artifact to **GitHub Pages**.
4. You can access the live interactive site publicly at:
   **`https://J-Rebs.github.io/DSA-prep/`**
*(Note: To enable this on GitHub, go to your repository settings on GitHub -> Pages -> Build and deployment -> Source: set to "GitHub Actions".)*

