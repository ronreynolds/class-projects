package com.ronreynolds.games.sudoku;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        // can be solved with naked-singles rule only
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

        Sudoku.solve(sudokuPuzzle);
        assertThat(sudokuPuzzle.isSolved()).isTrue();
        System.out.println("can solve a simple puzzle");
    }

    @Test
    void verifyCellPositions() {
        SudokuPuzzle puzzle = SudokuPuzzle.create(new int[][]{
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {4, 5, 6, 7, 8, 9, 1, 2, 3},
                {7, 8, 9, 1, 2, 3, 4, 5, 6},
                {9, 1, 2, 3, 4, 5, 6, 7, 8},
                {3, 4, 5, 6, 7, 8, 9, 1, 2},
                {6, 7, 8, 9, 1, 2, 3, 4, 5},
                {2, 3, 4, 5, 6, 7, 8, 9, 1},
                {5, 6, 7, 8, 9, 1, 2, 3, 4},
                {8, 9, 1, 2, 3, 4, 5, 6, 7},
        });

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
}