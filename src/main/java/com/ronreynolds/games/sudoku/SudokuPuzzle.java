package com.ronreynolds.games.sudoku;

import com.ronreynolds.games.util.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ronreynolds.games.sudoku.Sudoku.blockSize;
import static com.ronreynolds.games.sudoku.Sudoku.dimension;

/**
 * represents the state of the board; hard-coded for 9x9 grids for simplicity
 */
@Slf4j
public class SudokuPuzzle {
    private final CellGroup[] rows = new CellGroup[dimension];                  // 9 groups for the 9 rows
    private final CellGroup[] columns = new CellGroup[dimension];               // 9 groups for the 9 columns
    private final CellGroup[][] blocks = new CellGroup[blockSize][blockSize];   // 3x3 grid of groups

    /**
     * create a grid of dimension x dimension cells and an array of blocks of sqrt(dimension) x sqrt(dimension)
     */
    public SudokuPuzzle() {
        // populate our arrays first
        for (int x = 0; x < dimension; ++x) {
            rows[x] = new CellGroup();
            columns[x] = new CellGroup();
        }
        for (int x = 0; x < blockSize; ++x) {
            for (int y = 0; y < blockSize; ++y) {
                blocks[x][y] = new CellGroup();
            }
        }

        // populate the rows, columns, and blocks with Cells
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                Cell cell = new Cell(row, col);

                // find the right groups for this cell
                int blockRow = row / blockSize;
                int blockCol = col / blockSize;
                CellGroup rowGroup = rows[row];
                CellGroup columnGroup = columns[col];
                CellGroup blockGroup = blocks[blockRow][blockCol];

                if (!rowGroup.setCell(col, cell)) { // this should never return false
                    throw new IllegalStateException(String.format(
                            "LOGIC ERROR - there's already a cell in rowGroup %d at column %d", row, col));
                }

                if (!columnGroup.setCell(row, cell)) { // this should never return false
                    throw new IllegalStateException(String.format(
                            "LOGIC ERROR - there's already a cell in columnGroup %d at row %d", col, row));
                }

                // the cell position in the block doesn't actually matter
                if (!blockGroup.addCell(cell)) { // this should never return false
                    throw new IllegalStateException(String.format(
                            "LOGIC ERROR - failed to add cell %s to already full group at (%d, %d)", cell,
                            blockRow, blockCol));
                }
            }
        }
    }

    public static SudokuPuzzle create(char[][] knownCells) {
        if (knownCells.length != dimension) {
            throw new IllegalArgumentException("knownCells has wrong number of rows - " + knownCells.length);
        }

        final char UNKNOWN = '.';
        SudokuPuzzle puzzle = new SudokuPuzzle();
        for (int row = 0; row < dimension; ++row) {
            char[] rowOfCells = knownCells[row];
            if (rowOfCells.length != dimension) {
                throw new IllegalArgumentException(
                        String.format("row[%d] has wrong number of values - %d", row, rowOfCells.length));
            }
            for (int col = 0; col < dimension; ++col) {
                char value = rowOfCells[col];
                if (value != UNKNOWN) {
                    if (value < '1' || value > '9') {
                        throw new IllegalArgumentException(
                                String.format("invalid value (%c) at (%d, %d)", value, row, col));
                    }
                    puzzle.getCell(row, col).setValue(value - '0'); // simple way to convert a digit char to int
                }
            }
        }
        return puzzle;
    }

    // before i read the assignment it made more sense (to me) to create the puzzle from ints rather than chars
    public static SudokuPuzzle create(int[][] knownCells) {
        if (knownCells.length != dimension) {
            throw new IllegalArgumentException("knownCells has wrong number of rows - " + knownCells.length);
        }

        final int UNKNOWN = 0;
        SudokuPuzzle puzzle = new SudokuPuzzle();
        for (int row = 0; row < dimension; ++row) {
            int[] rowOfCells = knownCells[row];
            if (rowOfCells.length != dimension) {
                throw new IllegalArgumentException(
                        String.format("row[%d] has wrong number of values - %d", row, rowOfCells.length));
            }
            for (int col = 0; col < dimension; ++col) {
                int value = rowOfCells[col];
                if (value != UNKNOWN) {
                    if (value < 1 || value > dimension) {
                        throw new IllegalArgumentException(
                                String.format("invalid value (%c) at (%d, %d)", value, row, col));
                    }
                    puzzle.getCell(row, col).setValue(value);
                }
            }
        }
        return puzzle;
    }

    /**
     * if the value isn't between 1 and the max dimension (inclusive) throw an IllegalArgumentException
     */
    public static int validateRowOrColumn(int value, String field) {
        if (value < 0 || value >= dimension) {
            throw new IllegalArgumentException(field + " must be between 0 and " + dimension + "  exclusive");
        }
        return value;
    }

    /**
     * get the block of Cells that includes the cell at the specified coordinates
     *
     * @param coordinates the coordinates of the cell for which we want the block
     * @return the requested block of Cells
     */
    public CellGroup getBlockForCell(CellCoordinates coordinates) {
        int blockRow = coordinates.row / blockSize;
        int blockCol = coordinates.col / blockSize;
        return blocks[blockRow][blockCol];
    }

    public Cell getCell(int row, int col) {
        return getRow(row).getCell(col);
    }

    public CellGroup getRow(int row) {
        return rows[validateRowOrColumn(row, "row")];
    }

    public CellGroup getColumn(int col) {
        return columns[validateRowOrColumn(col, "col")];
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString())
                .append("\nrows:").append(Arrays.toString(rows))
                .append("\ncolumns:").append(Arrays.toString(columns))
                .append("\nblocks:");
        for (int row = 0; row < blockSize; ++row) {
            buf.append(Arrays.toString(blocks[row])).append('\n');
        }
        return buf.toString();
    }

    public boolean isValid() {
        for (SudokuRule rule : SudokuRule.getRules()) {
            if (!rule.isValid(this)) {
                log.warn("invalid puzzle according to rule {}", rule);
                return false;
            }
        }
        return true;
    }

    // only called for rule analysis (so rarely); otherwise would try to optimize this for perf
    public CellGroup[] getRows() {
        return rows;
    }

    public CellGroup[] getColumns() {
        return columns;
    }

    public CellGroup[][] getBlocks() {
        return blocks;
    }
}
