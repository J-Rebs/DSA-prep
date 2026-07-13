package com.engine;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * High-performance test data generator utility for generating massive algorithmic test datasets.
 * Designed to prevent object allocations and autoboxing overhead.
 */
public final class TestDataGenerator {

    private TestDataGenerator() {
        // Utility class
    }

    /**
     * Generates a primitive int array filled with random values.
     */
    public static int[] generateRandomArray(int size, int origin, int bound) {
        int[] arr = new int[size];
        Random rand = ThreadLocalRandom.current();
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(bound - origin) + origin;
        }
        return arr;
    }

    /**
     * Generates a sorted primitive int array.
     */
    public static int[] generateSortedArray(int size, int origin, int stepMax) {
        int[] arr = new int[size];
        Random rand = ThreadLocalRandom.current();
        int current = origin;
        for (int i = 0; i < size; i++) {
            current += rand.nextInt(stepMax) + 1;
            arr[i] = current;
        }
        return arr;
    }

    /**
     * Generates a monotonically decreasing primitive int array.
     */
    public static int[] generateDecreasingArray(int size, int startValue, int stepMax) {
        int[] arr = new int[size];
        Random rand = ThreadLocalRandom.current();
        int current = startValue;
        for (int i = 0; i < size; i++) {
            current -= (rand.nextInt(stepMax) + 1);
            arr[i] = current;
        }
        return arr;
    }

    /**
     * Generates a flat directed acyclic graph (DAG) represented as adjacency list coordinates.
     * Returns an array where even indices represent source nodes and odd indices represent destination nodes.
     */
    public static int[] generateRandomDAGEdges(int numVertices, int numEdges) {
        int[] edges = new int[numEdges * 2];
        Random rand = ThreadLocalRandom.current();
        int edgeCount = 0;
        
        while (edgeCount < numEdges) {
            int u = rand.nextInt(numVertices - 1);
            // In a DAG, u -> v is valid if u < v to prevent cycles
            int v = rand.nextInt(numVertices - (u + 1)) + (u + 1);
            
            edges[edgeCount * 2] = u;
            edges[edgeCount * 2 + 1] = v;
            edgeCount++;
        }
        return edges;
    }
}
