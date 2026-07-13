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

## 3. Monotonic Stack $\rightarrow$ Event Metric Streaming (Next Max Spike)

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

## 4. Heaps / Priority Queue $\rightarrow$ Distributed Top-K Heavy Hitters

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

## 5. Graph BFS/DFS $\rightarrow$ Microservice Call Graph Trace Optimizer

### Macro System Component: Distributed Tracing & Critical Path Analysis
Distributed tracing platforms (e.g., OpenTelemetry, Jaeger) reconstruct system requests as directed acyclic graphs (DAGs) representing microservice RPC calls. To optimize system latency, the analyzer must traverse these traces to detect circular dependencies (deadlocks), calculate cumulative path delays, or locate service performance bottlenecks.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Graph BFS/DFS** traversals.
* **Mechanism:**
  * **DFS** is used to traverse deep paths to find cycles or trace critical execution branches, and calculate cumulative trace dependencies.
  * **BFS** is used to analyze service depth profiles, breadth of fan-outs (parallel downstream calls), and evaluate network hop counts.
  * In Java, nodes are represented using flat primitive arrays representing adjacency lists (using integer offsets) to prevent memory fragmentation associated with millions of nested node objects.

---

## 6. Topological Sort $\rightarrow$ Distributed Task Scheduler & CI/CD Pipelines

### Macro System Component: CI/CD Pipeline Orchestrators & DAG Run Engines
CI/CD engines (e.g., Jenkins, GitLab CI, Argo) and orchestrators (e.g., Airflow) process workflows represented as Directed Acyclic Graphs (DAGs). Before launching, the system must determine a valid execution sequence where all dependency tasks run prior to their dependees.

### How it leverages the DSA Pattern
* **Algorithmic Primitive:** **Topological Sort** (Kahn's Algorithm or DFS post-order).
* **Mechanism:**
  * **Kahn's Algorithm:** We compute the *in-degree* (number of incoming dependency links) for every task node.
  * Nodes with an in-degree of 0 are added to a processing queue (since they have no dependencies).
  * As tasks complete or are dequeued, we decrement the in-degree of their neighbor task nodes. If any neighbor's in-degree reaches 0, it is queued for execution.
  * If the total number of sorted elements is less than the total tasks, a dependency cycle is detected (e.g., Task A depends on Task B, which depends on Task A), preventing execution deadlock.
