import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {
    private final int[][] board;
    private final int n;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        board = new int[tiles.length][tiles.length];
        n = tiles.length;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                board[i][j] = tiles[i][j];
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(board.length + "\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++)
                s.append(String.format("%2d ", board[i][j]));
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    private int getIndex(int row, int col) {
        return row * n + col + 1;
    }

    // number of tiles out of place
    public int hamming() {
        int ham = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (getIndex(i, j) == n * n)
                    break;
                if (board[i][j] != getIndex(i, j))
                    ham++;
            }
        }
        return ham;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manh = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 0) {
                    continue;
                }
                int row = (board[i][j] - 1) / n;
                int col = (board[i][j] - 1) % n;
                manh += Math.abs(row - i) + Math.abs(col - j);
            }
        }
        return manh;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 0) {
                    continue;
                }
                int row = (board[i][j] - 1) / n;
                int col = (board[i][j] - 1) % n;
                if (i != row || j != col) {
                    return false;
                }
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this)
            return true;
        if (y == null)
            return false;
        if (y.getClass() != this.getClass())
            return false;
        Board yy = (Board) y;
        // 在Board类中，可以访问到各个Board的实例变量
        return Arrays.deepEquals(board, yy.board);
    }

    private void swap(int row1, int col1, int row2, int col2) {
        int temp = board[row1][col1];
        board[row1][col1] = board[row2][col2];
        board[row2][col2] = temp;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ArrayList<Board> a = new ArrayList<>();
        int col0 = -1, row0 = -1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 0) {
                    row0 = i;
                    col0 = j;
                    break;
                }
            }
        }
        // top neighbor
        if (row0 - 1 >= 0) {
            Board top = new Board(board);
            top.swap(row0 - 1, col0, row0, col0);
            a.add(top);
        }
        // bottom neighbor
        if (row0 + 1 <= n - 1) {
            Board bottom = new Board(board);
            bottom.swap(row0 + 1, col0, row0, col0);
            a.add(bottom);
        }
        // left neighbor
        if (col0 - 1 >= 0) {
            Board left = new Board(board);
            left.swap(row0, col0 - 1, row0, col0);
            a.add(left);
        }
        // right neighbor
        if (col0 + 1 <= n - 1) {
            Board right = new Board(board);
            right.swap(row0, col0 + 1, row0, col0);
            a.add(right);
        }
        return a;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int a = 1;
        while (board[(a - 1) / n][(a - 1) % n] == 0 && a < n * n) {
            a++;
        }
        int b = a + 1;
        while (board[(b - 1) / n][(b - 1) % n] == 0 && b < n * n) {
            b++;
        }
        int rowa = (a - 1) / n, cola = (a - 1) % n, rowb = (b - 1) / n, colb = (b - 1) % n;
        Board twin = new Board(board);
        twin.swap(rowa, cola, rowb, colb);
        return twin;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        In in = new In("/Users/haohao/IdeaProjects/Princeton_Algorithms/week4_Puzzle/src/puzzle4x4-04.txt");
        int n = in.readInt();
        int[][] borad = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                borad[i][j] = in.readInt();
        Board initial = new Board(borad);

        System.out.println(initial.toString());
        System.out.println(initial.hamming());
        System.out.println(initial.manhattan());

        for (Board b : initial.neighbors()) {
            System.out.println(b.toString());
        }

        Board bb = initial.twin();
        System.out.println(bb.toString());
        System.out.println(bb.isGoal());
        Board cc = initial.twin();
        System.out.println(cc.toString());
        System.out.println(cc.isGoal());

        System.out.println(initial.equals(initial.twin()));
    }
}
