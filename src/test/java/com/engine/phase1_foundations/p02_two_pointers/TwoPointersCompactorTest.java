package com.engine.phase1_foundations.p02_two_pointers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class TwoPointersCompactorTest {

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    // 1. Pair with Target Sum
    static Stream<Object[]> pairWithTargetSumCases() {
        return Stream.of(
            new Object[]{ new int[]{1, 2, 3, 4, 6}, 6, new int[]{1, 3} },
            new Object[]{ new int[]{2, 5, 9, 11}, 11, new int[]{0, 2} },
            new Object[]{ new int[]{2, 5, 9, 11}, 12, new int[]{} }
        );
    }

    @ParameterizedTest
    @MethodSource("pairWithTargetSumCases")
    public void testPairWithTargetSum(int[] arr, int target, int[] expected) {
        assertArrayEquals(expected, TwoPointersCompactor.pairWithTargetSum(arr, target));
    }

    // 2. Remove Duplicates
    static Stream<Object[]> removeDuplicatesCases() {
        return Stream.of(
            new Object[]{ new int[]{2, 3, 3, 3, 6, 9, 9}, 4 },
            new Object[]{ new int[]{2, 2, 2, 2, 2}, 1 },
            new Object[]{ new int[]{}, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("removeDuplicatesCases")
    public void testRemoveDuplicates(int[] arr, int expected) {
        assertEquals(expected, TwoPointersCompactor.removeDuplicates(arr));
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    // 3. Squaring a Sorted Array
    static Stream<Object[]> squareSortedArrayCases() {
        return Stream.of(
            new Object[]{ new int[]{-2, -1, 0, 2, 3}, new int[]{0, 1, 4, 4, 9} },
            new Object[]{ new int[]{-3, -1, 0, 1, 2}, new int[]{0, 1, 1, 4, 9} },
            new Object[]{ new int[]{}, new int[]{} }
        );
    }

    @ParameterizedTest
    @MethodSource("squareSortedArrayCases")
    public void testSquareSortedArray(int[] arr, int[] expected) {
        assertArrayEquals(expected, TwoPointersCompactor.squareSortedArray(arr));
    }

    // 4. Triplet Sum to Zero
    static Stream<Object[]> tripletSumToZeroCases() {
        return Stream.of(
            new Object[]{ new int[]{-3, 0, 1, 2, -1, 1, -2}, 4 }, // Unique sums
            new Object[]{ new int[]{-5, 2, -1, -2, 3}, 2 }
        );
    }

    @ParameterizedTest
    @MethodSource("tripletSumToZeroCases")
    public void testTripletSumToZero(int[] arr, int expectedSize) {
        List<List<Integer>> result = TwoPointersCompactor.tripletSumToZero(arr);
        assertEquals(expectedSize, result.size());
    }

    // 5. Subarrays with Product Less than Target
    static Stream<Object[]> productLessThanTargetCases() {
        return Stream.of(
            new Object[]{ new int[]{2, 5, 3, 10}, 30, 6 },
            new Object[]{ new int[]{8, 2, 6, 5}, 50, 7 }
        );
    }

    @ParameterizedTest
    @MethodSource("productLessThanTargetCases")
    public void testSubarraysWithProductLessThanTarget(int[] arr, int target, int expected) {
        assertEquals(expected, TwoPointersCompactor.subarraysWithProductLessThanTarget(arr, target));
    }

    // 6. Compacting SSTable Logs
    static Stream<Object[]> compactSSTableLogsCases() {
        return Stream.of(
            new Object[]{ new int[]{1, -1, 2, -1, 3, 4}, -1, 4 },
            new Object[]{ new int[]{-1, -1, -1}, -1, 0 },
            new Object[]{ new int[]{5, 6, 7}, -1, 3 }
        );
    }

    @ParameterizedTest
    @MethodSource("compactSSTableLogsCases")
    public void testCompactSSTableLogs(int[] logs, int tombstone, int expected) {
        int newLength = TwoPointersCompactor.compactSSTableLogs(logs, tombstone);
        assertEquals(expected, newLength);
    }

    // 7. Min Window Sort
    static Stream<Object[]> minWindowSortCases() {
        return Stream.of(
            new Object[]{ new int[]{1, 2, 5, 3, 7, 10, 9, 12}, 5 },
            new Object[]{ new int[]{1, 3, 2, 0, 7, 5}, 6 },
            new Object[]{ new int[]{1, 2, 3}, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("minWindowSortCases")
    public void testMinWindowSort(int[] arr, int expected) {
        assertEquals(expected, TwoPointersCompactor.minWindowSort(arr));
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    // 8. Quadruple Sum
    @Test
    public void testQuadrupleSumToTarget() {
        int[] arr = new int[]{4, 1, 2, -1, 1, -3};
        List<List<Integer>> result = TwoPointersCompactor.quadrupleSumToTarget(arr, 1);
        assertEquals(2, result.size());
    }

    // 9. Dutch National Flag
    @Test
    public void testDutchNationalFlag() {
        int[] arr = new int[]{2, 2, 0, 1, 2, 0};
        TwoPointersCompactor.dutchNationalFlag(arr);
        assertArrayEquals(new int[]{0, 0, 1, 2, 2, 2}, arr);
    }

    @Nested
    public class RigorousGatekeeper {

        @Test
        public void testBoundaryConditions() {
            // Null and empty safety checks
            assertEquals(-1, TwoPointersCompactor.removeDuplicates(null));
            assertEquals(0, TwoPointersCompactor.removeDuplicates(new int[]{}));

            assertArrayEquals(new int[]{}, TwoPointersCompactor.pairWithTargetSum(null, 5));
            assertArrayEquals(new int[]{}, TwoPointersCompactor.pairWithTargetSum(new int[]{}, 5));

            assertArrayEquals(new int[]{}, TwoPointersCompactor.squareSortedArray(null));
            assertArrayEquals(new int[]{}, TwoPointersCompactor.squareSortedArray(new int[]{}));

            assertEquals(0, TwoPointersCompactor.tripletSumToZero(null).size());
            assertEquals(0, TwoPointersCompactor.tripletSumToZero(new int[]{1, 2}).size()); // size < 3

            assertEquals(-1, TwoPointersCompactor.subarraysWithProductLessThanTarget(null, 10));
            assertEquals(-1, TwoPointersCompactor.subarraysWithProductLessThanTarget(new int[]{1, 2}, -10));

            assertEquals(-1, TwoPointersCompactor.compactSSTableLogs(null, -1));
            assertEquals(0, TwoPointersCompactor.compactSSTableLogs(new int[]{}, -1));

            assertEquals(-1, TwoPointersCompactor.minWindowSort(null));
            assertEquals(0, TwoPointersCompactor.minWindowSort(new int[]{}));

            assertEquals(0, TwoPointersCompactor.quadrupleSumToTarget(null, 5).size());
            assertEquals(0, TwoPointersCompactor.quadrupleSumToTarget(new int[]{1, 2, 3}, 5).size()); // size < 4

            // Dutch National Flag bounds
            int[] nullArr = null;
            TwoPointersCompactor.dutchNationalFlag(nullArr); // should handle null gracefully without crash
            int[] emptyArr = new int[]{};
            TwoPointersCompactor.dutchNationalFlag(emptyArr); // empty check
        }

        @Test
        public void testTimeComplexityBounds() {
            int limit = 100_000;
            int[] arr = new int[limit];
            // Fully sorted containing 0, 1, 2 to test Dutch National Flag partitioning scale
            for (int i = 0; i < limit; i++) {
                arr[i] = i % 3;
            }
            assertTimeoutPreemptively(Duration.ofMillis(30), () -> {
                TwoPointersCompactor.dutchNationalFlag(arr);
            });
        }

        @Test
        public void testMemoryAllocations() {
            java.lang.management.ThreadMXBean threadBean = java.lang.management.ManagementFactory.getThreadMXBean();
            java.lang.reflect.Method isSupportedMethod = null;
            java.lang.reflect.Method setEnabledMethod = null;
            java.lang.reflect.Method getAllocatedMethod = null;
            try {
                isSupportedMethod = threadBean.getClass().getMethod("isThreadAllocatedMemorySupported");
                setEnabledMethod = threadBean.getClass().getMethod("setThreadAllocatedMemoryEnabled", boolean.class);
                getAllocatedMethod = threadBean.getClass().getMethod("getThreadAllocatedMemory", long.class);
            } catch (NoSuchMethodException e) {
                // Not supported on this JVM
            }

            if (isSupportedMethod != null && getAllocatedMethod != null && setEnabledMethod != null) {
                try {
                    boolean supported = (boolean) isSupportedMethod.invoke(threadBean);
                    if (supported) {
                        setEnabledMethod.invoke(threadBean, true);
                        long threadId = Thread.currentThread().getId();

                        int[] arr = new int[]{1, 2, 2, 3, 3, 3, 4, 4, 5};

                        // Warm-up
                        for (int i = 0; i < 5000; i++) {
                            // Copying array here is okay, we want to measure within the hot method itself
                            int[] temp = new int[]{1, 2, 2, 3, 3, 3, 4, 4, 5};
                            TwoPointersCompactor.removeDuplicates(temp);
                        }

                        // Run loop on single pre-allocated array (so the array copy allocation is outside the measurement window)
                        long bytesBefore = (long) getAllocatedMethod.invoke(threadBean, threadId);
                        for (int i = 0; i < 10000; i++) {
                            TwoPointersCompactor.removeDuplicates(arr);
                        }
                        long bytesAfter = (long) getAllocatedMethod.invoke(threadBean, threadId);

                        long allocatedSum = bytesAfter - bytesBefore;
                        assertTrue(allocatedSum < 1024, "removeDuplicates allocated too many bytes: " + allocatedSum + " bytes");
                    }
                } catch (Exception e) {
                    // Ignore reflection invoke errors
                }
            }
        }
    }
}
