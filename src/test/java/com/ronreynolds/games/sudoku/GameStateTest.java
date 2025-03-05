package com.ronreynolds.games.sudoku;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameStateTest {
    @Test
    void validateStateGridsAndRows() {
        SudokuPuzzle state = SudokuPuzzle.create(new int[][]{
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

        System.out.println("state = " + state);

        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                CellCoordinates coordinates = CellCoordinates.of(row, col);
                Cell cell = state.getCell(coordinates);
                assertThat(cell.getCoordinates().row).isEqualTo(row);
                assertThat(cell.getCoordinates().col).isEqualTo(col);
            }
        }

        CellGroup block = state.getBlockForCell(CellCoordinates.of(1, 2));  // should be the {678},{912},{345} block
        assertThat(block.getCell(0)).isSameAs(state.getCell(CellCoordinates.of(3, 6)));
        assertThat(block.getCell(9)).isSameAs(state.getCell(CellCoordinates.of(5, 8)));
    }
}