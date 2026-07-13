# Macro-to-Micro System Design Mappings

This document provides architectural deep dives connecting high-throughput system components (macro level) to their core DSA algorithmic primitives (micro level).

---

## 1. Sliding Window $\rightarrow$ Rate Limiter (Sliding Window Counter)

### Macro System Component: API Gateway Rate Limiting Middleware
API gateways and distributed rate limiters (e.g., in Netflix, Stripe, or AWS API Gateway) must enforce request rate limits (e.g., 100 requests per minute) per API client. Simple fixed window rate limiters suffer from burst traffic vulnerabilities at window boundaries (e.g., doubling the rate at the boundary). A **Sliding Window Counter** addresses this by smoothing out boundaries, but tracking every single request's timestamp can consume excessive memory.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** The **Sliding Window** pattern is applied to track the active window of timestamps.
* **Mechanism:**
  * To track requests, a sliding window of timestamps is kept. The queue stores timestamps in ascending order.
  * When a new request arrives at `current_time`, we evict all timestamps from the head of the queue that are older than `current_time - window_size`.
  * If the remaining size of the queue is less than the limit, the request is allowed, and `current_time` is appended to the queue.
  * **Throughput Optimization:** Instead of a heavy `LinkedList` or `PriorityQueue`, a circular buffer/array deque (`long[]`) is pre-allocated to size `LIMIT`. Head and tail pointers are advanced using modulo arithmetic, giving $O(1)$ operations with zero garbage collection allocations on request validation.

---

## 2. Two Pointers $\rightarrow$ Real-time Data Deduplication & Merging

### Macro System Component: Stream Compaction & LSM-Tree Merge
In high-throughput stream processing systems (e.g., Apache Flink) or write-heavy databases (e.g., Cassandra's LSM-Tree compaction), sorted streams of data or sorted files (SSTables) must be continuously merged and deduplicated. Allocating new structures during this process causes massive GC pauses and degrades throughput.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** The **Two Pointers** pattern allows in-place or single-pass traversal.
* **Mechanism:**
  * When compaction merges two sorted streams, we maintain two pointer indices pointing to the heads of both streams.
  * For in-place deduplication of a single batch/buffer, we use a *slow-runner* and a *fast-runner* pointer.
  * The fast-runner scans the array of incoming events. Whenever it finds an event that differs from the last unique event (at the slow-runner position), the slow-runner is advanced, and the element is copied in-place.
  * This guarantees that data is processed in $O(N)$ time complexity and $O(1)$ auxiliary space complexity, entirely avoiding new object allocations.

---

## 3. Binary Search $\rightarrow$ LSM-Tree SSTable Index Lookup

### Macro System Component: Log-Structured Merge-Tree (LSM-Tree) SSTable Index Lookup
High-throughput databases (e.g., RocksDB, Cassandra) use SSTables (Sorted String Tables) stored on disk. When a read query arrives for a key, the engine searches metadata block indices in memory to locate the file block where the key resides. Scanning the index linearly would be too slow ($O(N)$ per lookup).

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Binary Search** over sorted indexes.
* **Mechanism:**
  * SSTable block index contains keys and their corresponding file offsets, sorted lexicographically.
  * The engine uses Binary Search to find the target block index offset in $O(\log N)$ time.
  * **Quant/Performance Trick:** To optimize cache line utilization, engines structure key tables as flat contiguous byte arrays, implementing index-math-based binary search bounds (`mid = (low + high) >>> 1`) to prevent overflow and maximize instruction pipeline efficiency.

---

## 4. Monotonic Stack $\rightarrow$ Event Metric Streaming (Next Max Spike)

### Macro System Component: Real-time Metric Monitoring & Anomaly Detection
In telemetry infrastructure (e.g., Prometheus, Datadog), system agents process a continuous stream of time-series metric updates (e.g., CPU utilization, memory spikes). To detect anomalous events or analyze resource exhaustion, the engine must instantly identify the next higher utilization spike or calculate the duration until a resource usage record is broken.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** A **Monotonic Decreasing Stack** stores indices of the metrics stream.
* **Mechanism:**
  * As metrics arrive, the stack maintains a list of index numbers representing metric values in strictly decreasing order.
  * When a new metric value arrives that is larger than the value at the index on the top of the stack, we have found the "next maximum spike" for that top element.
  * We pop indices from the stack and record the time-difference until the stack is either empty or contains a larger metric value.
  * This allows the system to process metrics in $O(N)$ cumulative time rather than $O(N^2)$ brute force, giving microsecond-level latency per event.

---

## 5. Heaps / Priority Queue $\rightarrow$ Distributed Top-K Heavy Hitters

### Macro System Component: Real-time DDoS Mitigation & Trending Tracking
In network security or telemetry monitoring, engines must track the top $K$ most frequent IP addresses or requested URLs (e.g., detecting heavy hitters during a DDoS attack). In high-volume systems, keeping a complete map of all unique elements is memory-prohibitive.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** A **Min-Heap (Priority Queue) of size K** coupled with a frequency sketch.
* **Mechanism:**
  * A Count-Min Sketch or a bounded Hash Map processes the stream and estimates the frequencies of elements.
  * We maintain a Min-Heap of size $K$ containing the current top $K$ candidates and their estimated counts.
  * When an element's count is updated, if it exceeds the root of the Min-Heap, we update or push it onto the heap, and pop the root (which represents the element with the lowest count in the top-K list).
  * **Java GC Strategy:** Standard `PriorityQueue` triggers auto-boxing and object allocation per comparison. For high-throughput engines, we use a custom primitive array-based min-heap where elements are indexed using integers, reducing latency and memory footprint.

---

## 6. Trie $\rightarrow$ Longest Prefix Match (IP Routing)

### Macro System Component: IP Router Forwarding Information Base (FIB)
IP routers (e.g., Juniper, Cisco, or software routers like BGP daemons) must inspect incoming packet headers and locate the matching target destination route. Since IP addresses are partitioned into subnets of varying prefix lengths (e.g., `192.168.1.0/24` and `192.168.0.0/16`), the router must execute a **Longest Prefix Match (LPM)** lookup on each routing entry instantly.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Prefix Tree / Trie**
* **Mechanism:**
  * Router destinations are indexed in a bit-trie (binary trie where nodes branch on `0` or `1`).
  * The lookup traverses the trie bit-by-bit from the most significant bit of the IP address. The last node visited that represents a valid prefix defines the target destination route.
  * **Quant/Performance Trick:** The trie nodes are stored in contiguous memory structures (e.g., Luleå trie algorithm) using bitmasks to represent child array offsets to prevent cache misses.

---

## 7. Segment Tree / Fenwick Tree $\rightarrow$ Telemetry Metric Range Query Engine

### Macro System Component: Time-Series Database (TSDB) Segment Aggregates
TSDBs (e.g., InfluxDB) must process aggregations (e.g., total requests, max latency) over arbitrary, dynamic time range queries (e.g., "return request count between timestamp $T_1$ and $T_2$") in sub-millisecond speeds. Calculating range sums or range maximums on the fly by scanning raw data is far too slow ($O(N)$).

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Segment Tree** or **Fenwick Tree (Binary Indexed Tree)**
* **Mechanism:**
  * The time series data is partitioned into dynamic intervals. A Segment Tree stores interval summary metrics (sums, max, min) in a balanced tree representation.
  * Reading a range query accesses at most $O(\log N)$ nodes to resolve the interval range aggregate.
  * **Quant/Performance Trick:** Instead of pointer-chased trees, Segment Trees are stored as flat 1D arrays (`tree[]`) where node indices are calculated using bit-shifts (`leftChild = index << 1`, `rightChild = (index << 1) | 1`).

---

## 8. Graph BFS/DFS $\rightarrow$ Microservice Call Graph Trace Optimizer

### Macro System Component: Distributed Tracing & Critical Path Analysis
Distributed tracing platforms (e.g., OpenTelemetry, Jaeger) reconstruct system requests as directed acyclic graphs (DAGs) representing microservice RPC calls. To optimize system latency, the analyzer must traverse these traces to detect circular dependencies (deadlocks), calculate cumulative path delays, or locate service performance bottlenecks.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Graph BFS/DFS** traversals.
* **Mechanism:**
  * **DFS** is used to traverse deep paths to find cycles or trace critical execution branches, and calculate cumulative trace dependencies.
  * **BFS** is used to analyze service depth profiles, breadth of fan-outs (parallel downstream calls), and evaluate network hop counts.
  * In Java, nodes are represented using flat primitive arrays representing adjacency lists (using integer offsets) to prevent memory fragmentation associated with millions of nested node objects.

---

## 9. Topological Sort $\rightarrow$ Distributed Task Scheduler & CI/CD Pipelines

### Macro System Component: CI/CD Pipeline Orchestrators & DAG Run Engines
CI/CD engines (e.g., Jenkins, GitLab CI, Argo) and orchestrators (e.g., Airflow) process workflows represented as Directed Acyclic Graphs (DAGs). Before launching, the system must determine a valid execution sequence where all dependency tasks run prior to their dependees.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Topological Sort** (Kahn's Algorithm or DFS post-order).
* **Mechanism:**
  * **Kahn's Algorithm:** We compute the *in-degree* (number of incoming dependency links) for every task node.
  * Nodes with an in-degree of 0 are added to a processing queue (since they have no dependencies).
  * As tasks complete or are dequeued, we decrement the in-degree of their neighbor task nodes. If any neighbor's in-degree reaches 0, it is queued for execution.
  * If the total number of sorted elements is less than the total tasks, a dependency cycle is detected (e.g., Task A depends on Task B, which depends on Task A), preventing execution deadlock.

---

## 10. Union-Find (DSU) $\rightarrow$ Distributed Consensus Network Partition Detector

### Macro System Component: Cluster Membership & Network Split Tracker
In distributed datastores (e.g., Elasticsearch, Cassandra), nodes frequently join, leave, or lose connectivity. The orchestrator must track which nodes are connected to each other to detect "split-brain" partition scenarios and decide if a quorum can form.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Disjoint Set Union (DSU) / Union-Find** with Path Compression.
* **Mechanism:**
  * Each node is initialized as its own set. When a heartbeat or network ping confirms connection between Node A and Node B, `union(A, B)` is called.
  * Finding if Node A can talk to Node C becomes a fast `find(A) == find(C)` check.
  * **Quant/Performance Trick:** Using path compression (`parent[i] = find(parent[i])`) and union-by-rank, DSU queries run in amortized near-constant time ($O(\alpha(N))$ where $\alpha$ is the Inverse Ackermann function), enabling high-frequency network connectivity checks.

---

## 11. Dijkstra's / Shortest Path $\rightarrow$ Service Mesh Routing Engine

### Macro System Component: Envoy Router Service Mesh Traffic Controller
In microservice meshes (e.g., Istio/Envoy), traffic must be routed between containers. To minimize latency, the router maps container-to-container connections as a weighted directed graph where edge weights represent network delays (pings). The router must find the fastest latency path to direct traffic.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Dijkstra's Algorithm** (Single-Source Shortest Paths)
* **Mechanism:**
  * The routing table maintains pings as edge weights. Dijkstra's algorithm tracks shortest paths from the source container.
  * Using a binary heap as the priority queue, Dijkstra's resolves the minimum latency path to the target container in $O(E \log V)$ time.
  * **Quant/Performance Trick:** The router updates path weights dynamically via delta-updates rather than running full Dijkstra searches from scratch.

---

## 12. Dynamic Programming $\rightarrow$ Load Balancer Bin-Packing Resource Optimizer

### Macro System Component: Cloud Instance Scheduler & Task Bin-Packing
Cloud orchestration platforms (e.g. Kubernetes, AWS ECS) allocate containers/tasks with specific memory and CPU requests onto virtual machines. To minimize VM usage (cloud costs), the orchestrator must pack tasks efficiently. This is the classic NP-hard Bin Packing / Knapsack optimization problem.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Dynamic Programming (Knapsack & Partitioning)**
* **Mechanism:**
  * The allocation engine uses a 2D or 1D DP array representing maximum resource utilization capacities.
  * By solving subproblems (e.g., maximum task value packable under CPU capacity $C$ and memory capacity $M$), the engine computes the optimal combination of tasks to run on a single host.
  * For low latency, the DP state table is mapped directly to a flat pre-allocated primitive array, avoiding nested hash maps or collection arrays.

---

## 13. Backtracking $\rightarrow$ SQL Query Execution Planner (Optimizer)

### Macro System Component: Relational Database Query Optimizer
When a complex SQL query containing multiple `JOIN` clauses is submitted, the relational database planner (e.g., PostgreSQL, MySQL) must determine the order in which tables are joined. The search space of potential join combinations grows exponentially, and picking the wrong plan can delay execution by hours.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Backtracking & Pruning**
* **Mechanism:**
  * The optimizer traverses the join permutations tree using recursion.
  * At each level, the planner computes cost estimations (e.g., CPU, I/O cost).
  * **State Space Pruning:** If a sub-path's estimated cost exceeds the cost of the best full execution path found so far, the optimizer prunes that branch entirely (backtracks early), reducing query planner latency.

---

## 14. Greedy / Intervals $\rightarrow$ Thread Pool Task Scheduler

### Macro System Component: OS/Runtime Task Queue Prioritizer
High-performance scheduler runtimes (e.g., in JVM ForkJoinPool or operating system task queues) must execute intervals of tasks. When tasks overlap in execution time but resource access is exclusive, the scheduler must select the maximum subset of non-overlapping jobs (Interval Scheduling).

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Greedy Algorithm / Interval Scheduling**
* **Mechanism:**
  * Tasks are sorted by their end times. The scheduler greedily picks the task that finishes earliest, freeing up resources as soon as possible.
  * The scheduler iterates through the sorted array, selecting the next task whose start time is greater than or equal to the end time of the last selected task.
  * This guarantees an optimal selection in $O(N \log N)$ time (dominated by sorting).

---

## 15. Bit Manipulation & Math $\rightarrow$ Bloom Filter & HyperLogLog Key Hashing

### Macro System Component: High-Throughput Cache Filters & Cardinality Estimators
High-scale web caches (e.g., Redis, Varnish, Cloudflare CDN) use **Bloom Filters** to quickly check if a key is absent before executing expensive database lookups. **HyperLogLog** cardinality estimators (used by Redis) estimate millions of unique values (e.g., unique IP visits) using fractional bytes of memory.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Bit Manipulation** (Bit masking, Popcount, Leading Zero estimation)
* **Mechanism:**
  * **Bloom Filter:** Hashing a key produces multiple integer index offsets. We execute bitwise OR operations to write bits to a central bitmask array, and bitwise AND to check presence.
  * **HyperLogLog:** Evaluates the number of leading zeros in hashed binary keys using bit-shift logic (`num >>> 24`) and updates registers.
  * **Quant/Performance Trick:** The entire system operates on primitive `long[]` arrays, using bitwise operations (`1L << (index & 63)`) to modify individual bits without memory allocation.
