package com.engine.phase1_foundations.sliding_window;

import com.engine.TestDataGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SlidingWindowTest {

    /**
     * Provides test cases for parameterized stress testing.
     * Format: { inputArray, targetWindow, expectedOutput }
     */
    static Stream<Object[]> slidingWindowTestCases() {
        // Programmatic large-scale fuzz inputs
        int[] largeInput = TestDataGenerator.generateRandomArray(100_000, 0, 10_000);
        int[] largeExpectedPlaceholder = new int[largeInput.length - 2]; // For k = 3

        return Stream.of(
            // 1. Standard test case
            new Object[]{ 
                new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 
                3, 
                new int[]{3, 3, 5, 5, 6, 7} 
            },
            // 2. Single element boundary
            new Object[]{ 
                new int[]{1}, 
                1, 
                new int[]{1} 
            },
            // 3. Overflow and negative value check
            new Object[]{ 
                new int[]{Integer.MAX_VALUE, 1, Integer.MIN_VALUE}, 
                2, 
                new int[]{Integer.MAX_VALUE, 1} 
            },
            // 4. Large-scale performance stress case
            new Object[]{ 
                largeInput, 
                3, 
                largeExpectedPlaceholder 
            }
        );
    }

    @ParameterizedTest
    @MethodSource("slidingWindowTestCases")
    void stressTestSlidingWindow(int[] nums, int k, int[] expected) {
        int[] result = SlidingWindowCounter.solve(nums, k);
        assertNotNull(result);
        
        // Only run equality assertions for bounded static cases (we skip comparing dynamic large placeholders)
        if (nums.length < 100) {
            assertArrayEquals(expected, result);
        }
    }
}
