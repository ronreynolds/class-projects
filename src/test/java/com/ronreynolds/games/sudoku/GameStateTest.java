package com.ronreynolds.games.sudoku;


import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;

class GameStateTest {
    @Test
    void validateStateGridsAndRows() {
        GameState state = GameState.createState(new int[][]{
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
                Cell cell = state.getCell(row, col);
                CellCoordinates coords = cell.getCoordinates();
                assertThat(coords.row).isEqualTo(row);
                assertThat(coords.col).isEqualTo(col);
            }
        }

        Cell[][] block = state.getBlockForCell(new CellCoordinates(1, 2));  // should be the {678},{912},{345} block
        assertThat(block[0][0]).isSameAs(state.getCell(3, 6));
        assertThat(block[2][2]).isSameAs(state.getCell(5, 8));

        Cell[] column = state.getCol(4);
        for (int row = 0; row < column.length; ++row) {
            assertThat(column[row]).isSameAs(state.getCell(row, 4));
        }
    }
}