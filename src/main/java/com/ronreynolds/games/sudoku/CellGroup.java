package com.ronreynolds.games.sudoku;

import java.util.*;
import java.util.function.Consumer;

/**
 * a collection of 9 cells; whether they're in a row, column, or block depends on how they're referenced
 */
public class CellGroup implements Iterable<Cell> {
    private final Cell[] cells = new Cell[Sudoku.dimension];
    private final Set<Integer> possibleValues = new HashSet<>();

    public boolean addCell(Cell cell) {
        boolean cellAdded = false;
        // find the first empty spot; as this is a rare operation performance is less important
        for (int x = 0; x < cells.length; ++x) {
            if (cells[x] == null) {
                cells[x] = cell;
                cellAdded = true;
                break;
            }
        }
        return cellAdded;
    }
    public boolean setCell(int x, Cell cell) {
        validateRange(x);
        boolean cellWasEmpty = cells[x] == null;
        cells[x] = cell;
        return cellWasEmpty;
    }

    public Cell getCell(int x) {
        return cells[validateRange(x)];
    }

    public Set<Integer> getPossibleValues() {
        return possibleValues;
    }


    @Override
    public Iterator<Cell> iterator() {
        return Arrays.asList(cells).iterator();
    }

    @Override
    public void forEach(Consumer<? super Cell> action) {
        Arrays.asList(cells).forEach(action);
    }

    @Override
    public Spliterator<Cell> spliterator() {
        return Arrays.asList(cells).spliterator();
    }

    private static int validateRange(int x) {
        if (x < 0 || x >= Sudoku.dimension) {
            throw new IllegalArgumentException("x is outside the allowed range");
        }
        return x;
    }

}
