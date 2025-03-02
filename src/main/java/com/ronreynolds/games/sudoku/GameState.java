package com.ronreynolds.games.sudoku;

import java.util.Arrays;

/**
 * represents the state of the board; hard-coded for 9x9 grids for simplicity
 */
public class GameState {
    public static final int dimension = 9;
    public static final int blockSize = (int) Math.sqrt(dimension);

    private Cell[][] cells;         // 9x9 grid of cells
    private Cell[][] cols;          // 1x9 columns of cells (rows are easy to get)
    private Cell[][][][] blocks;    // 2D array of 2D arrays of Cells (for much faster lookup of blocks by cell-coordinates

    /**
     * create a grid of dimension x dimension cells and an array of blocks of sqrt(dimension) x sqrt(dimension)
     */
    public GameState() {
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

    public static GameState createState(int[][] knownCells) {
        if (knownCells.length != dimension || knownCells[0].length != dimension) {
            throw new IllegalArgumentException("knownCells is not of size " + dimension + " x " + dimension);
        }

        GameState gameState = new GameState();
        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                if (knownCells[row][col] > 0) {
                    gameState.cells[row][col].setValue(knownCells[row][col]);
                }
            }
        }
        return gameState;
    }

    private static <T> String toString2DArray(T[][] array) {
        StringBuilder buf = new StringBuilder();
        for (T[] row : array) {
            buf.append("\n" + Arrays.toString(row));
        }
        return buf.toString();
    }

    /**
     * if the value isn't between 1 and the max dimension (inclusive) throw an IllegalArgumentException
     */
    public static int validateRowOrColumn(int value, String field) {
        if (value < 0 || value >= GameState.dimension) {
            throw new IllegalArgumentException(field + " must be between 0 and " + GameState.dimension + "  exclusive");
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
        buf.append("\ncells:{").append(toString2DArray(cells)).append("}\n");
        buf.append("\ncols:{").append(toString2DArray(cols)).append("}\n");
        buf.append("blocks:{\n");
        for (int row = 0; row < blockSize; ++row) {
            for (int col = 0; col < blockSize; ++col) {
                buf.append(toString2DArray(blocks[row][col])).append("\n");
            }
        }
        buf.append("}\n");
        return buf.toString();
    }

}
