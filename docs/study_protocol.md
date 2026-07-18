# Study Protocol & Recalibration Guide

This document defines the learning framework, target problem volume, weekly evaluation protocol, and recalibration rules for the DSA & System Design Engine.

---

## 🎯 Target Problem Volume: The 11-Problem Rule

To master an algorithmic pattern without getting stuck in a cycle of endless grinding, aim for **11 highly targeted problems** per pattern. This volume is structured as a progressive difficulty ladder:

### The Mastery Ladder
```text
  [Level 3: Scale & Boundary Stress]  -->  2 Hard problems (Extreme bounds, overflow, quants tier)
                  ▲
  [Level 2: Systems Integration]       -->  6 Medium problems (Direct mappings to system design components)
                  ▲
  [Level 1: Structural Invariant]      -->  3 Easy problems (Understand basic template & index bounds)
```

1. **Level 1: Structural Warmups (3 Problems)**
   * **Goal:** Internalize the core pattern invariants and standard template.
   * **Focus:** Pointer movements, base cases, loop termination, and index boundaries.
2. **Level 2: Systems Core (6 Problems)**
   * **Goal:** Implement the direct mapping to real-world infrastructure components (e.g. Sliding Window Rate Limiter).
   * **Focus:** Low-level performance, cache locality, and zero-autoboxing.
3. **Level 3: Scale & Stress (2 Problems)**
   * **Goal:** Handle extreme scale, concurrency, or mathematical constraints (quant/hard interview tier).
   * **Focus:** Minimizing JVM GC pressure, time-limit-exceeded (TLE) prevention, and raw memory optimization.

*Total Curriculum Size: 16 Patterns × 11 Problems = 176 high-quality problems.*

---

## 📊 Weekly Testing Protocol

At the end of each week, schedule a **90-minute timed testing session** to evaluate retention, speed, and efficiency under mock interview constraints.

### 1. Test Setup
* **Coverage:** Select 3 problems:
  * **2 Problems** from the patterns studied during the current week.
  * **1 Problem** selected randomly from a previously mastered pattern (spaced repetition).
* **Constraints:** Timed (90 minutes total), no AI tools, and no documentation lookups. Use the [TestDataGenerator](file:///Users/joe/Documents/projects/DSA-prep/src/test/java/com/engine/TestDataGenerator.java) to generate stress inputs (100,000+ elements) for verification.

### 2. Grading Rubric
Your solution is graded on three criteria:
1. **Algorithmic Correctness (40%):** Passes all functional edge cases, boundary parameters (nulls, empty collections), and integer bounds.
2. **Time Complexity (30%):** Meets optimal asymptotic bounds ($O(N)$ or $O(\log N)$) and passes stress-test scales without TLE.
3. **Memory & JVM Efficiency (30%):** Achieves zero object-boxing in loops, utilizes flat primitive arrays, and maintains a clean GC profile under high throughput.

---

## 🔄 Recalibration & Progress Rules

Based on the weekly test results, update the status in the [README.md](file:///Users/joe/Documents/projects/DSA-prep/README.md) tracker using the following rules:

```mermaid
graph TD
    A[Run Weekly Test] --> B{Calculate Score}
    B -- "Score >= 90%" --> C[Mark as Mastered 🟢]
    B -- "Score 70% - 89%" --> D[Keep as In Progress 🔄]
    B -- "Score < 70%" --> E[Revert to Todo 🛑]

    C --> C1[Advance to Next Phase]
    D --> D1[Optimize GC & Add 2 Stress Tests]
    E --> E1[Re-study Mental Model & Run Walkthroughs]
```

### Recalibration Action Items

* **Grade: Mastered (`🟢 Mastered`)**
  * *Threshold:* Passes all correctness tests + achieves optimal space/time bounds + enforces zero-boxing.
  * *Action:* Update progress tracker to `🟢 Mastered`. Advance to the next phase on the roadmap.
* **Grade: Optimizing (`🔄 In Progress`)**
  * *Threshold:* Passes correctness, but triggers TLE on large scales, or uses boxed classes (GC overhead).
  * *Action:* Keep status as `🔄 In Progress`. Rewrite code to use flat primitive arrays and pre-allocated buffers. Run stress tests again.
* **Grade: Remedial (`🛑 Todo`)**
  * *Threshold:* Struggles with the core invariant logic, hits stack overflow, or fails multiple basic edge cases.
  * *Action:* Revert status to `🛑 Todo`. Open the pattern's `PATTERN_BLUEPRINT.md` and read the *Core Structural Trick (Mental Model)* again. Request a step-by-step trace from AI to inspect index movements.

---

## 🔒 Test Isolation & Self-Containment Invariant

To ensure that any problem can run its tests in total isolation without side-effects or dependencies:
1. **No Shared Mutable State:** Test classes must never rely on shared static mutable fields. Every test run must be stateless.
2. **Fresh Data Generation:** Always use the `TestDataGenerator` to supply unique, fresh arrays or parameters for each individual test execution.
3. **Independent Execution:** Each package (e.g. `phase1_foundations/sliding_window`) maintains its own self-contained source files and tests, meaning you can compile and run a single test class (e.g. `SlidingWindowTest`) directly from your IDE without compiling or invoking the rest of the test suite.

---

## 🤖 The AI Mentor Assessment & Code Audit Loop

Every time you submit a solution for a problem in the mastery ladder for the first time, we will run the **AI Mentor Audit**. This loop is designed to diagnose weak spots, provide targeted learning calibrations, and ensure production readiness.

### 1. The Audit Criteria
When you paste your solution, the AI will evaluate:
* **Edge-Case Resilience:** Did you handle null references, empty/single-element bounds, duplicates, and integer overflow/underflow points?
* **GC & Performance Footprint:** Did you enforce zero-boxing? Are operations running in optimal space/time bounds without triggering unnecessary object creations?
* **Clean Code & Invariant Documentation:** Is the loop invariant clearly documented? Is the code readability aligned with production library standards?

### 2. Diagnosis & Calibrations
If the AI detects a conceptual gap or optimization opportunity, it will:
* **Diagnose the Weak Spot:** Explain *why* the implementation falls short (e.g. index off-by-ones, unnecessary auto-boxing).
* **Provide a Micro-Drill:** Give you a specific target trace-debug exercise (e.g. "Trace this input manually through your loop: keys = [10], target = 5") to help you visualize the bug, rather than just giving you the corrected code.
* **Explain the Invariant:** Focus on the mental models or mathematical invariants before you re-submit.
