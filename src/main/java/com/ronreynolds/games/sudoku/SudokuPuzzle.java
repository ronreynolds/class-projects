package com.ronreynolds.games.sudoku;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private final List<CellGroup> groupList;

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
        // used by solvers that need to iterate all the groups regardless of what kind they are
        groupList = new ArrayList<>(rows.length + columns.length + blocks.length * 2);
        Collections.addAll(groupList, rows);
        Collections.addAll(groupList, columns);
        for (CellGroup[] blockRow : blocks) {
            Collections.addAll(groupList, blockRow);
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
                    int intValue = value - '0'; // simple way to convert a digit char to int
                    puzzle.setCellValue(CellCoordinates.of(row, col), intValue);
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
                    puzzle.setCellValue(CellCoordinates.of(row, col), value);
                }
            }
        }
        return puzzle;
    }


    public CellGroup[] getRows() {
        return rows;
    }

    public CellGroup[] getColumns() {
        return columns;
    }

    public CellGroup[][] getBlocks() {
        return blocks;
    }

    /**
     * convenience method for solvers that walk all rows, columns, and blocks
     */
    public List<CellGroup> getCellGroupList() {
        return Collections.unmodifiableList(groupList);
    }

    /**
     * this method, when it sets the value of a cell, also removes that value from the possible values of that cell's
     * row, column, and block
     */
    public void setCellValue(CellCoordinates coordinates, int value) {
        getCell(coordinates).setValue(value);
        getRowForCell(coordinates).forEach(c -> c.removePossibleValue(value));
        getColumnForCell(coordinates).forEach(c -> c.removePossibleValue(value));
        getBlockForCell(coordinates).forEach(c -> c.removePossibleValue(value));
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

    /**
     * if we use CellCoordinates instead of row/column ints we don't have to worry about validating the values
     */
    public Cell getCell(CellCoordinates coordinates) {
        return getRowForCell(coordinates).getCell(coordinates.col);
    }

    public CellGroup getRowForCell(CellCoordinates coordinates) {
        return rows[coordinates.row];
    }

    public CellGroup getColumnForCell(CellCoordinates coordinates) {
        return columns[coordinates.col];
    }

    public List<Cell> getCellList() {
        List<Cell> cellList = new ArrayList<>(dimension * dimension);
        for (CellGroup row : getRows()) {
            for (Cell cell : row) {
                cellList.add(cell);
            }
        }
        return cellList;
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

    /**
     * return true if all cells contain valid values (i.e., no value violates a SudokuRule)
     */
    public boolean isValid() {
        for (SudokuRule rule : SudokuRule.getRules()) {
            if (!rule.isValid(this)) {
                log.warn("invalid puzzle according to rule {}", rule);
                return false;
            }
        }
        return true;
    }

    /**
     * return true only if all cells have values; false otherwise
     */
    public boolean isSolved() {
        for (CellGroup row : getRows()) {
            for (Cell cell : row) {
                if (!cell.hasValue()) {
                    return false;
                }
            }
        }
        return true;
    }
}
