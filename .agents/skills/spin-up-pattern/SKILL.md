---
name: spin-up-pattern
description: Use this skill to initialize and scaffold a new DSA pattern in the DSA & System Design Engine repository. This generates the 9-problem mastery ladder, creates skeleton Java source files, sets up JUnit 5 parameterized test files, and registers the pattern with the interactive study dashboard.
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
2. **Define the 9-Problem Ladder:** Outline exactly 9 problems in sequential order:
   * **2 Easy Warmups:** Basic pointer swaps or array scans (e.g., LeetCode Easy equivalents).
   * **5 Medium Integrations:** Directly mapped to system bottlenecks (e.g., Rate limiters, buffer compactors, routers).
   * **2 Hard Stress:** Multi-partition search, scale limits, or mathematical bounds.
3. **Map to Systems:** For each problem, define the **Macro System Component** and the **Mental Model Invariant**.

---

## 2. Register in the Web Dashboard Database

1. Open [curriculum.json](file:///Users/joe/Documents/projects/DSA-prep/dashboard/src/curriculum.json).
2. Locate the target Phase object.
3. Add or update the concept object inside the `concepts` array:
   ```json
   {
     "id": "concept_id",
     "name": "Concept Display Name",
     "component": "Target System Component Name",
     "status": "🛑 Todo",
     "problems": [
       { "name": "Problem 1 Name", "difficulty": "Easy", "status": "🛑 Todo" },
       ...
       { "name": "Problem 9 Name", "difficulty": "Hard", "status": "🛑 Todo" }
     ]
   }
   ```

---

## 3. Update Documentation Mappings

1. **System Design Mappings:** Open [system_design_mappings.md](file:///Users/joe/Documents/projects/DSA-prep/docs/system_design_mappings.md) and append a section containing the deep-dive explanation of the pattern (Macro Component, Algorithmic Primitive, Mechanism, and performance optimizations).
2. **Master README:** Open [README.md](file:///Users/joe/Documents/projects/DSA-prep/README.md) and update the progress tracker table to include the new row.

---

## 4. Scaffold Source & Blueprint Files

1. Create the pattern directory: `src/main/java/com/engine/<phase_dir>/<pattern_dir>/`.
2. Create [PATTERN_BLUEPRINT.md](file:///Users/joe/Documents/projects/DSA-prep/docs/templates/java_dsa_blueprint.md) inside that directory outlining:
   * **1. System Design Mapping**
   * **2. High-Yield Performance Tricks (Java Specific)**
   * **3. The Core Structural Trick (Mental Model)**
   * **4. The 9-Problem Mastery Ladder** (with the status checklist).
3. Create the skeleton Java source file `<PatternClassName>.java` in that directory declaring the signatures of all 9 methods with placeholder return values (e.g., `-1`, `null`, `0.0`) and clear JavaDocs.

---

## 5. Scaffold Parameterized Test Files

1. Create the test directory: `src/test/java/com/engine/<phase_dir>/<pattern_dir>/`.
2. Create `<PatternClassName>Test.java` containing:
   * JUnit 5 Parameterized Tests (`@ParameterizedTest`, `@MethodSource`) for all 9 problems.
   * A large-scale stress test (100,000+ inputs generated via [TestDataGenerator](file:///Users/joe/Documents/projects/DSA-prep/src/test/java/com/engine/TestDataGenerator.java)) verifying time limits and OOM limits.
   * Boundary checks (nulls, overflows, empty arrays).

---

## 6. Commit & Push

1. Stage all created files (`git add .`).
2. Commit with a descriptive message (e.g., `git commit -m "Scaffold [Pattern Name] 9-problem ladder"`).
3. Push to `main` branch.
