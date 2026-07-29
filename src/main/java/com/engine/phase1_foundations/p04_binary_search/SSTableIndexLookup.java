package com.engine.phase1_foundations.p04_binary_search;

/**
 * High-performance SSTable Index Lookup & Capacity Management component.
 * Optimized for zero-autoboxing and zero heap allocations.
 * 
 * Study task: Implement the 11 binary search algorithms below.
 */
public final class SSTableIndexLookup {

    private SSTableIndexLookup() {
        // Utility class
    }

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    /**
     * Problem 1: Exact Key Search (Key Index Lookup)
     * Finds the 0-based index of target in sorted keys, or -1 if not present.
     */
    public static int searchExactKey(long[] keys, long target) {
        // saftey check
        if (keys == null || keys.length == 0) {
            return -1;
        }

        int low = 0;
        int high = keys.length - 1;
        while (low <= high) {
            // bit shift to avoid integer overflow
            int mid = (low + high) >>> 1;
            long midVal = keys[mid];
            if (midVal == target) {
                return mid;
            } else if (midVal < target) {
                // if the value is too small
                // assess the partition to the right of mid only
                low = mid + 1;
            } else {
                // if too big, asess to the left
                high = mid - 1;
            }

        }
        return -1;
    }

    /**
     * Problem 2: Insertion Position Finder (Start Block Locator)
     * Finds insertion index of target in sorted keys to maintain ascending order.
     */
    public static int findInsertPosition(long[] keys, long target) {
        // saftey guard
        if (keys == null || keys.length == 0) {
            return -1;
        }
        int low = 0;
        int high = keys.length - 1;
        // evaulate
        // note: we have the benefit that when searching
        // low will always be the closest match if there is no exact
        while (low <= high) {
            int mid = (low + high) >>> 1;
            long midval = keys[mid];
            if (midval == target) {
                return mid;
            } else if (midval < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return low;
    }

    /**
     * Problem 3: First and Last Occurrence Range Search
     * Finds starting and ending 0-based index of duplicate target in sorted keys.
     * Returns int[] {firstIndex, lastIndex}, or int[] {-1, -1} if not present.
     */
    public static int[] findFirstAndLastOccurrence(long[] keys, long target) {
        // do two passes, on the first use low to find the first appearance
        // on the second use high to find the last appearance
        int[] res = new int[] { -1, -1 };
        // saftey guard
        if (keys == null || keys.length == 0) {
            return res;
        }

        int low = 0;
        int high = keys.length - 1;
        int mid;
        long midVal;
        // low pass
        while (low <= high) {
            mid = (low + high) >>> 1;
            midVal = keys[mid];
            // if either on target or too small,
            // try a smaller partition
            if (midVal == target) {
                res[0] = mid;
                high = mid - 1;
            } else if (midVal < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        // high pass
        low = 0;
        high = keys.length - 1;
        while (low <= high) {
            mid = (low + high) >>> 1;
            midVal = keys[mid];
            // always try a larger partition
            if (midVal == target) {
                res[1] = mid;
                low = mid + 1;
            } else if (midVal < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return res;
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    /**
     * Problem 4: Range Query Lookup (SSTable Lower Bound)
     * Finds largest key <= targetKey to retrieve corresponding offset.
     */
    public static int findBlockOffset(long[] keys, int[] offsets, long targetKey) {
        // saftey guard
        if (keys == null || offsets == null || keys.length == 0 || keys.length != offsets.length) {
            return -1;
        } else if (targetKey < keys[0]) {
            // there's no point in trying process in this case.
            return -1;
        }

        int low = 0;
        int high = keys.length - 1;
        int mid;
        long midVal;

        while (low <= high) {
            mid = (low + high) >>> 1;
            midVal = keys[mid];

            if (midVal == targetKey) {
                return offsets[mid];
            } else if (midVal < targetKey) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        // in the event there was not an exact match, low will be the best option
        // because we did a saftey check tom ake sure at least one key is >= target
        // this should work ok
        return offsets[low - 1];
    }

    /**
     * Problem 5: Rotated Index Buffer Search (Hash Ring Locator)
     * Searches for targetKey in a sorted array that has been rotated by an unknown
     * offset.
     */

    public static int searchRotatedIndex(long[] keys, long targetKey) {
        // key: one of our partitions is always sorted
        // so we can check if our target is in the sorted partition's range
        // if not, by defintion, it must be in the other parition if it exists

        // saftey guards
        if (keys == null || keys.length == 0) {
            return -1;
        }

        int low = 0;
        int high = keys.length - 1;
        int mid;
        long midVal;

        while (low <= high) {
            mid = (low + high) >>> 1;
            midVal = keys[mid];

            // if we found the target, nothing to do, return
            if (midVal == targetKey) {
                return mid;
            }
            // otherwise, which partition is sorted
            // we can use the right partition check as a universal check
            // for which partition is sorted AND which partition contains
            // the minimum
            boolean isRightPartitionSorted = keys[mid] < keys[high];

            if (isRightPartitionSorted) {
                if (midVal < targetKey && targetKey <= keys[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            } else {
                if (keys[low] <= targetKey && targetKey < midVal) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }

            }
        }

        return -1;
    }

    /**
     * Problem 6: Rotated Index Boundary Finder (Min Element in Ring)
     * Finds the minimum element in a sorted rotated array.
     */
    public static long findMinInRotated(long[] keys) {
        // rely on two keys:
        // if the right partition is sorted, then the right partition
        // is strictly growing vs mid, so we can drop it
        // AND we rely on the convergence of low and high within the
        // partition that contains the min
        // such that when low == high, it will mean we've found the answer

        if (keys == null || keys.length == 0) {
            return -1;
        }

        int low = 0;
        int high = keys.length - 1;
        int mid;

        // when equal, we've found the answer
        // so exit the loop
        while (low < high) {
            mid = (low + high) >>> 1;

            // in the case that the right partition is sorted
            // we can simply ignore the right
            if (keys[mid] < keys[high]) {
                // however, mid itself could be an answer
                // so we cannot drop it
                high = mid;
            } else {
                // in the event that right is not sorted
                // we know for a fact mid is already greater
                // that something in the right
                // so we can drop it and explore right
                low = mid + 1;
            }
        }

        return keys[low];
    }

    /**
     * Problem 7: Peak Load Anomaly Detector (Find Peak Element)
     * Finds a local peak element index in an unsorted metrics array.
     */
    public static int findPeakLoad(long[] metrics) {
        // key idea: there are multiple answers that are valid
        // and for any given local peak we either end up on its descending
        // or ascending slope,
        // therefore we can always cut the search space in half each time
        // until we arrive at the peak

        // saftey guard
        if (metrics == null || metrics.length == 0) {
            return -1;
        }

        int low = 0;
        int high = metrics.length - 1;
        int mid;
        long midVal;
        while (low < high) {
            mid = (low + high) >>> 1;
            midVal = metrics[mid];
            // mid is guaranteed to be less than high because bit shift rounds down
            // therefore since high <= metrics.length - 1, we dont have to worry
            // about mid + 1 >= metrics.length
            if (midVal > metrics[mid + 1]) {
                // in this case we are on the descending slope, so we want to
                // back up the slope
                // not we cannot preclude mid as the peak!
                high = mid;
            } else {
                // otherwise we are on the ascending slope, so we ant to advance
                // towards the peak
                // in this case, we know mid is not the peak, so we can go beyond it
                low = mid + 1;
            }

        }
        return low;
    }

    /**
     * Problem 8: Capacity Planner (Search Space Binary Search)
     * Finds the minimum per-instance capacity required to process an ordered
     * sequence
     * of workloads using at most {@code numInstances} parallel worker instances.
     * 
     * Tasks must be assigned in contiguous order to parallel workers without
     * splitting single tasks.
     * Binary searches the answer space [max(taskLoads), sum(taskLoads)] to find the
     * smallest capacity
     * that allows all tasks to finish across <= numInstances workers.
     * 
     * @param taskLoads    array where each element represents the workload
     *                     cost/size of a task
     * @param numInstances maximum number of parallel worker instances available
     * @return minimum instance capacity needed
     */
    public static int calculateMinimumCapacity(int[] taskLoads, int numInstances) {
        // saftey guard
        if (taskLoads == null || taskLoads.length == 0 || numInstances <= 0) {
            return -1;
        }

        // compute the answer space which is
        // [max(taskLoads) ... sum(taskLoads)] because at minimum an instance
        // must be able to handle the largest task by itself
        // and at maximum we could just pick a huge instance such that all task loads
        // fit into it
        int tasksSum = 0;
        int maxTask = 0;
        for (int task : taskLoads) {
            tasksSum += task;
            maxTask = maxTask > task ? maxTask : task;
        }
        // then conduct binary search
        int lowCapacity = maxTask;
        int highCapacity = tasksSum;
        int instanceCapacity;
        int instancesUsed;
        int currentInstanceCapacityLeft;

        while (lowCapacity < highCapacity) {
            instanceCapacity = (lowCapacity + highCapacity) >>> 1;
            // see how many instances we are going to use at this capacity
            instancesUsed = 1;
            currentInstanceCapacityLeft = instanceCapacity;

            for (int task : taskLoads) {
                // either the current instance can take the whole task
                // or we need a new instance
                if (currentInstanceCapacityLeft >= task) {
                    currentInstanceCapacityLeft -= task;
                } else {
                    instancesUsed += 1;
                    currentInstanceCapacityLeft = instanceCapacity - task;
                }
            }
            // check feasiability
            if (instancesUsed == numInstances) {
                // if right at max num instances, see if can go lower
                highCapacity = instanceCapacity;
            } else if (instancesUsed < numInstances) {
                // or if under the max instances try to see if can get size of
                // instance down
                highCapacity = instanceCapacity;
            } else {
                // if using too many tasks have to try to make instances larger;
                // in this case, we can exclude instance capacity as a possible answer
                // because it is NOT feasible whereas it is feasible for the above two
                // conditions
                lowCapacity = instanceCapacity + 1;
            }

        }
        // on loop exit, low and high should converge to the mins
        return lowCapacity;
    }

    /**
     * Problem 9: 2D SSTable Matrix Search (Partition Grid Search)
     * Searches for target in a 2D matrix where rows are sorted and first element of
     * each row > last of previous.
     */
    public static boolean searchIn2DMatrix(long[][] matrix, long target) {
        // saftey guard
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return false;
        }

        // find the right row and then the right column
        boolean targetFound = false;
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        int low = 0;
        int high = numRows - 1;
        int mid;
        int targetRow = -1;

        while (low <= high) {
            mid = (low + high) >>> 1;
            // if the row bounds target, we've found the row we need
            // no need to keep searching
            if (matrix[mid][0] <= target && matrix[mid][numCols - 1] >= target) {
                targetRow = mid;
                break;
            } else if (matrix[mid][0] > target) {
                // if on the other hand the first element is already larger than target
                // we need to look at an earlier row
                high = mid - 1;
            } else {
                // or in the last case, if the last element of the row is smaller than
                // the target, we need a later row
                low = mid + 1;
            }
        }
        // if you couldn't find a suitable row, then return false
        if (targetRow == -1) {
            return targetFound;
        }
        // otherwise, try and find the target
        long[] candidateRow = matrix[targetRow];
        low = 0;
        high = numCols - 1;
        while (low <= high) {
            mid = (low + high) >>> 1;

            if (candidateRow[mid] == target) {
                targetFound = true;
                break;
            } else if (candidateRow[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return targetFound;
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    /**
     * Problem 10: Cross-Partition Median Key Find
     * Finds the median of two sorted arrays of different sizes in O(log(min(M, N)))
     * time.
     */
    public static double findMedianKey(long[] keysA, long[] keysB) {
        // key idea: we dont have to merge the arrays, we can just pretend they are
        // merged
        // and then check when we've actually found a partition that covers the smallest
        // 50% of the overall
        // elements

        // saftey guard, always have the smaller array be the first one, so B cannot
        // be negative or out of bounds
        if (keysA.length > keysB.length) {
            return findMedianKey(keysB, keysA);
        }

        int M = keysA.length;
        int N = keysB.length;

        int low = 0;
        // high is M because we are looking at number of elements
        // rather than specific indexes
        int high = M;

        long maxALeft = Long.MIN_VALUE;
        long maxBLeft = Long.MIN_VALUE;
        long minARight = Long.MAX_VALUE;
        long minBRight = Long.MAX_VALUE;

        int elementCountA;
        int elementCountB;

        // since (M + N + 1) / 2 would be the middle of the overall array
        // we can simulate guess where to cut B based on where we cut A

        while (low <= high) {
            elementCountA = (low + high) >>> 1;
            // the + 1 is required to make sure that when we compute median it is accurate
            elementCountB = (M + N + 1) / 2 - elementCountA;

            // subtract 1 because of 0 based indexing
            maxALeft = (elementCountA == 0) ? Long.MIN_VALUE : keysA[elementCountA - 1];
            minARight = (elementCountA == M) ? Long.MAX_VALUE : keysA[elementCountA];

            maxBLeft = (elementCountB == 0) ? Long.MIN_VALUE : keysB[elementCountB - 1];
            minBRight = (elementCountB == N) ? Long.MAX_VALUE : keysB[elementCountB];

            // now assess if we found the median or not
            if (maxALeft <= minBRight && maxBLeft <= minARight) {
                if ((M + N) % 2 != 0) {
                    return Math.max(maxALeft, maxBLeft);
                } else {
                    return (Math.max(maxALeft, maxBLeft) + Math.min(minARight, minBRight)) / 2.0;
                }
            } else if (maxALeft > minBRight) {
                // in this case, we have too many elements in A
                high = elementCountA - 1;
            } else {
                // int his case, we have too many elements in B
                low = elementCountA + 1;
            }

        }

        return 0.0;
    }

    /**
     * Problem 11: Maximum Metric Load Allocator
     * Partitions array into numShards such that maximum subarray sum is minimized.
     */
    public static int minimizeMaxShardLoad(int[] partitionSizes, int numShards) {
        // this problem reduces to the prior.
        return calculateMinimumCapacity(partitionSizes, numShards);

    }
}
