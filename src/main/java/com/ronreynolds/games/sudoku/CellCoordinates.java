package com.ronreynolds.games.sudoku;

import java.util.Objects;

/**
 * this class represents the coordinates of a cell in a 9x9 sudoku puzzle
 */
public class CellCoordinates implements Comparable<CellCoordinates> {
    public final int row;   // 0-based
    public final int col;   // 0-based

    public CellCoordinates(int row, int col) {
        this.row = SudokuPuzzle.validateRowOrColumn(row, "row");
        this.col = SudokuPuzzle.validateRowOrColumn(col, "column");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CellCoordinates that = (CellCoordinates) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public int compareTo(CellCoordinates o) {
        int diff = this.row - o.row;
        if (diff == 0) {
            diff = this.col - o.col;
        }
        return diff;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
