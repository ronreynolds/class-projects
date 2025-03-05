package com.ronreynolds.games.sudoku;

import java.util.Objects;

/**
 * this class represents the coordinates of a cell in a 9x9 sudoku puzzle
 */
public class CellCoordinates implements Comparable<CellCoordinates> {
    public static int assertValidCoordinate(int value, String field) {
        if (value < 0 || value >= Sudoku.dimension) {
            throw new IllegalArgumentException(
                    String.format("%d for %s must be between 0 and %d", value, field, Sudoku.dimension - 1));
        }
        return value;
    }

    public static CellCoordinates of(int row, int column) {
        return new CellCoordinates(row, column);
    }

    // these are public ONLY because they are final (and thus can't be changed)
    public final int row;   // 0-based
    public final int col;   // 0-based

    private CellCoordinates(int row, int col) {
        this.row = assertValidCoordinate(row, "row");
        this.col = assertValidCoordinate(col, "column");
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
