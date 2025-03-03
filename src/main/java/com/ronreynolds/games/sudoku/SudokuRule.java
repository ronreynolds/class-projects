package com.ronreynolds.games.sudoku;

import java.util.HashSet;
import java.util.Set;

public enum SudokuRule {
    ONE_VALUE_PER_ROW {
        @Override
        public boolean isValid(SudokuPuzzle puzzle) {
            for (Cell[] row : puzzle.getRows()) {
                if (hasDuplicateValues(row)) {
                    return false;
                }
            }
            return true;
        }
    },
    ONE_VALUE_PER_COLUMN {
        @Override
        public boolean isValid(SudokuPuzzle puzzle) {
            for (int x = 0; x < Sudoku.dimension; ++x) {
                Cell[] row = puzzle.getCol(x);
                if (hasDuplicateValues(row)) {
                    return false;
                }
            }
            return true;
        }
    },
    ONE_VALUE_PER_BLOCK {
        @Override
        public boolean isValid(SudokuPuzzle puzzle) {
            for (int x = 0; x < Sudoku.dimension; ++x) {
                Cell[] row = puzzle.getBlockForCell(x);
                if (hasDuplicateValues(row)) {
                    return false;
                }
            }
            return true;
        }
    };

    // values creates a new array on every call; this incurs that cost only once
    private static final SudokuRule[] daRules = values();

    public static SudokuRule[] getRules() {
        return daRules;
    }

    public abstract boolean isValid(SudokuPuzzle sudokuPuzzle);

    /** common method to detect if a group of cells has a single value more than once */
    private static boolean hasDuplicateValues(Cell[] cells) {
        Set<Integer> values = newValuesSet();
        for (Cell cell : cells) {
            if (cell.hasValue()) {
                Integer value = cell.getValue();
                // check that we haven't seen this value before
                if (values.contains(value)) {
                    values.remove(value);
                } else {
                    return true;   // duplicate value found in row
                }
            }
        }
        return false;
    }

    private static Set<Integer> newValuesSet() {
        Set<Integer> values = new HashSet<>();
        for (int val = 1; val <= Sudoku.dimension; ++val) {
            values.add(val);
        }
        return values;
    }
}
