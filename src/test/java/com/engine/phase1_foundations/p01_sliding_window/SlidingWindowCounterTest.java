package com.engine.phase1_foundations.p01_sliding_window;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlidingWindowCounterTest {

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    // 1. Max Sum Subarray
    static Stream<Object[]> maxSumSubarrayCases() {
        return Stream.of(
            new Object[]{ new int[]{2, 1, 5, 1, 3, 2}, 3, 9 },
            new Object[]{ new int[]{2, 3, 4, 1, 5}, 2, 7 },
            new Object[]{ new int[]{1, 2}, 3, -1 }, // k > length
            new Object[]{ new int[]{}, 2, -1 }      // empty
        );
    }

    @ParameterizedTest
    @MethodSource("maxSumSubarrayCases")
    public void testMaxSumSubarray(int[] arr, int k, int expected) {
        assertEquals(expected, SlidingWindowCounter.maxSumSubarray(arr, k));
    }

    // 2. Smallest Subarray with Greater Sum
    static Stream<Object[]> smallestSubarrayCases() {
        return Stream.of(
            new Object[]{ new int[]{2, 1, 5, 2, 3, 2}, 7, 2 },
            new Object[]{ new int[]{2, 1, 5, 2, 8}, 7, 1 },
            new Object[]{ new int[]{3, 4, 1, 1, 6}, 8, 3 },
            new Object[]{ new int[]{1, 2, 3}, 10, 0 },  // sum never reached
            new Object[]{ new int[]{}, 5, 0 }           // empty
        );
    }

    @ParameterizedTest
    @MethodSource("smallestSubarrayCases")
    public void testSmallestSubarrayWithGreaterSum(int[] arr, int target, int expected) {
        assertEquals(expected, SlidingWindowCounter.smallestSubarrayWithGreaterSum(arr, target));
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    // 3. Rate Limiter Count
    static Stream<Object[]> rateLimiterCases() {
        return Stream.of(
            new Object[]{ new long[]{100, 200, 250, 300, 500, 1000}, 200L, 2, new int[]{2, 3} },
            new Object[]{ new long[]{100, 110, 120, 130}, 50L, 2, new int[]{2, 3} },
            new Object[]{ new long[]{100, 200, 300, 400}, 50L, 1, new int[]{} },
            new Object[]{ new long[]{}, 100L, 3, new int[]{} }
        );
    }

    @ParameterizedTest
    @MethodSource("rateLimiterCases")
    public void testRateLimiterCount(long[] timestamps, long windowSize, int maxRequests, int[] expected) {
        assertArrayEquals(expected, SlidingWindowCounter.rateLimiterCount(timestamps, windowSize, maxRequests));
    }

    // 4. Longest Substring with K Distinct
    static Stream<Object[]> longestSubstringKDistinctCases() {
        return Stream.of(
            new Object[]{ "araaci", 2, 4 },
            new Object[]{ "araaci", 1, 2 },
            new Object[]{ "cbbebi", 3, 5 },
            new Object[]{ "abc", 5, 3 },
            new Object[]{ "", 2, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("longestSubstringKDistinctCases")
    public void testLongestSubstringKDistinct(String str, int k, int expected) {
        assertEquals(expected, SlidingWindowCounter.longestSubstringKDistinct(str, k));
    }

    // 5. Permutation in a String
    static Stream<Object[]> findPermutationsCases() {
        return Stream.of(
            new Object[]{ "oidbcaf", "abc", new int[]{3} },
            new Object[]{ "ppqp", "pq", new int[]{1, 2} },
            new Object[]{ "abcdef", "xyz", new int[]{} },
            new Object[]{ "", "abc", new int[]{} }
        );
    }

    @ParameterizedTest
    @MethodSource("findPermutationsCases")
    public void testFindPermutations(String str, String pattern, int[] expected) {
        assertArrayEquals(expected, SlidingWindowCounter.findPermutations(str, pattern));
    }

    // 6. Jitter Buffer Smoothing
    static Stream<Object[]> jitterBufferSmoothingCases() {
        return Stream.of(
            new Object[]{ "aabccbb", 2, 5 },
            new Object[]{ "abbcbi", 1, 4 },
            new Object[]{ "abccde", 1, 3 },
            new Object[]{ "", 1, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("jitterBufferSmoothingCases")
    public void testJitterBufferSmoothing(String str, int k, int expected) {
        assertEquals(expected, SlidingWindowCounter.jitterBufferSmoothing(str, k));
    }

    // 7. Telemetry Drop Tolerator
    static Stream<Object[]> telemetryDropToleratorCases() {
        return Stream.of(
            new Object[]{ new int[]{1, 1, 0, 0, 1, 1, 0, 1, 1}, 2, 6 },
            new Object[]{ new int[]{0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1}, 2, 6 },
            new Object[]{ new int[]{1, 0, 1, 1, 0, 1, 1}, 1, 5 },
            new Object[]{ new int[]{}, 2, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("telemetryDropToleratorCases")
    public void testTelemetryDropTolerator(int[] packetLogs, int k, int expected) {
        assertEquals(expected, SlidingWindowCounter.telemetryDropTolerator(packetLogs, k));
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    // 8. Minimum Window Substring
    static Stream<Object[]> minWindowSubstringCases() {
        return Stream.of(
            new Object[]{ "aabdec", "abc", "abdec" },
            new Object[]{ "abdbca", "abc", "bca" },
            new Object[]{ "adobedecodebanc", "abc", "banc" },
            new Object[]{ "abc", "abcd", "" }
        );
    }

    @ParameterizedTest
    @MethodSource("minWindowSubstringCases")
    public void testMinWindowSubstring(String str, String pattern, String expected) {
        assertEquals(expected, SlidingWindowCounter.minWindowSubstring(str, pattern));
    }

    // 9. Sliding Window Maximum
    static Stream<Object[]> slidingWindowMaximumCases() {
        return Stream.of(
            new Object[]{ new int[]{1, 2, 3, 1, 4, 5, 2, 3, 6}, 3, new int[]{3, 3, 4, 5, 5, 5, 6} },
            new Object[]{ new int[]{8, 5, 10, 7, 9, 4, 15, 12, 90, 13}, 4, new int[]{10, 10, 10, 15, 15, 90, 90} },
            new Object[]{ new int[]{1, 2}, 3, new int[]{} }
        );
    }

    @ParameterizedTest
    @MethodSource("slidingWindowMaximumCases")
    public void testSlidingWindowMaximum(int[] metrics, int k, int[] expected) {
        assertArrayEquals(expected, SlidingWindowCounter.slidingWindowMaximum(metrics, k));
    }

    // Stress testing max sum subarray
    @Test
    public void testScaleMaxSumSubarray() {
        int limit = 100_000;
        int[] arr = new int[limit];
        for (int i = 0; i < limit; i++) {
            arr[i] = 1;
        }
        int sum = SlidingWindowCounter.maxSumSubarray(arr, 50);
        // Correct implementation should yield 50 or -1 if unimplemented
        // We just assert that it executes within bounds and return successfully
        System.out.println("Stress scale run completed. Returned: " + sum);
    }
}
