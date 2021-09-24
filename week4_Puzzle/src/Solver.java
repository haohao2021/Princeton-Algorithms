import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final Stack<Board> result;
    private final int moves;

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final int moves;
        private final SearchNode pre;
        private final int priority;

        SearchNode(Board b, SearchNode p) {
            board = b;
            if (p == null)
                moves = 0;
            else
                moves = p.moves + 1;
            pre = p;
            priority = board.manhattan() + moves;
        }

        public int compareTo(SearchNode that) {
            return this.priority - that.priority;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        // use pq to find the goal
        MinPQ<SearchNode> pq = new MinPQ<>();
        MinPQ<SearchNode> twinPQ = new MinPQ<>();
        pq.insert(new SearchNode(initial, null));
        twinPQ.insert(new SearchNode(initial.twin(), null));
        SearchNode min, twinMin;
        while (true) {
            min = pq.delMin();
            if (min.board.isGoal()) break;
            twinMin = twinPQ.delMin();
            if (twinMin.board.isGoal()) break;
            for (Board b : min.board.neighbors()) {
                if (min.pre == null || !b.equals(min.pre.board)) {
                    pq.insert(new SearchNode(b, min));
                }
            }
            for (Board b : twinMin.board.neighbors()) {
                if (twinMin.pre == null || !b.equals(twinMin.pre.board)) {
                    twinPQ.insert(new SearchNode(b, twinMin));
                }
            }
        }
        // determine whether solvable
        if (!min.board.isGoal()) {
            moves = -1;
            result = null;
        }
        else {
            // track the solution trace and get the result
            result = new Stack<>();
            SearchNode p = min;
            while (p != null) {
                result.push(p.board);
                p = p.pre;
            }
            moves = min.moves;
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return result != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return result;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In("/Users/haohao/IdeaProjects/Princeton_Algorithms/week4_Puzzle/src/puzzle4x4-04.txt");
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
