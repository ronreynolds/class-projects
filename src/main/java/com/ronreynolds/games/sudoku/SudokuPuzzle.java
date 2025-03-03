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
    private Cell[][] cells;         // 9x9 grid of cells
    private Cell[][] cols;          // 1x9 columns of cells (rows are easy to get)
    private Cell[][][][] blocks;    // 2D array of 2D arrays of Cells (for much faster lookup of blocks by cell-coordinates

    /**
     * create a grid of dimension x dimension cells and an array of blocks of sqrt(dimension) x sqrt(dimension)
     */
    public SudokuPuzzle() {
        cells = new Cell[dimension][dimension];
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                // create a Cell object for every cell in the grid
                cells[row][col] = new Cell(row, col);
            }
        }
        // group the Cells into blocks for faster lookup
        blocks = new Cell[blockSize][blockSize][][];
        for (int row = 0; row < blockSize; ++row) {
            for (int col = 0; col < blockSize; ++col) {
                Cell[][] block = new Cell[blockSize][blockSize];
                for (int x = 0; x < blockSize; ++x) {
                    for (int y = 0; y < blockSize; ++y) {
                        block[x][y] = cells[x * blockSize + x][y * blockSize + y];
                    }
                }
                blocks[row][col] = block;
            }
        }

        // create columns of Cells by column for faster lookup (rows of Cells are just cells[row]
        cols = new Cell[dimension][];
        for (int col = 0; col < dimension; ++col) {
            cols[col] = new Cell[dimension];
            for (int row = 0; row < dimension; ++row) {
                cols[col][row] = cells[col][row];
            }
        }
    }

    public static SudokuPuzzle create(char[][] knownCells) {

    }

    public static SudokuPuzzle create(int[][] knownCells) {
        if (knownCells.length != dimension || knownCells[0].length != dimension) {
            throw new IllegalArgumentException("knownCells is not of size " + dimension + " x " + dimension);
        }

        SudokuPuzzle gameState = new SudokuPuzzle();
        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                if (knownCells[row][col] > 0) {
                    gameState.cells[row][col].setValue(knownCells[row][col]);
                }
            }
        }
        return gameState;
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
    public Cell[][] getBlockForCell(CellCoordinates coordinates) {
        int blockRow = coordinates.row / blockSize;
        int blockCol = coordinates.col / blockSize;
        return blocks[blockRow][blockCol];
    }

    public Cell getCell(int row, int col) {
        return cells[validateRowOrColumn(row, "row")][validateRowOrColumn(col, "col")];
    }

    public Cell[] getRow(int row) {
        return cells[validateRowOrColumn(row, "row")];
    }

    public Cell[] getCol(int col) {
        return cols[validateRowOrColumn(col, "col")];
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("GameState{");
        buf.append("\ncells:{").append(Strings.toString2DArray(cells)).append("}\n");
        buf.append("\ncols:{").append(Strings.toString2DArray(cols)).append("}\n");
        buf.append("blocks:{\n");
        for (int row = 0; row < blockSize; ++row) {
            for (int col = 0; col < blockSize; ++col) {
                buf.append(Strings.toString2DArray(blocks[row][col])).append('\n');
            }
        }
        buf.append("}\n");
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
    public List<Cell[]> getRowList() {
        return Arrays.asList(cells);
    }

    public List<Cell[]> getColumnList() {
        return Arrays.asList(cols);
    }

    public List<Cell[]> getBlockList() {
        List<Cell[]> blockList = new ArrayList<>(dimension);
        for (int row = 0; row < blockSize; ++row) {
            for (int col = 0; col < blockList; ++col) {
                Cell[][] blockCells = blocks[row][col];
                Cell[] flattenedArray = Cell[dimension];

                blockList.add(flattenedArray);
            }
        }
        return blockList;
    }

}
