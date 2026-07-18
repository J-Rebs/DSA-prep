---
name: spin-up-pattern
description: Use this skill to initialize and scaffold a new DSA pattern in the DSA & System Design Engine repository. This generates the 11-problem mastery ladder, creates skeleton Java source files, sets up JUnit 5 parameterized test files, and registers the pattern with the interactive study dashboard.
---

# Skill: Spin Up Pattern

Use this skill whenever the user asks you to "spin up", "initialize", "add", or "scaffold" a new DSA pattern (e.g. Monotonic Stack, Trie, Dynamic Programming) in this repository.

Follow this systematic workflow:

---

## 1. Plan & Structure Mappings

1. **Phase Classification:** Determine which Phase the pattern belongs to:
   * `Phase 1: Foundations`
   * `Phase 2: Structural`
   * `Phase 3: Distributed & Networks`
   * `Phase 4: Optimization & Search`
2. **Define the 11-Problem Ladder:** Outline exactly 11 problems in sequential order:
   * **3 Easy Warmups:** Basic pointer swaps or array scans (e.g., LeetCode Easy equivalents).
   * **6 Medium Integrations:** Directly mapped to system bottlenecks (e.g., Rate limiters, buffer compactors, routers).
   * **2 Hard Stress:** Multi-partition search, scale limits, or mathematical bounds.
3. **Map to Systems:** For each problem, define the **Macro System Component** and the **Mental Model Invariant**.

---

## 2. Register in the Web Dashboard Database

1. Open [curriculum.json](file:///Users/joe/Documents/projects/DSA-prep/dashboard/src/curriculum.json).
2. Locate the target Phase object.
3. Determine the sequential learning order number `XX` (from `01` to `16`) for the pattern.
4. Add or update the concept object inside the `concepts` array. **Note: The ID must use the prefix `pXX_` (e.g. `p05_monotonic_stack`) to ensure it sorts alphabetically in the IDE while remaining a valid Java package identifier!**
   ```json
   {
     "id": "pXX_concept_id",
     "name": "Concept Display Name",
     "component": "Target System Component Name",
     "status": "🛑 Todo",
     "problems": [
       { "name": "Problem 1 Name", "difficulty": "Easy", "status": "🛑 Todo" },
       ...
       { "name": "Problem 11 Name", "difficulty": "Hard", "status": "🛑 Todo" }
     ]
   }
   ```

---

## 3. Update Documentation Mappings

1. **System Design Mappings:** Open [system_design_mappings.md](file:///Users/joe/Documents/projects/DSA-prep/docs/system_design_mappings.md) and append a section containing the deep-dive explanation of the pattern (Macro Component, Algorithmic Primitive, Mechanism, and performance optimizations).
2. **Master README:** Open [README.md](file:///Users/joe/Documents/projects/DSA-prep/README.md) and update the progress tracker table to include the new row.

---

## 4. Scaffold Source & Blueprint Files

1. Create the pattern directory: `src/main/java/com/engine/<phase_dir>/pXX_<concept_id>/` (e.g. `src/main/java/com/engine/phase2_structural/p05_monotonic_stack/`).
2. Create `PATTERN_BLUEPRINT.md` inside that directory outlining the system mappings, Java tricks, loop invariants, and problem checklist.
3. Create the skeleton Java source file `<PatternClassName>.java` in that directory declaring:
   * The package: `package com.engine.<phase_dir>.pXX_<concept_id>;`
   * Signatures for all 11 methods in the mastery ladder with placeholder return values.

---

## 5. Scaffold Parameterized Test Files

1. Create the test directory: `src/test/java/com/engine/<phase_dir>/pXX_<concept_id>/` (e.g. `src/test/java/com/engine/phase2_structural/p05_monotonic_stack/`).
2. Create `<PatternClassName>Test.java` in that directory declaring:
   * The package: `package com.engine.<phase_dir>.pXX_<concept_id>;`
   * Imports for the source classes, test generator, JUnit `@Nested`, and `com.sun.management.ThreadMXBean` for memory tracking.
   * Parameterized tests for all 11 problems.
   * A nested class `@Nested class RigorousGatekeeper` containing:
     * Boundary validation tests (empty inputs, null values, out-of-bound arguments).
     * Time complexity stress tests using `assertTimeoutPreemptively` to guarantee $O(N)$ behavior.
     * Zero-allocation memory checks using `ThreadMXBean` to ensure no objects are allocated in critical paths.


---

## 6. Commit & Push

1. Stage all created files (`git add .`).
2. Commit with a descriptive message (e.g., `git commit -m "Scaffold [Pattern Name] 9-problem ladder"`).
3. Push to `main` branch.
