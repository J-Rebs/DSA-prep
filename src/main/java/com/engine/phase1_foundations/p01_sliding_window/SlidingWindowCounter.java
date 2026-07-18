package com.engine.phase1_foundations.p01_sliding_window;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * High-performance Sliding Window Counter & Metric Buffer component.
 * Optimized for zero heap allocations where possible.
 */
public final class SlidingWindowCounter {

    private SlidingWindowCounter() {
        // Utility class
    }

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    /**
     * Problem 1: Max Sum Subarray of Size K
     * Finds the maximum sum of any contiguous subarray of size k.
     */
    public static int maxSumSubarray(int[] arr, int k) {
        // check we can produce something of size k or just retturn - 1
        if (arr == null || arr.length < k || k <= 0) {
            return -1;
        }
        int left = 0;
        int sum = 0;
        int res = -1;
        for (int right = 0; right < arr.length; right++) {
            sum += arr[right];
            // constraint violation
            if (right >= k) {
                sum -= arr[left];
                left++;
            }
            res = Math.max(res, sum);
        }
        return res;
    }

    /**
     * Problem 2: Smallest Subarray with a Greater Sum
     * Finds the length of the smallest contiguous subarray whose sum is >= target.
     */
    public static int smallestSubarrayWithGreaterSum(int[] arr, int target) {
        int sum = 0;
        int left = 0;
        int minLength = Integer.MAX_VALUE;
        for (int right = 0; right < arr.length; right++) {
            sum += arr[right];
            // take off as much of the left as we are permitted
            while (sum >= target) {
                minLength = Math.min(minLength, right - left + 1);
                sum -= arr[left];
                left++;
            }
        }
        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    /**
     * Problem 3: Rate Limiter Sliding Window Count
     * Given sorted event timestamps, return indices of events that violated the
     * rate limit
     * (more than maxRequests within windowSize).
     */
    public static int[] rateLimiterCount(long[] timestamps, long windowSize, int maxRequests) {
        if (timestamps == null || timestamps.length == 0 || windowSize <= 0 || maxRequests <= 0) {
            return new int[]{};
        }
        int[] violations = new int[timestamps.length];
        int violationIdx = 0;

        int left = 0;
        for (int right = 0; right < timestamps.length; right++) {
            // first, remove old events, then see if violate rate limit
            while (timestamps[right] - timestamps[left] >= windowSize) {
                left++;
            }
            // already would have checked previously violating right constraints,
            // so just check one
            if (right - left + 1 > maxRequests) {
                violations[violationIdx++] = right;
            }

        }

        int[] result = new int[violationIdx];
        for (int i = 0; i < violationIdx; i++) {
            result[i] = violations[i];
        }
        return result;
    }

    /**
     * Problem 4: Longest Substring with K Distinct Characters
     * Finds the length of the longest substring with at most k distinct characters.
     */
    public static int longestSubstringKDistinct(String str, int k) {
        // remember if you see something like longest x with distinct y, think frequency
        // maps
        // with sliding window
        int[] seen = new int[256];
        int uniqueCount = 0;
        int left = 0;
        int maxLen = 0;
        for (int right = 0; right < str.length(); right++) {
            // incorporate right character
            if (seen[str.charAt(right)] == 0) {
                uniqueCount++;
            }
            seen[str.charAt(right)]++;
            // then if we have too many unique characters shrink
            while (uniqueCount > k) {
                seen[str.charAt(left)]--;
                if (seen[str.charAt(left)] == 0) {
                    uniqueCount--;
                }
                left++;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    /**
     * Problem 5: Permutation in a String
     * Given a string and a pattern, find all starting indices of the pattern's
     * permutations in the string.
     */
    public static int[] findPermutations(String str, String pattern) {
        int[] seen = new int[256];
        int[] patternFreq = new int[256];
        List<Integer> res = new ArrayList<>();
        int uniqueChars = 0;
        // store character counts and the unique count
        for (char c : pattern.toCharArray()) {
            if (patternFreq[c] == 0) {
                uniqueChars++;
            }
            patternFreq[c]++;
        }
        int left = 0;
        int matched = 0;
        for (int right = 0; right < str.length(); right++) {
            // process right
            char rightChar = str.charAt(right);
            seen[rightChar]++;
            // if after processing right we have matched a full character, we should update
            if (patternFreq[rightChar] > 0 && seen[rightChar] == patternFreq[rightChar]) {
                matched++;
            }
            // check if window is too big
            if (right - left + 1 > pattern.length()) {
                char leftChar = str.charAt(left);
                if (patternFreq[leftChar] > 0 && seen[leftChar] == patternFreq[leftChar]) {
                    matched--;
                }
                seen[leftChar]--;
                left++;
            }
            // then see if we have matched a full permutation
            if (matched == uniqueChars) {
                res.add(left);
            }
        }

        return res.stream().mapToInt(Integer::intValue).toArray();

    }

    /**
     * Problem 6: Jitter Buffer Smoothing (Character Replacement)
     * Finds length of longest substring with same letters after replacing at most k
     * characters.
     */
    public static int jitterBufferSmoothing(String str, int k) {
        // track counts of all characters
        // find the most frequent character in a window
        // if a window size minus the count of the most frequent exceeds k, then
        // we have to shrink the window, otherwise we can see if this is a result
        int res = 0;
        int[] freq = new int[256];
        int left = 0;
        int maxFreq = 0;
        for (int right = 0; right < str.length(); right++) {
            // update frequency map for right
            char rChar = str.charAt(right);
            freq[rChar]++;
            // update maxFreq
            maxFreq = Math.max(maxFreq, freq[rChar]);
            // check if constraint violated
            // necessarily maxFreq cannot change here since freq at left losing value
            if (right - left + 1 - maxFreq > k) {
                freq[str.charAt(left)]--;
                left++;
            }
            // update result
            res = Math.max(res, right - left + 1);
        }
        return res;
    }

    /**
     * Problem 7: Telemetry Drop Tolerator (Max Consecutive Ones III)
     * Finds longest consecutive run of successful packets (1s) after replacing at
     * most k dropped packets (0s).
     */
    public static int telemetryDropTolerator(int[] packetLogs, int k) {
        // count 0s in window, if those exceed k shift window
        int left = 0;
        int dropped = 0;
        int res = 0;
        for (int right = 0; right < packetLogs.length; right++) {
            if (packetLogs[right] == 0)
                dropped++;
            // if dropped is now too large, we have to shift the window until dropped is ok
            // that means we would lose some of the earlier 1s too, but we must remove
            // a sufficient number of dropped
            while (dropped > k) {
                // key idea, we have use a while loop here on the constraint to get
                // to the solution, we cannot do something else
                if (packetLogs[left] == 0)
                    dropped--;
                left++;
            }
            res = Math.max(res, right - left + 1);
        }
        return res;
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    /**
     * Problem 8: Minimum Window Substring (Log Aggregator)
     * Finds the smallest substring of str containing all characters of pattern.
     */
    public static String minWindowSubstring(String str, String pattern) {
        // count frequencies, the second you meet pattern check result
        // then shrink until you break the pattern
        // key idea: if asking for min or max window, see if once satisfy result
        // can i shrink more? yes, then shrink, repeat.
        // if asking for max and don't satisfy, then grow
        int[] patternFreq = new int[256];
        int[] seen = new int[256];

        int uniqueCount = 0;
        for (char c : pattern.toCharArray()) {
            if (patternFreq[c] == 0)
                uniqueCount++;
            patternFreq[c]++;
        }

        int left = 0;
        int matched = 0;
        String res = "";

        for (int right = 0; right < str.length(); right++) {
            // process right
            char rChar = str.charAt(right);
            seen[rChar]++;
            if (patternFreq[rChar] > 0 && seen[rChar] == patternFreq[rChar]) {
                matched++;
            }
            // then see if we have a workable window
            while (matched == uniqueCount) {
                // first update result
                if (res.equals("") || right - left + 1 < res.length()) {
                    res = str.substring(left, right + 1);
                }
                // then shrink
                char lChar = str.charAt(left);
                if (patternFreq[lChar] > 0 && seen[lChar] == patternFreq[lChar]) {
                    matched--;
                }
                seen[lChar]--;
                left++;

            }
        }
        return res;
    }

    /**
     * Problem 9: Sliding Window Maximum (Metric buffer queues)
     * Finds the maximum value in each sliding window of size k.
     */
    public static int[] slidingWindowMaximum(int[] metrics, int k) {
        if (metrics == null || metrics.length < k || k <= 0) {
            return new int[]{};
        }
        // use a monotonic deque
        // basically have an array deque that tracks the best answers
        // available to us in the window
        ArrayDeque<Integer> deq = new ArrayDeque<>();
        ArrayList<Integer> res = new ArrayList<>();
        int left = 0;
        for (int right = 0; right < metrics.length; right++) {
            // process right -- remove the newest stuff smaller than
            // right from the deq since these cannot beat right for the max
            while (!deq.isEmpty() && metrics[deq.peekLast()] < metrics[right]) {
                deq.pollLast();
            }
            // then add right as a new possible maximum
            deq.offerLast(right);
            // then check if left has slid too far
            // remove the left if it is now too far
            if (right - left + 1 > k) {
                // only remove the left if it is the first
                // in the deque, otherwise we can just move on since
                // it was some non maximal value
                if (deq.peekFirst() == left) {
                    deq.pollFirst();
                }
                left++;
            }
            // then if have a valid result update
            if (right - left + 1 == k) {
                res.add(metrics[deq.peekFirst()]);
            }

        }
        return res.stream().mapToInt(Integer::intValue).toArray();
    }
}
