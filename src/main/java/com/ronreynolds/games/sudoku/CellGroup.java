package com.ronreynolds.games.sudoku;

import java.util.*;
import java.util.function.Consumer;

/**
 * a collection of 9 cells; whether they're in a row, column, or block depends on how they're referenced
 */
public class CellGroup implements Iterable<Cell> {
    // the cells of this group (row, column, or block)
    private final Cell[] cells = new Cell[Sudoku.dimension];
    // the possible values of this group (union of possible values of all group cells)
    private final Set<Integer> possibleValues = Sudoku.newAllValuesSet();

    /**
     * adds a cell in the next unoccupied spot (useful for blocks where order doesn't matter)
     *
     * @param cell the cell to add
     * @throws IllegalStateException if this cell group is full
     */
    public void addCell(Cell cell) {
        // find the first empty spot; as this is a rare operation performance is less important
        for (int x = 0; x < cells.length; ++x) {
            if (cells[x] == null) {
                cells[x] = cell;
                updatePossibleValues(cell);
                return;
            }
        }
        throw new IllegalStateException("no space for this cell was found in this group");
    }

    /**
     * set the value of the 'x'th cell to this value
     *
     * @param x    the row, column, or member index in a block
     * @param cell the cell to store
     * @throws IllegalStateException if this method attempts to overwrite an existing cell
     */
    public void setCell(int x, Cell cell) {
        if (cells[CellCoordinates.assertValidCoordinate(x, "x")] != null) {
            throw new IllegalStateException("attempt to overwrite cell at " + x);
        }
        cells[x] = cell;
        updatePossibleValues(cell);
    }

    /**
     * return the 'x'th member of the group (what x means depends on how this group is referenced)
     */
    public Cell getCell(int x) {
        return cells[CellCoordinates.assertValidCoordinate(x, "x")];
    }

    /**
     * return the set of possible values for this group
     */
    public Set<Integer> getPossibleValues() {
        return Collections.unmodifiableSet(possibleValues);
    }

    public void removePossibleValue(Integer value) {
        possibleValues.remove(value);
        for (Cell cell : cells) {
            cell.removePossibleValue(value);
        }
    }

    private void updatePossibleValues(Cell cell) {
        if (cell.hasValue()) {
            removePossibleValue(cell.getValue());
        }
    }

    @Override
    public String toString() {
        return super.toString() + "\n\tcells:" + Arrays.toString(cells) + "\n\tpossibles:" + possibleValues;
    }

    /**
     * easy way to iterate the cells of a group in-order
     */
    @Override
    public Iterator<Cell> iterator() {
        return Arrays.asList(cells).iterator();
    }

    /**
     * invoke the action on each cell in this group
     */
    @Override
    public void forEach(Consumer<? super Cell> action) {
        Arrays.asList(cells).forEach(action);
    }

    /**
     * used for Streams
     */
    @Override
    public Spliterator<Cell> spliterator() {
        return Arrays.asList(cells).spliterator();
    }

    /**
     * create and return a map containing an entry for each possible value and the cells that have that possible value;
     * if we wanted to boost performance maintaining this map as a field would be useful but a little complicated
     */
    public Map<Integer, List<Cell>> getPossibleToCellMap() {
        // if this group has no possible values return an empty map
        if (possibleValues.isEmpty()) {
            return Map.of();
        }
        Map<Integer, List<Cell>> possibleValuesMap = new HashMap<>();
        for (Integer possibleValue : possibleValues) {
            possibleValuesMap.put(possibleValue, getCellsWithPossibleValue(possibleValue));
        }
        return possibleValuesMap;
    }

    private List<Cell> getCellsWithPossibleValue(Integer possibleValue) {
        List<Cell> cellsWithPossibleValue = new ArrayList<>();
        for (Cell cell : cells) {
            if (cell.hasPossibleValue(possibleValue)) {
                cellsWithPossibleValue.add(cell);
            }
        }
        return cellsWithPossibleValue;
    }
}
