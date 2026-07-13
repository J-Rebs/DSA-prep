package com.engine.phase1_foundations.binary_search;

import com.engine.TestDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SSTableIndexLookupTest {

    // --- 1. Parametric test cases for lower-bound block lookups ---
    static Stream<Object[]> lowerBoundTestCases() {
        return Stream.of(
            // Format: { keys, offsets, targetKey, expectedOffset }
            new Object[]{ new long[]{10, 20, 30, 40}, new int[]{100, 200, 300, 400}, 25, 200 }, // Standard range match
            new Object[]{ new long[]{10, 20, 30, 40}, new int[]{100, 200, 300, 400}, 10, 100 }, // Exact boundary match
            new Object[]{ new long[]{10, 20, 30, 40}, new int[]{100, 200, 300, 400}, 5, -1 },   // Underflow
            new Object[]{ new long[]{10, 20, 30, 40}, new int[]{100, 200, 300, 400}, 100, 400 }, // Overflow (caps to last block)
            new Object[]{ new long[]{10}, new int[]{100}, 10, 100 },                             // Single-element exact
            new Object[]{ new long[]{10}, new int[]{100}, 15, 100 }                              // Single-element greater
        );
    }

    @ParameterizedTest
    @MethodSource("lowerBoundTestCases")
    void testLowerBoundLookup(long[] keys, int[] offsets, long target, int expectedOffset) {
        assertEquals(expectedOffset, SSTableIndexLookup.findBlockOffset(keys, offsets, target));
    }

    // --- 2. Parametric test cases for rotated circular index buffers ---
    static Stream<Object[]> rotatedIndexTestCases() {
        return Stream.of(
            // Format: { keys, targetKey, expectedIndex }
            new Object[]{ new long[]{4, 5, 6, 7, 0, 1, 2}, 0, 4 }, // Standard rotated
            new Object[]{ new long[]{4, 5, 6, 7, 0, 1, 2}, 3, -1 }, // Not present
            new Object[]{ new long[]{0, 1, 2, 4, 5, 6, 7}, 5, 4 }, // Unrotated/standard sorted
            new Object[]{ new long[]{2, 0, 1}, 0, 1 },              // Single pivot shift
            new Object[]{ new long[]{5}, 5, 0 }                    // Single element
        );
    }

    @ParameterizedTest
    @MethodSource("rotatedIndexTestCases")
    void testRotatedBufferSearch(long[] keys, long target, int expectedIndex) {
        assertEquals(expectedIndex, SSTableIndexLookup.searchRotatedIndex(keys, target));
    }

    // --- 3. Parametric test cases for median key finds across two sorted partitions ---
    static Stream<Object[]> medianTestCases() {
        return Stream.of(
            // Format: { keysA, keysB, expectedMedian }
            new Object[]{ new long[]{1, 3}, new long[]{2}, 2.0 },                   // Odd total size
            new Object[]{ new long[]{1, 2}, new long[]{3, 4}, 2.5 },                 // Even total size
            new Object[]{ new long[]{10, 20, 30}, new long[]{5, 15, 25, 35}, 20.0 }, // Interleaved elements
            new Object[]{ new long[]{}, new long[]{1, 2, 3, 4}, 2.5 },               // One empty partition
            new Object[]{ new long[]{100}, new long[]{10, 20}, 20.0 }                 // Single vs multiple
        );
    }

    @ParameterizedTest
    @MethodSource("medianTestCases")
    void testMedianKeyFinder(long[] keysA, long[] keysB, double expectedMedian) {
        assertEquals(expectedMedian, SSTableIndexLookup.findMedianKey(keysA, keysB), 0.001);
    }

    // --- 4. Invalid parameters verification ---
    @Test
    void testInvalidParameters() {
        assertEquals(-1, SSTableIndexLookup.findBlockOffset(null, new int[]{1}, 10));
        assertEquals(-1, SSTableIndexLookup.searchRotatedIndex(new long[0], 10));
        assertThrows(IllegalArgumentException.class, () -> SSTableIndexLookup.findMedianKey(null, new long[0]));
    }

    // --- 5. High-scale stress testing ---
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

        // Verify lookup speed is within log range (sub-millisecond)
        double elapsedMs = elapsed / 1_000_000.0;
        assertEquals(true, elapsedMs < 2.0, "Stress search exceeded 2ms threshold. Took: " + elapsedMs + "ms");
        
        // Basic confirmation that median returned is valid boundary range
        assertEquals(true, median >= keysA[0] || median >= keysB[0]);
        assertEquals(true, median <= keysA[size - 1] || median <= keysB[size - 1]);
    }
}
