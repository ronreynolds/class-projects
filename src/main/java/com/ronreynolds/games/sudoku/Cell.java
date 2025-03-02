package com.ronreynolds.games.sudoku;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class Cell {
    @Getter
    private final CellCoordinates coordinates;
    @Getter
    private Integer value;
    @Getter
    private Set<Integer> possibleValues = new HashSet<>();

    Cell(int row, int col) {
        this(new CellCoordinates(row, col));
    }

    Cell(CellCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setValue(Integer value) {
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
}
