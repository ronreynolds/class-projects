package com.ronreynolds.games.sudoku;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SudokuTest {
    private static final int[][] solvedGrid = {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 8, 9, 1, 2, 3, 4, 5, 6},
            {2, 3, 4, 5, 6, 7, 8, 9, 1},
            {5, 6, 7, 8, 9, 1, 2, 3, 4},
            {8, 9, 1, 2, 3, 4, 5, 6, 7},
            {3, 4, 5, 6, 7, 8, 9, 1, 2},
            {6, 7, 8, 9, 1, 2, 3, 4, 5},
            {9, 1, 2, 3, 4, 5, 6, 7, 8},
    };

    @Test
    void isValidIsSolved_works() {
        SudokuPuzzle sudokuPuzzle;
        sudokuPuzzle = SudokuPuzzle.create(solvedGrid);
        assertThat(sudokuPuzzle.isValid()).isTrue();
        System.out.println("can detect a valid puzzle");

        assertThat(sudokuPuzzle.isSolved()).isTrue();
        System.out.println("can detect a solved puzzle");

        assertThat(SudokuPuzzle.create(new int[][]{
                {1, 1, 3, 4, 5, 6, 7, 8, 9}, // 2 1's
                {4, 5, 6, 7, 8, 9, 1, 2, 3},
                {7, 8, 9, 1, 2, 3, 4, 5, 6},
                {2, 3, 4, 5, 6, 7, 8, 9, 1},
                {5, 6, 7, 8, 9, 1, 2, 3, 4},
                {8, 9, 1, 2, 3, 4, 5, 6, 7},
                {3, 4, 5, 6, 7, 8, 9, 1, 2},
                {6, 7, 8, 9, 1, 2, 3, 4, 5},
                {9, 1, 2, 3, 4, 5, 6, 7, 8},
        }).isValid()).isFalse();
        System.out.println("can detect an invalid puzzle");

        sudokuPuzzle = SudokuPuzzle.create(blankCharGrid(9, 9));
        assertThat(sudokuPuzzle.isValid()).isTrue();
        assertThat(sudokuPuzzle.isSolved()).isFalse();
        System.out.println("can detect an empty puzzle is valid and unsolved");
    }

    @Test
    void enforcesValidInputs_char() {
        char[][] grid = blankCharGrid(8, 9);    // grid's too small
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> SudokuPuzzle.create(grid));
        assertThat(ex).hasMessage("knownCells has wrong number of rows - 8");
        ex = assertThrows(IllegalArgumentException.class,
                () -> SudokuPuzzle.create(new char[][]{
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.',},
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.',},
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'}, // extra cell
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.',},
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.',},
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.',},
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.',},
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.',},
                        {'.', '.', '.', '.', '.', '.', '.', '.', '.',},
                }));
        assertThat(ex).hasMessage("row[2] has wrong number of values - 10");

        char[][] grid2 = blankCharGrid(9, 9);
        grid2[0][0] = 'X';
        ex = assertThrows(IllegalArgumentException.class, () -> SudokuPuzzle.create(grid2));
        assertThat(ex).hasMessage("invalid value (X) at (0, 0)");
    }

    @Test
    void enforcesValidInputs_int() {
        int[][] grid = blankIntGrid(8, 9);    // grid's too small
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> SudokuPuzzle.create(grid));
        assertThat(ex).hasMessage("knownCells has wrong number of rows - 8");
        ex = assertThrows(IllegalArgumentException.class,
                () -> SudokuPuzzle.create(new int[][]{
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // extra col
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                }));
        assertThat(ex).hasMessage("row[2] has wrong number of values - 10");

        int[][] grid2 = blankIntGrid(9, 9);
        grid2[1][2] = 42;
        ex = assertThrows(IllegalArgumentException.class, () -> SudokuPuzzle.create(grid2));
        assertThat(ex).hasMessage("invalid value (42) at (1, 2)");
    }

    @Test
    void solve_detectsNotSolved() {
        // can be solved with naked-singles rule only
        SudokuPuzzle sudokuPuzzle = SudokuPuzzle.create(new int[][]{
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {4, 5, 6, 7, 8, 9, 1, 2, 3},
                {7, 8, 9, 1, 2, 3, 4, 5, 6},
                {2, 3, 4, 5, 6, 7, 8, 9, 1},
                {5, 6, 7, 8, 9, 1, 2, 3, 4},
                {8, 9, 1, 2, 3, 4, 5, 6, 7},
                {3, 4, 5, 6, 7, 8, 9, 1, 2},
                {6, 7, 8, 9, 1, 2, 3, 4, 5},
                {0, 1, 2, 3, 4, 5, 6, 7, 8}, // 1 unsolved value
        });
        assertThat(sudokuPuzzle.isSolved()).isFalse();
        System.out.println("can detect that a puzzle isn't solved");

        Sudoku.solve(sudokuPuzzle);
        assertThat(sudokuPuzzle.isSolved()).isTrue();
        System.out.println("can solve a simple puzzle");
    }

    @Test
    void verifyCellPositions() {
        SudokuPuzzle puzzle = SudokuPuzzle.create(solvedGrid);

        System.out.println("puzzle = " + puzzle);

        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                // verify every cell has the right coordinates
                CellCoordinates coordinates = CellCoordinates.of(row, col);
                Cell cell = puzzle.getCell(coordinates);
                assertThat(cell.getCoordinates().row).isEqualTo(row);
                assertThat(cell.getCoordinates().col).isEqualTo(col);
                // verify cell has proper position in its row and column groups
                assertThat(puzzle.getRowForCell(coordinates).getCell(col)).isSameAs(cell);
                assertThat(puzzle.getColumnForCell(coordinates).getCell(row)).isSameAs(cell);
                // assert that cell is somewhere in its group
                assertThat(StreamSupport.stream(puzzle.getBlockForCell(coordinates).spliterator(), false))
                        .contains(cell);
            }
        }
    }

    @Test
    void canSolve_veryEasy() {
        String grid = "" +
                "9 73    6" +
                "  3  8 1 " +
                "85  2 4  " +
                " 2      5" +
                "  4   2  " +
                "6      8 " +
                "  2 7  43" +
                " 1 5  7  " +
                "3    29 8";

        SudokuPuzzle puzzle = SudokuPuzzle.create(grid);
        Sudoku.solve(puzzle);
        assertThat(puzzle.isSolved()).isTrue();
        System.out.println(puzzle);
    }

    @Test
    void canSolve_easy() {
        String grid = "" +
                "4   58 17" +
                "1 76    3" +
                "     39  " +
                "  49   7 " +
                " 8  6  5 " +
                " 2   43  " +
                "  15     " +
                "5    71 2" +
                "37 48   9";
        SudokuPuzzle puzzle = SudokuPuzzle.create(grid);
        Sudoku.solve(puzzle);
        assertThat(puzzle.isSolved()).isTrue();
        System.out.println(puzzle);
    }

    @Test
    void canSolve_medium() {
        String grid = "" +
                "  345    " +
                " 2   6  3" +
                "  1   7 2" +
                "      8 1" +
                " 8  2  9 " +
                "9 7      " +
                "1 6   9  " +
                "2  5   1 " +
                "    432  ";
        SudokuPuzzle puzzle = SudokuPuzzle.create(grid);
        Sudoku.solve(puzzle);
        assertThat(puzzle.isSolved()).isTrue();
        System.out.println(puzzle);
    }

    @Test
    void canSolve_hard() {
        int[][] grid = {
                {1, 0, 0, 5, 0, 0, 0, 8, 0},
                {7, 0, 0, 0, 0, 6, 2, 0, 0},
                {0, 9, 0, 0, 7, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 2, 0, 3, 0},
                {0, 0, 1, 0, 3, 0, 5, 0, 0},
                {0, 6, 0, 4, 0, 0, 0, 0, 0},
                {9, 0, 0, 0, 4, 0, 0, 7, 0},
                {0, 0, 6, 7, 0, 0, 0, 0, 9},
                {0, 4, 0, 0, 0, 3, 0, 0, 2},
        };
        SudokuPuzzle puzzle = SudokuPuzzle.create(grid);
        Sudoku.solve(puzzle);
        assertThat(puzzle.isSolved()).isFalse();    // TODO - figure out next solver needed for hard puzzles
        System.out.println(puzzle);
    }

    private static char[][] blankCharGrid(int row, int col) {
        char[][] array = new char[row][col];
        for (int x = 0; x < row; ++x) {
            Arrays.fill(array[x], '.');
        }
        return array;
    }

    private static int[][] blankIntGrid(int row, int col) {
        int[][] array = new int[row][col];
        for (int x = 0; x < row; ++x) {
            Arrays.fill(array[x], 0);
        }
        return array;
    }
}