package com.ronreynolds.games.sudoku;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * rules for finding cell values in a sudoku puzzle
 */
public enum SudokuHeuristic {
    NAKED_SINGLES {
        @Override
        public boolean findValues(SudokuPuzzle puzzle) {
            boolean foundValue = false;
            for (Cell cell : puzzle.getCellList()) {
                // we only care about cells that don't yet have a value
                if (!cell.hasValue()) {
                    Set<Integer> possibles = cell.getPossibleValues();
                    // if there's only 1 possible value set that as the value for the cell
                    if (possibles.size() == 1) {
                        puzzle.setCellValue(cell.getCoordinates(), possibles.iterator().next());
                        foundValue = true;
                    }
                }
            }
            return foundValue;
        }
    };

    static final SudokuHeuristic[] heuristics = values();

    public static SudokuHeuristic[] getHeuristics() {
        return heuristics;
    }

    /**
     * given a puzzle apply the rule to it to see if it can find any new cell values
     * @param puzzle the puzzle to be checked and modified with found values
     * @return if any values were found (i.e., did this rule make progress to solving the puzzle?)
     */
    public abstract boolean findValues(SudokuPuzzle puzzle);
}
