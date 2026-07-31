package com.engine.phase3_distributed.p09_graph_traversals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GraphTraceOptimizerTest {

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    // 1. Flood Fill Grid
    static Stream<Object[]> floodFillTestCases() {
        return Stream.of(
            new Object[]{
                new int[][]{{1,1,1},{1,1,0},{1,0,1}}, 1, 1, 2,
                new int[][]{{2,2,2},{2,2,0},{2,0,1}}
            },
            new Object[]{
                new int[][]{{0,0,0},{0,0,0}}, 0, 0, 0,
                new int[][]{{0,0,0},{0,0,0}}
            }
        );
    }

    @ParameterizedTest
    @MethodSource("floodFillTestCases")
    void testFloodFill(int[][] image, int sr, int sc, int color, int[][] expected) {
        assertArrayEquals(expected, GraphTraceOptimizer.floodFill(image, sr, sc, color));
    }

    // 2. Island Count
    static Stream<Object[]> islandCountTestCases() {
        return Stream.of(
            new Object[]{
                new char[][]{
                    {'1','1','1','1','0'},
                    {'1','1','0','1','0'},
                    {'1','1','0','0','0'},
                    {'0','0','0','0','0'}
                }, 1
            },
            new Object[]{
                new char[][]{
                    {'1','1','0','0','0'},
                    {'1','1','0','0','0'},
                    {'0','0','1','0','0'},
                    {'0','0','0','1','1'}
                }, 3
            }
        );
    }

    @ParameterizedTest
    @MethodSource("islandCountTestCases")
    void testIslandCount(char[][] grid, int expectedIslands) {
        assertEquals(expectedIslands, GraphTraceOptimizer.numIslands(grid));
    }

    // 3. Max Island Area
    static Stream<Object[]> maxIslandAreaTestCases() {
        return Stream.of(
            new Object[]{
                new int[][]{
                    {0,0,1,0,0,0,0,1,0,0,0,0,0},
                    {0,0,0,0,0,0,0,1,1,1,0,0,0},
                    {0,1,1,0,1,0,0,0,0,0,0,0,0},
                    {0,1,0,0,1,1,0,0,1,0,1,0,0},
                    {0,1,0,0,1,1,0,0,1,1,1,0,0},
                    {0,0,0,0,0,0,0,0,0,0,1,0,0},
                    {0,0,0,0,0,0,0,1,1,1,0,0,0},
                    {0,0,0,0,0,0,0,1,1,0,0,0,0}
                }, 6
            },
            new Object[]{ new int[][]{{0,0,0,0,0,0,0,0}}, 0 }
        );
    }

    @ParameterizedTest
    @MethodSource("maxIslandAreaTestCases")
    void testMaxIslandArea(int[][] grid, int expectedMaxArea) {
        assertEquals(expectedMaxArea, GraphTraceOptimizer.maxAreaOfIsland(grid));
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    // 4. Pacific Atlantic Water Flow
    @Test
    void testPacificAtlantic() {
        int[][] heights = {
            {1,2,2,3,5},
            {3,2,3,4,4},
            {2,4,5,3,1},
            {6,7,1,4,5},
            {5,1,1,2,4}
        };
        List<List<Integer>> result = GraphTraceOptimizer.pacificAtlantic(heights);
        List<List<Integer>> expected = List.of(
            List.of(0, 4), List.of(1, 3), List.of(1, 4), List.of(2, 2),
            List.of(3, 0), List.of(3, 1), List.of(4, 0)
        );
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
    }

    // 5. Surrounded Regions
    @Test
    void testSurroundedRegions() {
        char[][] board = {
            {'X','X','X','X'},
            {'X','O','O','X'},
            {'X','X','O','X'},
            {'X','O','X','X'}
        };
        char[][] expected = {
            {'X','X','X','X'},
            {'X','X','X','X'},
            {'X','X','X','X'},
            {'X','O','X','X'}
        };
        GraphTraceOptimizer.solveSurroundedRegions(board);
        assertArrayEquals(expected, board);
    }

    // 6. Rotting Oranges
    @Test
    void testRottingOranges() {
        int[][] grid1 = {
            {2,1,1},
            {1,1,0},
            {0,1,1}
        };
        assertEquals(4, GraphTraceOptimizer.orangesRotting(grid1));

        int[][] grid2 = {
            {2,1,1},
            {0,1,1},
            {1,0,1}
        };
        assertEquals(-1, GraphTraceOptimizer.orangesRotting(grid2));

        int[][] grid3 = {{0,2}};
        assertEquals(0, GraphTraceOptimizer.orangesRotting(grid3));
    }

    // 7. 01 Matrix Nearest Distance
    @Test
    void testUpdateMatrix() {
        int[][] mat1 = {
            {0,0,0},
            {0,1,0},
            {1,1,1}
        };
        int[][] expected1 = {
            {0,0,0},
            {0,1,0},
            {1,2,1}
        };
        assertArrayEquals(expected1, GraphTraceOptimizer.updateMatrix(mat1));

        int[][] mat2 = {
            {0,0,0},
            {0,1,0},
            {0,0,0}
        };
        int[][] expected2 = {
            {0,0,0},
            {0,1,0},
            {0,0,0}
        };
        assertArrayEquals(expected2, GraphTraceOptimizer.updateMatrix(mat2));
    }

    // 8. Word Ladder
    @Test
    void testWordLadder() {
        List<String> wordList = List.of("hot","dot","dog","lot","log","cog");
        assertEquals(5, GraphTraceOptimizer.ladderLength("hit", "cog", wordList));
    }

    // 9. Clone Graph
    @Test
    void testCloneGraph() {
        GraphTraceOptimizer.Node n1 = new GraphTraceOptimizer.Node(1);
        GraphTraceOptimizer.Node n2 = new GraphTraceOptimizer.Node(2);
        n1.neighbors.add(n2);
        n2.neighbors.add(n1);

        GraphTraceOptimizer.Node cloned = GraphTraceOptimizer.cloneGraph(n1);
        // Basic check
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    // 10. Word Ladder II
    @Test
    void testWordLadderII() {
        List<String> wordList = List.of("hot","dot","dog","lot","log","cog");
        List<List<String>> result = GraphTraceOptimizer.findLadders("hit", "cog", wordList);
        assertNotNull(result);
    }

    // 11. Shortest Path in 2D Matrix with Obstacle Elimination
    @Test
    void testShortestPathWithObstacles() {
        int[][] grid = {
            {0,0,0},
            {1,1,0},
            {0,0,0},
            {0,1,1},
            {0,0,0}
        };
        assertEquals(6, GraphTraceOptimizer.shortestPathWithObstacles(grid, 1));
    }
}
