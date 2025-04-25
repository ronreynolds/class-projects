package com.ronreynolds.games.sudoku;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ronreynolds.games.sudoku.Sudoku.blockSize;
import static com.ronreynolds.games.sudoku.Sudoku.dimension;

/**
 * represents the state of the board
 */
@Slf4j
public class SudokuPuzzle {
    private final List<CellGroup> rows;
    private final List<CellGroup> columns;
    private final List<CellGroup> blocks;
    private final List<CellGroup> allGroups;

    /**
     * create a grid of dimension x dimension cells and an array of blocks of sqrt(dimension) x sqrt(dimension)
     */
    public SudokuPuzzle() {
        // populate our lists first (these are unmodifiable because we don't add/remove Cells; we just modify the Cells)
        rows = Stream.generate(CellGroup::new).limit(dimension).collect(Collectors.toUnmodifiableList());
        columns = Stream.generate(CellGroup::new).limit(dimension).collect(Collectors.toUnmodifiableList());
        blocks = Stream.generate(CellGroup::new).limit(dimension).collect(Collectors.toUnmodifiableList());

        // populate the rows, columns, and blocks with Cells
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                Cell cell = new Cell(row, col);

                // find the right groups for this cell
                CellGroup rowGroup = rows.get(row);
                CellGroup columnGroup = columns.get(col);
                CellGroup blockGroup = blocks.get(toBlockIndex(row, col));

                // add this cell to its groups
                rowGroup.setCell(col, cell);
                columnGroup.setCell(row, cell);
                blockGroup.addCell(cell); // the cell position in the block doesn't actually matter
            }
        }
        // used by solvers that need to iterate all the groups regardless of what kind they are
        List<CellGroup> allGroups = new ArrayList<>(3 * dimension);
        allGroups.addAll(rows);
        allGroups.addAll(columns);
        allGroups.addAll(blocks);
        this.allGroups = Collections.unmodifiableList(allGroups);
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
                    if (value < '1' || value > '9') { // only (other) place where 9 is hard-coded
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
                                String.format("invalid value (%d) at (%d, %d)", value, row, col));
                    }
                    puzzle.setCellValue(CellCoordinates.of(row, col), value);
                }
            }
        }
        return puzzle;
    }

    /**
     * this is actually the most useful and easiest to use grid notation; 81 chars, space = unknown
     */
    public static SudokuPuzzle create(String compactGrid) {
        // strip all CRLFs (if any)
        compactGrid = compactGrid.replace("\n", "");
        if (compactGrid.length() != dimension * dimension) {
            throw new IllegalArgumentException(
                    String.format("incorrect size (%d) for compactGrid of %d x %d puzzle",
                            compactGrid.length(), dimension, dimension));
        }
        int[][] grid = new int[dimension][dimension];
        char[] chars = compactGrid.toCharArray();
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                int offset = row * dimension + col;
                if (chars[offset] != ' ') {
                    grid[row][col] = chars[offset] - '0';
                } else {
                    grid[row][col] = 0;
                }
            }
        }
        return create(grid);
    }

    public List<CellGroup> getRows() {
        return rows;
    }

    public List<CellGroup> getColumns() {
        return columns;
    }

    public List<CellGroup> getBlocks() {
        return blocks;
    }

    /**
     * convenience method for solvers that walk all rows, columns, and blocks
     */
    public List<CellGroup> getAllGroups() {
        return allGroups;
    }

    /**
     * this method, when it sets the value of a cell, also removes that value from the possible values of that cell's
     * row, column, and block
     */
    public void setCellValue(CellCoordinates coordinates, int value) {
        getCell(coordinates).setValue(value);
        getRowForCell(coordinates).removePossibleValue(value);
        getColumnForCell(coordinates).removePossibleValue(value);
        getBlockForCell(coordinates).removePossibleValue(value);
    }

    /**
     * get the block of Cells that includes the cell at the specified coordinates
     *
     * @param coordinates the coordinates of the cell for which we want the block
     * @return the requested block of Cells
     */
    public CellGroup getBlockForCell(CellCoordinates coordinates) {
        return blocks.get(toBlockIndex(coordinates.row, coordinates.col));
    }

    public CellGroup getBlockForCell(Cell cell) {
        return blocks.get(toBlockIndex(cell.getCoordinates().row, cell.getCoordinates().col));
    }

    /**
     * if we use CellCoordinates instead of row/column ints we don't have to worry about validating the values
     */
    public Cell getCell(CellCoordinates coordinates) {
        return getRowForCell(coordinates).getCell(coordinates.col);
    }

    public CellGroup getRowForCell(CellCoordinates coordinates) {
        return rows.get(coordinates.row);
    }

    public CellGroup getRowForCell(Cell cell) {
        return getRowForCell(cell.getCoordinates());
    }

    public CellGroup getColumnForCell(CellCoordinates coordinates) {
        return columns.get(coordinates.col);
    }

    public CellGroup getColumnForCell(Cell cell) {
        return getColumnForCell(cell.getCoordinates());
    }

    /**
     * return a list of all the cells in this puzzle
     */
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
        Map<CellCoordinates, Set<Integer>> possibleValuesMap = new TreeMap<>();
        for (CellGroup row : getRows()) {
            for (Cell cell : row) {
                Integer val = cell.getValue();
                if (val == null) {
                    // easier to read if the values are sorted
                    possibleValuesMap.put(cell.getCoordinates(), new TreeSet<>(cell.getPossibleValues()));
                    buf.append('?');    // place-holder for unknown cell
                } else {
                    buf.append(val);
                }
                buf.append(' ');
            }
            buf.append('\n');
        }
        // if it's not solved we also want the possible values of the unsolved cells
        if (!possibleValuesMap.isEmpty()) {
            buf.append("\npossible values:\n");
            int currentRow = 0;
            for (int row = 0; row < dimension; ++row) {
                for (int col = 0; col < dimension; ++col) {
                    Set<Integer> possibleValues = possibleValuesMap.get(CellCoordinates.of(row, col));
                    buf.append('[');
                    if (possibleValues != null) {
                        buf.append(String.format("%9s", possibleValues.stream().map(String::valueOf).collect(Collectors.joining())));
                    } else {
                        buf.append("         ");    // so things line up
                    }
                    buf.append("] ");
                }
                buf.append('\n');
            }
/*
            for (var entry : possibleValuesMap.entrySet()) {    // using var to infer the type of entry
                if (entry.getKey().row != currentRow) {
                    buf.append('\n');
                    currentRow = entry.getKey().row;
                    if (currentRow % blockSize == 0) {
                        buf.append('\n');   // add a gap between groups; makes the output more readable
                    }
                }
                // pack in possible values with no space between
                buf.append(entry.getValue()));
            }
*/
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

    private static int toBlockIndex(int row, int col) {
        // we actually want the truncation of integer division here (e.g., 2/3 = 0, 5/3 = 1, 6/3 = 2)
        int blockRow = row / blockSize;
        int blockCol = col / blockSize;
        return blockRow * blockSize + blockCol;
    }
}
