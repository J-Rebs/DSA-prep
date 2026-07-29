# Pattern Blueprint: Graph BFS/DFS & Grid Traversals

## 1. System Design Mapping
* **Macro System Component:** Call Graph Trace Optimizer & Distributed Network Topology Engine
* **How it leverages this DSA Pattern:**
  Production infrastructure components (such as API gateways, distributed call graph trace visualizers, RPC network topology analyzers, and fault propagation simulators) model services and network cells as graphs. Traversing 2D spatial grids or adjacency list graphs via BFS/DFS finds connected subnets, shortest transformation/routing paths, and cascading failure radii.
* **Data Flow Architecture:**
  ```text
  Graph / 2D Grid ──> [Queue (BFS) / Stack (DFS) + Visited Marker] ──> [Neighbor Exploration] ──> Shortest Path / Cluster Output
  ```

## 2. High-Yield Performance Tricks (Java Specific)
* **In-Place Grid Marking:**
  When traversing 2D grids (like Flood Fill or Island Count), modify grid values in-place (e.g. set `'1'` to `'0'`) to eliminate $O(M \times N)$ boolean `visited[][]` array allocations.
* **Multi-Source BFS for Simultaneous Propagation:**
  For problems modeling simultaneous propagation (e.g. Rotting Oranges, 01 Matrix distance), enqueue all initial source nodes into the BFS queue at level 0 up front instead of running separate BFS passes.
* **Direction Array Vectors:**
  Use a static direction offset array `private static final int[][] DIRS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};` to avoid redundant boundary condition branches.

---

## 3. The Core Structural Invariants (Mental Models):
* **BFS Level-Order Layering:** BFS guarantees the shortest path on unweighted graphs because it explores nodes layer by layer (level-by-level distance).
* **DFS Backtracking Boundary Search:** DFS naturally explores connected components to maximum depth; useful for region capture, boundary checks, and path enumerations.
* **Bi-Directional BFS:** When searching shortest paths from `start` to `end` (like Word Ladder), alternating search from both small sets (`beginSet` vs `endSet`) reduces search space from $O(b^d)$ to $O(b^{d/2})$.

---

## 4. The 11-Problem Mastery Ladder

### Phase 1: Easy Warmups
1. **[Easy] Flood Fill Grid (Pixel Color Ingest)**
   * *System Mapping:* Distributed canvas & image segmentation rasterizer.
   * *Description:* Change starting cell and connected same-colored cells to target color.
2. **[Easy] Island Count (Connected Microservice Cluster Locator)**
   * *System Mapping:* Network cluster topology analyzer (counting isolated server clusters).
   * *Description:* Count number of 4-directionally connected islands of 1s in a 2D binary grid.
3. **[Easy] Max Island Area (Largest Data Center Subnet Size)**
   * *System Mapping:* Infrastructure capacity analyzer (finding largest connected server cluster).
   * *Description:* Find maximum area of an island in a 2D binary grid.

### Phase 2: Medium System Integration
4. **[Medium] Pacific Atlantic Water Flow (Bi-directional Network Ingress)**
   * *System Mapping:* Multi-region traffic routing validator (nodes reaching both ocean backbones).
   * *Description:* Find coordinates where water can flow to both Pacific and Atlantic oceans.
5. **[Medium] Surrounded Regions (Deadlock Memory Cell Reclaimer)**
   * *System Mapping:* Deadlock memory cell reclaimer (capturing 4-way enclosed unreferenced nodes).
   * *Description:* Capture all 'O' regions completely surrounded by 'X'.
6. **[Medium] Rotting Oranges / Outage Spread (Cascading Latency Spike Propagation)**
   * *System Mapping:* Network outage propagation & epidemic latency spike simulation.
   * *Description:* Min time for all 1s to become 2s via 4-directional BFS propagation.
7. **[Medium] 01 Matrix Nearest Distance (Service Mesh RPC Latency Map)**
   * *System Mapping:* Service Mesh RPC Latency Map (finding distance to nearest zero node).
   * *Description:* Find distance of nearest 0 for each cell in a binary matrix.
8. **[Medium] Word Ladder (RPC Method Schema Mutation Path)**
   * *System Mapping:* API schema transformation planner (shortest mutation path from source word to target word).
   * *Description:* Find shortest transformation sequence length from beginWord to endWord.
9. **[Medium] Clone Graph (Distributed RPC Microservice Call-Graph Replicator)**
   * *System Mapping:* Microservice call graph trace duplicator.
   * *Description:* Create a deep copy of a connected undirected graph.

### Phase 3: Hard Scale & Stress
10. **[Hard] Word Ladder II (All Shortest Transformation Paths)**
    * *System Mapping:* Fault-tolerant multi-path RPC mutation sequence planner.
    * *Description:* Find all shortest transformation sequences from beginWord to endWord.
11. **[Hard] Shortest Path in 2D Matrix with Obstacle Elimination**
    * *System Mapping:* Router packet bypass allocator (shortest path bypassing up to K firewall rules).
    * *Description:* Find shortest path from (0,0) to (M-1,N-1) stepping over up to K obstacle walls.
