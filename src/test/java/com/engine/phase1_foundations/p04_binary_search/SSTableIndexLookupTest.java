package com.engine.phase1_foundations.p04_binary_search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import com.engine.TestDataGenerator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SSTableIndexLookupTest {

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    // 1. Exact Key Search
    static Stream<Object[]> exactSearchTestCases() {
        return Stream.of(
            new Object[]{ new long[]{10, 20, 30, 40}, 30L, 2 },
            new Object[]{ new long[]{10, 20, 30, 40}, 25L, -1 },
            new Object[]{ new long[]{10, 20, 30, 40}, 5L, -1 },
            new Object[]{ new long[]{10, 20, 30, 40}, 50L, -1 },
            new Object[]{ new long[]{10}, 10L, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("exactSearchTestCases")
    void testExactSearch(long[] keys, long target, int expectedIndex) {
        assertEquals(expectedIndex, SSTableIndexLookup.searchExactKey(keys, target));
    }

    // 2. Insertion Position Finder
    static Stream<Object[]> insertPositionTestCases() {
        return Stream.of(
            new Object[]{ new long[]{10, 20, 30, 40}, 30L, 2 },
            new Object[]{ new long[]{10, 20, 30, 40}, 25L, 2 },
            new Object[]{ new long[]{10, 20, 30, 40}, 5L, 0 },
            new Object[]{ new long[]{10, 20, 30, 40}, 50L, 4 },
            new Object[]{ new long[]{10}, 10L, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("insertPositionTestCases")
    void testInsertPosition(long[] keys, long target, int expectedIndex) {
        assertEquals(expectedIndex, SSTableIndexLookup.findInsertPosition(keys, target));
    }

    // 3. First and Last Occurrence Range Search
    static Stream<Object[]> firstAndLastOccurrenceTestCases() {
        return Stream.of(
            new Object[]{ new long[]{5, 7, 7, 8, 8, 10}, 8L, new int[]{3, 4} },
            new Object[]{ new long[]{5, 7, 7, 8, 8, 10}, 6L, new int[]{-1, -1} },
            new Object[]{ new long[]{}, 0L, new int[]{-1, -1} }
        );
    }

    @ParameterizedTest
    @MethodSource("firstAndLastOccurrenceTestCases")
    void testFindFirstAndLastOccurrence(long[] keys, long target, int[] expected) {
        assertArrayEquals(expected, SSTableIndexLookup.findFirstAndLastOccurrence(keys, target));
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    // 4. Lower Bound Range Query
    static Stream<Object[]> lowerBoundTestCases() {
        return Stream.of(
            new Object[]{ new long[]{100, 200, 300, 400}, new int[]{10, 20, 30, 40}, 250L, 20 },
            new Object[]{ new long[]{100, 200, 300, 400}, new int[]{10, 20, 30, 40}, 100L, 10 },
            new Object[]{ new long[]{100, 200, 300, 400}, new int[]{10, 20, 30, 40}, 450L, 40 },
            new Object[]{ new long[]{100, 200, 300, 400}, new int[]{10, 20, 30, 40}, 50L, -1 }
        );
    }

    @ParameterizedTest
    @MethodSource("lowerBoundTestCases")
    void testLowerBoundLookup(long[] keys, int[] offsets, long targetKey, int expectedOffset) {
        assertEquals(expectedOffset, SSTableIndexLookup.findBlockOffset(keys, offsets, targetKey));
    }

    // 5. Rotated Index Buffer Search
    static Stream<Object[]> rotatedSearchTestCases() {
        return Stream.of(
            new Object[]{ new long[]{40, 50, 10, 20, 30}, 10L, 2 },
            new Object[]{ new long[]{40, 50, 10, 20, 30}, 30L, 4 },
            new Object[]{ new long[]{40, 50, 10, 20, 30}, 50L, 1 },
            new Object[]{ new long[]{40, 50, 10, 20, 30}, 99L, -1 }
        );
    }

    @ParameterizedTest
    @MethodSource("rotatedSearchTestCases")
    void testRotatedBufferSearch(long[] keys, long target, int expectedIndex) {
        assertEquals(expectedIndex, SSTableIndexLookup.searchRotatedIndex(keys, target));
    }

    // 6. Rotated Minimum Finder
    static Stream<Object[]> rotatedMinTestCases() {
        return Stream.of(
            new Object[]{ new long[]{30, 40, 50, 10, 20}, 10L },
            new Object[]{ new long[]{40, 50, 60, 70, 0, 10, 20}, 0L },
            new Object[]{ new long[]{10, 20, 30, 40}, 10L },
            new Object[]{ new long[]{50}, 50L }
        );
    }

    @ParameterizedTest
    @MethodSource("rotatedMinTestCases")
    void testRotatedMinFinder(long[] keys, long expectedMin) {
        assertEquals(expectedMin, SSTableIndexLookup.findMinInRotated(keys));
    }

    // 7. Peak Load Detector
    static Stream<Object[]> peakLoadTestCases() {
        return Stream.of(
            new Object[]{ new long[]{1, 3, 20, 4, 1, 0} },
            new Object[]{ new long[]{1, 2, 1, 3, 5, 6, 4} }
        );
    }

    @ParameterizedTest
    @MethodSource("peakLoadTestCases")
    void testPeakLoadFinder(long[] metrics) {
        int peakIndex = SSTableIndexLookup.findPeakLoad(metrics);
        if (metrics.length > 0 && peakIndex != -1) {
            boolean leftOk = (peakIndex == 0) || (metrics[peakIndex] > metrics[peakIndex - 1]);
            boolean rightOk = (peakIndex == metrics.length - 1) || (metrics[peakIndex] > metrics[peakIndex + 1]);
            assertTrue(leftOk && rightOk, "Index " + peakIndex + " is not a local peak");
        }
    }

    // 8. Capacity Planner
    static Stream<Object[]> capacityPlannerTestCases() {
        return Stream.of(
            new Object[]{ new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5, 15 },
            new Object[]{ new int[]{3, 2, 2, 4, 1, 4}, 3, 6 },
            new Object[]{ new int[]{1, 2, 3, 1, 1}, 4, 3 }
        );
    }

    @ParameterizedTest
    @MethodSource("capacityPlannerTestCases")
    void testCapacityPlanner(int[] taskLoads, int numInstances, int expectedMinCapacity) {
        assertEquals(expectedMinCapacity, SSTableIndexLookup.calculateMinimumCapacity(taskLoads, numInstances));
    }

    // 9. 2D SSTable Matrix Search
    static Stream<Object[]> matrixSearchTestCases() {
        return Stream.of(
            new Object[]{ new long[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 3L, true },
            new Object[]{ new long[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 13L, false }
        );
    }

    @ParameterizedTest
    @MethodSource("matrixSearchTestCases")
    void testSearchIn2DMatrix(long[][] matrix, long target, boolean expected) {
        assertEquals(expected, SSTableIndexLookup.searchIn2DMatrix(matrix, target));
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    // 10. Cross-Partition Median Key Find
    static Stream<Object[]> medianKeyTestCases() {
        return Stream.of(
            new Object[]{ new long[]{1, 3}, new long[]{2}, 2.0 },
            new Object[]{ new long[]{1, 2}, new long[]{3, 4}, 2.5 },
            new Object[]{ new long[]{0, 0}, new long[]{0, 0}, 0.0 }
        );
    }

    @ParameterizedTest
    @MethodSource("medianKeyTestCases")
    void testMedianKeyFinder(long[] keysA, long[] keysB, double expectedMedian) {
        assertEquals(expectedMedian, SSTableIndexLookup.findMedianKey(keysA, keysB), 0.0001);
    }

    // 11. Maximum Metric Load Allocator
    static Stream<Object[]> maxShardLoadTestCases() {
        return Stream.of(
            new Object[]{ new int[]{7, 2, 5, 10, 8}, 2, 18 },
            new Object[]{ new int[]{1, 2, 3, 4, 5}, 2, 9 },
            new Object[]{ new int[]{1, 4, 4}, 3, 4 }
        );
    }

    @ParameterizedTest
    @MethodSource("maxShardLoadTestCases")
    void testMaxShardLoadAllocator(int[] partitionSizes, int numShards, int expectedMaxSum) {
        assertEquals(expectedMaxSum, SSTableIndexLookup.minimizeMaxShardLoad(partitionSizes, numShards));
    }

    // --- Stress Test ---
    @Test
    void stressTestMedianKeyFinder() {
        int size = 150_000;
        int[] rawA = TestDataGenerator.generateSortedArray(size, 0, 10);
        int[] rawB = TestDataGenerator.generateSortedArray(size, 5, 10);
        
        long[] keysA = new long[size];
        long[] keysB = new long[size];
        for (int i = 0; i < size; i++) {
            keysA[i] = rawA[i];
            keysB[i] = rawB[i];
        }

        long start = System.nanoTime();
        double median = SSTableIndexLookup.findMedianKey(keysA, keysB);
        long elapsed = System.nanoTime() - start;

        double elapsedMs = elapsed / 1_000_000.0;
        assertTrue(elapsedMs < 2.0, "Stress search exceeded 2ms. Took: " + elapsedMs + "ms");
    }
}
