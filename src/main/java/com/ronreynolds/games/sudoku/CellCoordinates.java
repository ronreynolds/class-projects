package com.ronreynolds.games.sudoku;

import java.util.Objects;

import static com.ronreynolds.games.sudoku.Sudoku.dimension;

/**
 * this class represents the coordinates of a cell in a 9x9 sudoku puzzle
 */
public class CellCoordinates implements Comparable<CellCoordinates> {
    public final int row;   // 0-based
    public final int col;   // 0-based

    public static CellCoordinates of(int row, int column) {
        return new CellCoordinates(row, column);
    }

    private CellCoordinates(int row, int col) {
        this.row = validateRowOrColumn(row, "row");
        this.col = validateRowOrColumn(col, "column");
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

    private static int validateRowOrColumn(int value, String field) {
        if (value < 0 || value >= dimension) {
            throw new IllegalArgumentException(
                    String.format("%d for %s must be between 0 and %d", value, field, dimension - 1));
        }
        return value;
    }
}
