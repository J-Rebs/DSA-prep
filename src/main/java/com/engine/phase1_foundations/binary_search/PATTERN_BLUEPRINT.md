# Pattern Blueprint: Binary Search

## 1. System Design Mapping
* **Macro System Component:** LSM-Tree SSTable Index Lookup
* **How it leverages this DSA Pattern:**
  SSTable block indexes contain keys sorted in lexicographical order. Searching for a specific database key uses binary search on the index blocks to locate the appropriate offset on disk.
* **Data Flow Architecture:**
  ```text
  Key Query ──> [Binary Search on Block Index] ──> Identify Block Offset ──> Fetch Disk Block
  ```

## 2. High-Yield Performance Tricks (Java Specific)
* **Memory & Collection Strategy:**
  Avoid object wrappers. Store indices as flat primitive `int[]` or `byte[]` arrays.
* **Midpoint Calculation:**
  To prevent integer overflow errors, use the unsigned right shift operator: `int mid = (low + high) >>> 1;` instead of `(low + high) / 2`.
* **Time/Space Constraints:**
  * Time Complexity: $O(\log N)$
  * Space Complexity: $O(1)$ auxiliary space.

## 3. The Core Structural Trick (Mental Model)
> Continually half the search space based on sorted properties. The invariant is that the target key resides in the interval `[low, high]`.

## 4. Production-Ready Java Blueprint

### Code Blocks
```java
// SSTableIndexLookup.java
package com.engine.phase1_foundations.binary_search;

public final class SSTableIndexLookup {
    // 1. Lower Bound Binary Search: findBlockOffset
    public static int findBlockOffset(long[] keys, int[] offsets, long targetKey) {
        int low = 0;
        int high = keys.length - 1;
        int ans = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (keys[mid] <= targetKey) {
                ans = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return ans == -1 ? -1 : offsets[ans];
    }

    // 2. Rotated Buffer Search: searchRotatedIndex
    public static int searchRotatedIndex(long[] keys, long targetKey) {
        int low = 0;
        int high = keys.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (keys[mid] == targetKey) return mid;
            if (keys[low] <= keys[mid]) {
                if (targetKey >= keys[low] && targetKey < keys[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                if (targetKey > keys[mid] && targetKey <= keys[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }

    // 3. Cross-Partition Median Find: findMedianKey
    public static double findMedianKey(long[] keysA, long[] keysB) {
        if (keysA.length > keysB.length) return findMedianKey(keysB, keysA);
        int m = keysA.length, n = keysB.length;
        int low = 0, high = m;
        while (low <= high) {
            int i = (low + high) >>> 1;
            int j = ((m + n + 1) / 2) - i;
            long maxLeftA = (i == 0) ? Long.MIN_VALUE : keysA[i - 1];
            long minRightA = (i == m) ? Long.MAX_VALUE : keysA[i];
            long maxLeftB = (j == 0) ? Long.MIN_VALUE : keysB[j - 1];
            long minRightB = (j == n) ? Long.MAX_VALUE : keysB[j];
            if (maxLeftA <= minRightB && maxLeftB <= minRightA) {
                if ((m + n) % 2 != 0) {
                    return Math.max(maxLeftA, maxLeftB);
                } else {
                    return (Math.max(maxLeftA, maxLeftB) + Math.min(minRightA, minRightB)) / 2.0;
                }
            } else if (maxLeftA > minRightB) {
                high = i - 1;
            } else {
                low = i + 1;
            }
        }
        throw new IllegalArgumentException("Inputs not sorted.");
    }
}
```

### Performance Analysis
1. **Zero-Boxing Optimization:** All methods consume and return primitives (`long`, `int`, `double`). This ensures that zero heap allocations are triggered during calculations, eliminating GC overhead.
2. **Unsigned Midpoint Math:** The midpoint calculation uses `(low + high) >>> 1` to prevent potential integer overflow bugs that happen when `low + high` exceeds `Integer.MAX_VALUE`.
3. **Cache Line Friendly:** Operates directly on contiguous parallel arrays (`keys[]` and `offsets[]`), which improves CPU cache hit ratios during random binary leaps.

