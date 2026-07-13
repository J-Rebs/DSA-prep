package com.engine.phase1_foundations.p04_binary_search;

import com.engine.TestDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SSTableIndexLookupTest {

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    // 1. Exact Search
    static Stream<Object[]> exactSearchTestCases() {
        return Stream.of(
            new Object[]{ new long[]{10, 20, 30, 40}, 30, 2 },
            new Object[]{ new long[]{10, 20, 30, 40}, 25, -1 },
            new Object[]{ new long[]{10, 20, 30, 40}, 5, -1 },
            new Object[]{ new long[]{10, 20, 30, 40}, 50, -1 },
            new Object[]{ new long[]{10}, 10, 0 }
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
            new Object[]{ new long[]{10, 20, 30, 40}, 30, 2 },
            new Object[]{ new long[]{10, 20, 30, 40}, 25, 2 },
            new Object[]{ new long[]{10, 20, 30, 40}, 5, 0 },
            new Object[]{ new long[]{10, 20, 30, 40}, 50, 4 },
            new Object[]{ new long[]{10}, 10, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("insertPositionTestCases")
    void testInsertPosition(long[] keys, long target, int expectedIndex) {
        assertEquals(expectedIndex, SSTableIndexLookup.findInsertPosition(keys, target));
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    // 3. Range Query Lookup (Lower Bound)
    static Stream<Object[]> lowerBoundTestCases() {
        return Stream.of(
            new Object[]{ new long[]{10, 20, 30, 40}, new int[]{100, 200, 300, 400}, 25, 200 },
            new Object[]{ new long[]{10, 20, 30, 40}, new int[]{100, 200, 300, 400}, 10, 100 },
            new Object[]{ new long[]{10, 20, 30, 40}, new int[]{100, 200, 300, 400}, 5, -1 },
            new Object[]{ new long[]{10, 20, 30, 40}, new int[]{100, 200, 300, 400}, 100, 400 },
            new Object[]{ new long[]{10}, new int[]{100}, 15, 100 }
        );
    }

    @ParameterizedTest
    @MethodSource("lowerBoundTestCases")
    void testLowerBoundLookup(long[] keys, int[] offsets, long target, int expectedOffset) {
        assertEquals(expectedOffset, SSTableIndexLookup.findBlockOffset(keys, offsets, target));
    }

    // 4. Rotated Index Buffer Search
    static Stream<Object[]> rotatedIndexTestCases() {
        return Stream.of(
            new Object[]{ new long[]{4, 5, 6, 7, 0, 1, 2}, 0, 4 },
            new Object[]{ new long[]{4, 5, 6, 7, 0, 1, 2}, 3, -1 },
            new Object[]{ new long[]{0, 1, 2, 4, 5, 6, 7}, 5, 4 },
            new Object[]{ new long[]{2, 0, 1}, 0, 1 }
        );
    }

    @ParameterizedTest
    @MethodSource("rotatedIndexTestCases")
    void testRotatedBufferSearch(long[] keys, long target, int expectedIndex) {
        assertEquals(expectedIndex, SSTableIndexLookup.searchRotatedIndex(keys, target));
    }

    // 5. Rotated Index Boundary Finder
    static Stream<Object[]> rotatedMinTestCases() {
        return Stream.of(
            new Object[]{ new long[]{4, 5, 6, 7, 0, 1, 2}, 0L },
            new Object[]{ new long[]{0, 1, 2, 4, 5, 6, 7}, 0L },
            new Object[]{ new long[]{2, 0, 1}, 0L },
            new Object[]{ new long[]{5}, 5L }
        );
    }

    @ParameterizedTest
    @MethodSource("rotatedMinTestCases")
    void testRotatedMinFinder(long[] keys, long expectedMin) {
        assertEquals(expectedMin, SSTableIndexLookup.findMinInRotated(keys));
    }

    // 6. Peak Load Anomaly Detector
    static Stream<Object[]> peakLoadTestCases() {
        return Stream.of(
            new Object[]{ new long[]{1, 2, 3, 1} },
            new Object[]{ new long[]{1, 2, 1, 3, 5, 6, 4} },
            new Object[]{ new long[]{10, 20, 30, 40, 50} },
            new Object[]{ new long[]{50, 40, 30, 20, 10} }
        );
    }

    @ParameterizedTest
    @MethodSource("peakLoadTestCases")
    void testPeakLoadFinder(long[] metrics) {
        int peakIdx = SSTableIndexLookup.findPeakLoad(metrics);
        assertTrue(peakIdx >= 0 && peakIdx < metrics.length);
        
        // Assert boundary conditions to verify index represents a local maximum
        boolean leftOk = (peakIdx == 0) || (metrics[peakIdx] > metrics[peakIdx - 1]);
        boolean rightOk = (peakIdx == metrics.length - 1) || (metrics[peakIdx] > metrics[peakIdx + 1]);
        assertTrue(leftOk && rightOk, "Index " + peakIdx + " with value " + metrics[peakIdx] + " is not a local peak.");
    }

    // 7. Capacity Planner
    static Stream<Object[]> capacityPlannerTestCases() {
        return Stream.of(
            // Format: { taskLoads, numInstances, expectedMinCapacity }
            new Object[]{ new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5, 15 },
            new Object[]{ new int[]{3, 2, 2, 4, 1, 4}, 3, 6 },
            new Object[]{ new int[]{1, 2, 1}, 3, 2 }
        );
    }

    @ParameterizedTest
    @MethodSource("capacityPlannerTestCases")
    void testCapacityPlanner(int[] taskLoads, int numInstances, int expectedCapacity) {
        assertEquals(expectedCapacity, SSTableIndexLookup.calculateMinimumCapacity(taskLoads, numInstances));
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    // 8. Cross-Partition Median Key Find
    static Stream<Object[]> medianTestCases() {
        return Stream.of(
            new Object[]{ new long[]{1, 3}, new long[]{2}, 2.0 },
            new Object[]{ new long[]{1, 2}, new long[]{3, 4}, 2.5 },
            new Object[]{ new long[]{10, 20, 30}, new long[]{5, 15, 25, 35}, 20.0 },
            new Object[]{ new long[]{}, new long[]{1, 2, 3, 4}, 2.5 }
        );
    }

    @ParameterizedTest
    @MethodSource("medianTestCases")
    void testMedianKeyFinder(long[] keysA, long[] keysB, double expectedMedian) {
        assertEquals(expectedMedian, SSTableIndexLookup.findMedianKey(keysA, keysB), 0.001);
    }

    // 9. Maximum Metric Load Allocator
    static Stream<Object[]> maxShardLoadTestCases() {
        return Stream.of(
            // Format: { partitionSizes, numShards, expectedMaxMinimizedSum }
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
        assertTrue(median >= keysA[0] || median >= keysB[0]);
    }
}
