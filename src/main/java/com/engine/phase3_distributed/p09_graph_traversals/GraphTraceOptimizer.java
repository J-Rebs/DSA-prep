package com.engine.phase3_distributed.p09_graph_traversals;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

/**
 * High-Performance Graph BFS/DFS & Grid Traversal Engine.
 * Mapped to Call Graph Trace Optimization & Distributed Network Topology.
 */
public final class GraphTraceOptimizer {

    private GraphTraceOptimizer() {
        // Utility class
    }

    // ==========================================
    // PHASE 1: EASY WARMUPS
    // ==========================================

    /**
     * Problem 1: Flood Fill Grid (Pixel Color Ingest)
     * Changes starting cell and all 4-directionally connected same-colored cells to
     * color.
     */
    public static int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int originalColor = image[sr][sc];
        return floodFillHelper(image, sr, sc, color, originalColor);
    }

    public static int[][] floodFillHelper(int[][] image, int sr, int sc, int color, int originalColor) {
        // saftey guard boundary checks
        if (image == null || image[0] == null || sr < 0
                || sr >= image.length || sc < 0 || sc >= image[0].length) {
            return image;
        }
        // then we can do a base case check either we've already painted here
        // or it is a spot we cannot paint
        if (image[sr][sc] == color || image[sr][sc] == 0 || image[sr][sc] != originalColor) {
            return image;
        }
        // update
        image[sr][sc] = color;
        // then we can recurse
        int[][] dirs = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        for (int[] dir : dirs) {
            floodFillHelper(image, sr + dir[0], sc + dir[1], color, originalColor);
        }

        return image;
    }

    /**
     * Problem 2: Island Count (Connected Microservice Cluster Locator)
     * Counts number of 4-directionally connected islands of '1's in a 2D grid.
     */
    public static int numIslands(char[][] grid) {
        // saftey guard
        if (grid == null || grid[0] == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int islandCount = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == '1') {
                    islandCount++;
                    numIslandsHelper(grid, r, c);
                }
            }
        }
        return islandCount;
    }

    public static void numIslandsHelper(char[][] grid, int r, int c) {
        // base case
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) {
            return;
        }
        // check if valid island plot
        if (grid[r][c] == '0') {
            return;
        }
        // mark plot as seen by sinking
        grid[r][c] = '0';

        // explore new plots
        int[][] dirs = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        for (int[] dir : dirs) {
            numIslandsHelper(grid, r + dir[0], c + dir[1]);
        }
    }

    /**
     * Problem 3: Max Island Area (Largest Data Center Subnet Size)
     * Finds maximum area of an island of 1s in a 2D grid.
     */
    public static int maxAreaOfIsland(int[][] grid) {
        // saftey guard
        if (grid == null || grid[0] == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        // computation
        int maxIslandArea = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 1) {
                    maxIslandArea = Math.max(maxIslandArea, calculateIslandArea(grid, r, c));
                }
            }
        }
        return maxIslandArea;
    }

    public static int calculateIslandArea(int[][] grid, int r, int c) {
        // boundary check
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) {
            return 0;
        }
        // otherwise check if plot is land
        if (grid[r][c] == 0) {
            return 0;
        }
        // if is land add to answer after sinking
        grid[r][c] = 0;
        int[][] DIRS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        int area = 1;
        for (int[] dir : DIRS) {
            area += calculateIslandArea(grid, r + dir[0], c + dir[1]);
        }

        return area;
    }

    // ==========================================
    // PHASE 2: MEDIUM SYSTEM INTEGRATION
    // ==========================================

    /**
     * Problem 4: Pacific Atlantic Water Flow (Bi-directional Network Ingress)
     * Finds coordinates where water can flow to both Pacific and Atlantic oceans.
     */
    public static List<List<Integer>> pacificAtlantic(int[][] heights) {
        // saftey guard
        if (heights == null || heights.length == 0 || heights[0] == null || heights[0].length == 0) {
            return List.of();
        }
        // set up reached and visited
        boolean[][] visitedPacific = new boolean[heights.length][heights[0].length];
        boolean[][] visitedAtlantic = new boolean[heights.length][heights[0].length];
        int[][] reached = new int[heights.length][heights[0].length];

        // traverse
        // start from first and last row
        for (int c = 0; c < heights[0].length; c++) {
            invertedWaterFlow(heights, reached, visitedPacific, 0, c);
            invertedWaterFlow(heights, reached, visitedAtlantic, heights.length - 1, c);
        }
        // and also start from first and last column
        for (int r = 0; r < heights.length; r++) {
            invertedWaterFlow(heights, reached, visitedPacific, r, 0);
            invertedWaterFlow(heights, reached, visitedAtlantic, r, heights[0].length - 1);
        }

        // process results
        List<List<Integer>> res = new ArrayList<>();

        for (int r = 0; r < heights.length; r++) {
            for (int c = 0; c < heights[0].length; c++) {
                if (reached[r][c] == 2) {
                    res.add(List.of(r, c));
                }
            }
        }

        return res;
    }

    public static void invertedWaterFlow(int[][] heights, int[][] reached, boolean[][] visited, int r, int c) {
        // are we at aa valid spot, gate this in case first call is not coming from
        // recursion
        if (r < 0 || c < 0 || r >= heights.length || c >= heights[0].length) {
            return;
        }
        // has this spot been visited
        if (visited[r][c]) {
            return;
        }
        // if not, then we should visit it
        visited[r][c] = true;
        reached[r][c] += 1;
        // then traverse
        int[][] dirs = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        for (int[] dir : dirs) {
            // for a given dir, we have to check if the next step is valid
            int newR = r + dir[0];
            int newC = c + dir[1];
            // first, will it be on the board
            if (newR < 0 || newC < 0 || newR >= heights.length || newC >= heights[0].length) {
                continue;
            }
            // next, would where we want to go be uphill from where we are
            // implies water could flow from there to here
            // note: equivalent to check the inverse and skip recursion
            if (heights[newR][newC] < heights[r][c]) {
                continue;
            }
            invertedWaterFlow(heights, reached, visited, r + dir[0], c + dir[1]);
        }

    }

    /**
     * Problem 5: Surrounded Regions (Deadlock Memory Cell Reclaimer)
     * Captures all 'O' regions completely surrounded by 'X'.
     */
    public static void solveSurroundedRegions(char[][] board) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) {
            return;
        }
        int rows = board.length;
        int cols = board[0].length;

        // figure out which O are reachable from edge
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // do the top and bottow row
                if (r == 0 || r == rows - 1) {
                    doDFS(board, r, c);
                }
                // do the left and right column
                if (c == 0 || c == cols - 1) {
                    doDFS(board, r, c);
                }
            }
        }
        // those which were remain 'O' otherwise they become 'X'
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 'O') {
                    board[r][c] = 'X';
                } else if (board[r][c] == 'E') {
                    board[r][c] = 'O';
                }
            }
        }
    }

    public static void doDFS(char[][] board, int r, int c) {
        // we dont recurse if we see a O (inner region would remain unreached)
        // since we assume we only start from edge cells
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != 'O') {
            return;
        }
        // mark safe
        board[r][c] = 'E';
        doDFS(board, r + 1, c);
        doDFS(board, r - 1, c);
        doDFS(board, r, c + 1);
        doDFS(board, r, c - 1);

    }

    /**
     * Problem 6: Rotting Oranges / Outage Spread (Cascading Latency Spike
     * Propagation)
     * Finds minimum time for all fresh oranges (1) to become rotten (2) via 4-way
     * BFS.
     */
    public static int orangesRotting(int[][] grid) {
        // saftey guards
        if (grid == null || grid[0] == null || grid.length == 0 || grid[0].length == 0) {
            return -1;
        }
        // find out how many fresh oranges we have.
        int freshOrangeCount = 0;

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 1) {
                    freshOrangeCount++;
                }
            }
        }
        // use BFS to count time ticks till no freshOranges
        int timeTick = 0;
        int[][] dirs = new int[][] { { 1, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 } };
        Queue<int[]> q = new ArrayDeque<>();
        // push any rotten oranges as the start
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 2) {
                    q.offer(new int[] { r, c });
                }
            }
        }
        // then process in layers
        while (!q.isEmpty()) {
            // evaluate if any fresh oranges at start of each layer, handles
            // case there were never any to start
            if (freshOrangeCount == 0) {
                return timeTick;
            }
            int layerSize = q.size();
            timeTick++;
            while (layerSize != 0) {
                int[] cellCoordinates = q.poll();
                int r = cellCoordinates[0];
                int c = cellCoordinates[1];

                // process neighbors, note no other checks required
                // because we only push 2 to the queue
                for (int[] dir : dirs) {
                    // get neighbor coordinates
                    int nR = r + dir[0];
                    int nC = c + dir[1];
                    // if it is invalid ignore it
                    if (nR < 0 || nC < 0 || nR >= grid.length || nC >= grid[0].length) {
                        continue;
                    }
                    // if 2 or 0 ignore
                    if (grid[nR][nC] == 2 || grid[nR][nC] == 0) {
                        continue;
                    }
                    // otherwise is 1, and we can push
                    // assuming valid input, otherwise can do if == 1
                    grid[nR][nC] = 2;
                    freshOrangeCount--;
                    q.offer(new int[] { nR, nC });
                }
                layerSize--;
            }
        }
        // if cannot get rid of all fresh oranges return -1
        return -1;
    }

    /**
     * Problem 7: 01 Matrix Nearest Distance (Service Mesh RPC Latency Map)
     * Finds distance of nearest 0 for each cell in a binary matrix.
     */
    public static int[][] updateMatrix(int[][] mat) {
        // Multi-Source BFS: Expand outward from all '0's simultaneously.
        // First visit to any cell via BFS guarantees the minimum distance from the
        // nearest '0'.

        // saftey guards
        if (mat == null || mat[0] == null || mat.length == 0 || mat[0].length == 0) {
            return mat;
        }
        // initial processing, find 0 and pick diff value for unvisited
        // since 1 is a valid distance
        Queue<int[]> q = new ArrayDeque<>();
        int[][] dirs = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        for (int r = 0; r < mat.length; r++) {
            for (int c = 0; c < mat[0].length; c++) {
                if (mat[r][c] == 0) {
                    q.offer(new int[] { r, c });
                } else {
                    mat[r][c] = Integer.MIN_VALUE;
                }
            }
        }
        // now do BFS
        while (!q.isEmpty()) {
            int[] coord = q.poll();
            int r = coord[0];
            int c = coord[1];

            // assume we've only pushed valid coordinates, so process neighbors directly
            for (int[] dir : dirs) {
                int nR = r + dir[0];
                int nC = c + dir[1];
                // if not valid skip
                if (nR < 0 || nC < 0 || nR >= mat.length || nC >= mat[0].length) {
                    continue;
                }
                // if not an unvisited value skip
                if (mat[nR][nC] != Integer.MIN_VALUE) {
                    continue;
                }
                // otherwise process
                // notice that by updating values when process neighbors
                // we correctly leave 0 cells w/ 0 distance
                mat[nR][nC] = mat[r][c] + 1;
                q.offer(new int[] { nR, nC });
            }
        }

        return mat;
    }

    /**
     * Problem 8: Word Ladder (RPC Method Schema Mutation Path)
     * Finds length of shortest transformation sequence from beginWord to endWord.
     */
    public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        // saftey guard
        if (beginWord == null || endWord == null || wordList == null || wordList.size() == 0) {
            return 0;
        }
        // key: a single transformation is defined as a change in one letter
        Queue<String> wordQueue = new ArrayDeque<>();
        HashSet<String> wordSet = new HashSet<>(wordList);
        wordQueue.offer(beginWord);
        int transforms = 0;

        while (!wordQueue.isEmpty()) {
            // do bfs by level since each neighbor represents the same number of transforms
            transforms++;
            int levelSize = wordQueue.size();
            for (int i = 0; i < levelSize; i++) {
                // get a word
                char[] wordArray = wordQueue.poll().toCharArray();
                // try to transform it
                for (int wordIndex = 0; wordIndex < wordArray.length; wordIndex++) {
                    char originalChar = wordArray[wordIndex];
                    for (char c = 'a'; c <= 'z'; c++) {
                        // if what we had before, skip
                        if (c == originalChar) {
                            continue;
                        }
                        wordArray[wordIndex] = c;
                        // change by 1 yields a neighbor
                        String neighbor = new String(wordArray);

                        // check that this neighbor actually exists
                        // then we can either mark it as the answer or
                        // add it to the queueu
                        // we can add neighbor to the queue
                        if (wordSet.remove(neighbor)) {
                            if (neighbor.equals(endWord)) {
                                // plus 1 for this latest transformation
                                return transforms + 1;
                            }
                            wordQueue.offer(neighbor);
                        }
                    }
                    // restore original character before moving to next index
                    wordArray[wordIndex] = originalChar;
                }

            }
        }
        return 0;
    }

    /**
     * Problem 9: Clone Graph (Distributed RPC Microservice Call-Graph Replicator)
     * Creates a deep copy of a connected undirected graph.
     */
    public static Node cloneGraph(Node node) {
        // TODO: Implement Graph Clone BFS/DFS
        return null;
    }

    public static class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new java.util.ArrayList<>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new java.util.ArrayList<>();
        }

        public Node(int _val, List<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }

    // ==========================================
    // PHASE 3: HARD SCALE & STRESS
    // ==========================================

    /**
     * Problem 10: Word Ladder II (All Shortest Transformation Paths)
     * Finds all shortest transformation sequences from beginWord to endWord.
     */
    public static List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        // TODO: Implement BFS + Backtracking for Word Ladder II
        return List.of();
    }

    /**
     * Problem 11: Shortest Path in 2D Matrix with Obstacle Elimination
     * Finds shortest path from (0,0) to (m-1, n-1) eliminating up to k obstacles.
     */
    public static int shortestPathWithObstacles(int[][] grid, int k) {
        // TODO: Implement State-based 3D BFS
        return -1;
    }
}
