package com.ronreynolds.games.sudoku;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * represents the value (if known) and possible values of a single cell in a sudoku puzzle
 */
public class Cell implements Comparable<Cell> {
    private final CellCoordinates coordinates;
    private final Set<Integer> possibleValues = Sudoku.newAllValuesSet();
    private Integer value;

    Cell(int row, int col) {
        this(CellCoordinates.of(row, col));
    }

    Cell(@NonNull CellCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setValue(int value) {
        this.value = assertValidValue(value);
        possibleValues.clear(); // once we set the value there are no more possibles
    }

    public void setPossibleValues(Set<Integer> values) {
        possibleValues.clear();
        possibleValues.addAll(values);
    }

    public boolean hasPossibleValue(Integer possibleValue) {
        return possibleValues.contains(possibleValue);
    }

    /**
     * remove a possible value from this Cell's possibleValues set
     * @param value the value to remove from the set of possibles
     * @return if the value was removed (i.e., if it was in the set before and it's not now)
     */
    public boolean removePossibleValue(Integer value) {
        return possibleValues.remove(value);
    }

    public CellCoordinates getCoordinates() {
        return coordinates;
    }

    public Set<Integer> getPossibleValues() {
        // in general it's a bad idea to expose references to mutable internal data
        return Collections.unmodifiableSet(possibleValues);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return coordinates + " value:" + value + " possibles:" + possibleValues;
    }

    public boolean hasValue() {
        return value != null;
    }

    public int getPossibleCount() {
        return possibleValues.size();
    }

    @Override
    public int compareTo(Cell o) {
        // cells are completely compared by coordinates
        return this.coordinates.compareTo(o.coordinates);
    }

    public static int assertValidValue(int val) {
        if (val < 1 || val > Sudoku.dimension) {
            throw new IllegalArgumentException(
                    String.format("value %d is outside the allowed range (1,%d)", val, Sudoku.dimension));
        }
        return val;
    }
}
