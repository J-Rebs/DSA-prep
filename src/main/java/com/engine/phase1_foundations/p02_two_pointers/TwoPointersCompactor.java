package com.engine.phase1_foundations.p02_two_pointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * High-performance Two-Pointer stream compaction & merging component.
 * Optimized to perform mutations in-place with minimal or zero heap
 * allocations.
 */
public final class TwoPointersCompactor {

    private TwoPointersCompactor() {
        // Utility class
    }

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    /**
     * Problem 1: Pair with Target Sum
     * Finds indices of a pair in a sorted array that adds up to target.
     */
    public static int[] pairWithTargetSum(int[] arr, int target) {
        // saftey guard and perf optimization for too small array
        if (arr == null || arr.length < 2) {
            return new int[] {};
        }
        // solution
        int left = 0;
        int right = arr.length - 1;
        while (left < right) {
            int sum = arr[left] + arr[right];
            if (sum == target) {
                return new int[] { left, right };
            } else if (sum > target) {
                right--;
            } else {
                left++;
            }
        }
        return new int[] {};
    }

    /**
     * Problem 2: Remove Duplicates (Compaction)
     * Removes duplicate elements in-place and returns the new unique length.
     */
    public static int removeDuplicates(int[] arr) {
        // saftey and performance guard for too small or null inputs
        if (arr == null || arr.length < 2) {
            return arr == null ? -1 : arr.length;
        }
        int write = 1;
        for (int read = 1; read < arr.length; read++) {
            // check is the read value unique or not, write exists
            // at the boundary of what is duplicative or not
            if (arr[read] != arr[write - 1]) {
                // if this is still unique, then we can overwrite what
                // is in the spot where write is duplicate or
                // if indexes are the same no op
                arr[write] = arr[read];
                write++;
            }
        }
        return write;
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    /**
     * Problem 3: Squaring a Sorted Array
     * Returns a sorted array containing squares of elements from the sorted input.
     */
    public static int[] squareSortedArray(int[] arr) {
        if (arr == null) {
            return new int[] {};
        }
        // we can use left and right pointers (the converging pattern) to simulate
        // as if we had taken the absolute values already. key: extreme values
        // comparison.
        int left = 0;
        int right = arr.length - 1;
        int[] solution = new int[arr.length];
        // we start with the greatest values so have to start at end of solution array
        int solIdx = arr.length - 1;

        while (left <= right) {
            int lVal = arr[left] < 0 ? -arr[left] : arr[left];
            int rVal = arr[right] < 0 ? -arr[right] : arr[right];
            if (lVal > rVal) {
                solution[solIdx--] = lVal * lVal;
                left++;
            } else {
                solution[solIdx--] = rVal * rVal;
                right--;
            }
        }
        return solution;
    }

    /**
     * Problem 4: Triplet Sum to Zero
     * Finds all unique triplets in the array that sum to zero.
     */
    public static List<List<Integer>> tripletSumToZero(int[] arr) {
        // null check
        if (arr == null || arr.length == 0) {
            return new ArrayList<>();
        }
        // sort input, O(nlogn)
        Arrays.sort(arr);
        List<List<Integer>> res = new ArrayList<>();
        // O(n^2) loop - we have to evaluate the whole array for a
        // given index. This beats O(n^3) but we cannot do better than this.
        for (int left = 0; left < arr.length; left++) {
            // no need to repeat a value if we already examined it's possible
            // solutions
            if (left > 0 && arr[left] == arr[left - 1]) {
                continue;
            }
            int mid = left + 1;
            int right = arr.length - 1;
            while (mid < right) {
                int sum = arr[left] + arr[mid] + arr[right];
                if (sum == 0) {
                    res.add(List.of(arr[left], arr[mid], arr[right]));

                    // dont want to repeat so exclude like values
                    mid++;
                    // is what we went to the same as what we saw before
                    while (mid < right && arr[mid] == arr[mid - 1]) {
                        mid++;
                    }
                    right--;
                    // is what we want to the same as what we saw before
                    while (mid < right && arr[right] == arr[right + 1]) {
                        right--;
                    }
                } else if (sum < 0) {
                    mid++;
                } else {
                    right--;
                }

            }
        }
        return res;
    }

    /**
     * Problem 5: Subarrays with Product Less than Target
     * Counts contiguous subarrays whose product is strictly less than target.
     */
    public static int subarraysWithProductLessThanTarget(int[] arr, int target) {
        // null guard
        if (arr == null || target < 1) {
            return -1;
        }

        // process all array of size 2 or greater
        int left = 0;
        int count = 0;
        int prod = 1;
        for (int right = 0; right < arr.length; right++) {

            prod *= arr[right];

            // update for constraint violation
            while (prod >= target && left <= right) {
                prod = prod / arr[left++];
            }

            // after doing our best to update, test
            if (prod < target) {
                // Key idea: # of contigous subarrays ending at index right
                // is right - left + 1
                count += right - left + 1;
            }

        }
        return count;
    }

    /**
     * Problem 6: Compacting SSTable logs in-place
     * Consolidates active and tombstoned logs in-place, returning new active
     * length.
     */
    public static int compactSSTableLogs(int[] logs, int tombstone) {
        return -1;
    }

    /**
     * Problem 7: Min Window Sort
     * Finds length of shortest subarray that, when sorted, makes the entire array
     * sorted.
     */
    public static int minWindowSort(int[] arr) {
        // saftey guard
        if (arr == null) {
            return -1;
        }
        // use symmterical scanning to find window
        int lBound = -1;
        for (int i = 0; i < arr.length - 1; i++) {
            // should be the thing to the right
            // is always greater than the thing to the left
            if (arr[i] > arr[i + 1]) {
                lBound = i;
                break;
            }
        }
        int rBound = -1;
        for (int i = arr.length - 1; i > 0; i--) {
            // should be the thing to the right is always greater
            // than the thing to the left
            if (arr[i] < arr[i - 1]) {
                rBound = i;
                break;
            }
        }
        // if lBound is unset, must be rBound is unset and therefore
        // already sorted
        if (lBound == -1) {
            return 0;
        }

        // pass 2 find min and max in window
        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        for (int i = lBound; i <= rBound; i++) {
            minVal = Math.min(arr[i], minVal);
            maxVal = Math.max(arr[i], maxVal);
        }
        // pass 3 find full sorting candidate
        while (lBound >= 1 && arr[lBound - 1] > minVal) {
            lBound--;
        }
        while (rBound < arr.length - 1 && arr[rBound + 1] < maxVal) {
            rBound++;
        }
        return rBound - lBound + 1;
    }

    /**
     * Problem 8: Quadruple Sum to Target
     * Finds all unique quadruplets that sum to a target value.
     */
    public static List<List<Integer>> quadrupleSumToTarget(int[] arr, int target) {
        return new ArrayList<>();
    }

    /**
     * Problem 9: Dutch National Flag Problem (Partitioning)
     * Sorts an array of 0s, 1s, and 2s in-place in linear time.
     */
    public static void dutchNationalFlag(int[] arr) {
        // In-place modification
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    /**
     * Problem 10: Trapping Rain Water
     * Computes how much water can be trapped in an elevation map.
     */
    public static int trappingRainWater(int[] heights) {
        return -1;
    }

    /**
     * Problem 11: Shortest Subarray with Sum at Least K
     * Finds shortest contiguous subarray with sum >= K (supports negative values).
     */
    public static int shortestSubarraySumAtLeastK(int[] arr, int k) {
        return -1;
    }
}
