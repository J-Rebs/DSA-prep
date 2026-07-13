package com.engine.phase1_foundations.binary_search;

/**
 * High-performance SSTable Index Lookup component using Binary Search.
 * Optimized for zero-autoboxing and zero heap allocations.
 */
public final class SSTableIndexLookup {

    private SSTableIndexLookup() {
        // Utility class
    }

    /**
     * Finds the file block offset for the greatest key that is less than or equal to targetKey.
     * Maps to a lower-bound binary search (LeetCode Medium equivalent).
     *
     * @param keys Sorted block start keys
     * @param offsets Parallel array containing block offsets
     * @param targetKey Search query key
     * @return Block offset, or -1 if targetKey is less than the first key
     */
    public static int findBlockOffset(long[] keys, int[] offsets, long targetKey) {
        if (keys == null || offsets == null || keys.length == 0 || offsets.length == 0 || keys.length != offsets.length) {
            return -1;
        }

        int low = 0;
        int high = keys.length - 1;
        int ans = -1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (keys[mid] <= targetKey) {
                ans = mid;
                low = mid + 1; // Scan right to see if a closer key <= targetKey exists
            } else {
                high = mid - 1;
            }
        }
        return ans == -1 ? -1 : offsets[ans];
    }

    /**
     * Resolves key index position when keys are rotated in circular index buffers (LeetCode Medium).
     *
     * @param keys Sorted but rotated key array
     * @param targetKey Target key to search
     * @return Index position of targetKey, or -1 if not found
     */
    public static int searchRotatedIndex(long[] keys, long targetKey) {
        if (keys == null || keys.length == 0) {
            return -1;
        }

        int low = 0;
        int high = keys.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (keys[mid] == targetKey) {
                return mid;
            }

            // Check if left partition is sorted
            if (keys[low] <= keys[mid]) {
                if (targetKey >= keys[low] && targetKey < keys[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else { // Right partition is sorted
                if (targetKey > keys[mid] && targetKey <= keys[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }

    /**
     * Finds the median key across two separate sorted partitions without merging them (LeetCode Hard).
     *
     * @param keysA Sorted key partition A
     * @param keysB Sorted key partition B
     * @return Double precision median value
     */
    public static double findMedianKey(long[] keysA, long[] keysB) {
        if (keysA == null || keysB == null) {
            throw new IllegalArgumentException("Inputs cannot be null.");
        }

        // Ensure keysA is the smaller array to minimize the search space
        if (keysA.length > keysB.length) {
            return findMedianKey(keysB, keysA);
        }

        int m = keysA.length;
        int n = keysB.length;
        int low = 0;
        int high = m;

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
                high = i - 1; // Partition partition A leftwards
            } else {
                low = i + 1;  // Partition partition A rightwards
            }
        }
        throw new IllegalArgumentException("Input partitions are not sorted.");
    }
}
