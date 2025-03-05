package com.ronreynolds.games.sudoku;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * represents the value (if known) and possible values of a single cell in a sudoku puzzle
 */
@Getter
public class Cell {
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
        this.value = value;
        possibleValues.clear(); // once we set the value there are no more possibles
    }

    public void addPossibleValue(int value) {
        possibleValues.add(value);
    }

    public boolean hasPossibleValue(int value) {
        return possibleValues.contains(value);
    }

    public void removePossibleValue(int value) {
        possibleValues.remove(value);
    }

    @Override
    public String toString() {
        return super.toString() + "coord:" + coordinates + " value:" + value + " possibles:" + possibleValues;
    }

    public boolean hasValue() {
        return value != null;
    }
}
