package com.ronreynolds.games.sudoku;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SudokuTest {
    @Test
    void isValidIsSolved_works() {
        SudokuPuzzle sudokuPuzzle;
        sudokuPuzzle = SudokuPuzzle.create(new int[][]{
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {4, 5, 6, 7, 8, 9, 1, 2, 3},
                {7, 8, 9, 1, 2, 3, 4, 5, 6},
                {2, 3, 4, 5, 6, 7, 8, 9, 1},
                {5, 6, 7, 8, 9, 1, 2, 3, 4},
                {8, 9, 1, 2, 3, 4, 5, 6, 7},
                {3, 4, 5, 6, 7, 8, 9, 1, 2},
                {6, 7, 8, 9, 1, 2, 3, 4, 5},
                {9, 1, 2, 3, 4, 5, 6, 7, 8},
        });
        assertThat(sudokuPuzzle.isValid()).isTrue();
        System.out.println("can detect a solved puzzle");

        assertThat(sudokuPuzzle.isSolved()).isTrue();
        System.out.println("can detect that a puzzle isn't solved");

        assertThat(SudokuPuzzle.create(new int[][]{
                {1, 1, 3, 4, 5, 6, 7, 8, 9},
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
    }

    @Test
    void solve_detectsNotSolved() {
        SudokuPuzzle sudokuPuzzle = SudokuPuzzle.create(new int[][]{
                {0, 2, 3, 4, 5, 6, 7, 8, 9},
                {0, 5, 6, 7, 8, 9, 1, 2, 3},
                {0, 8, 9, 1, 2, 3, 4, 5, 6},
                {0, 3, 4, 5, 6, 7, 8, 9, 1},
                {0, 6, 7, 8, 9, 1, 2, 3, 4},
                {0, 9, 1, 2, 3, 4, 5, 6, 7},
                {0, 4, 5, 6, 7, 8, 9, 1, 2},
                {0, 7, 8, 9, 1, 2, 3, 4, 5},
                {0, 1, 2, 3, 4, 5, 6, 7, 8},
        });
        assertThat(sudokuPuzzle.isSolved()).isFalse();
        System.out.println("can detect that a puzzle isn't solved");

        sudokuPuzzle = Sudoku.solve(sudokuPuzzle);
        assertThat(sudokuPuzzle.isSolved()).isTrue();
        System.out.println("can solve a simple puzzle");
    }
}