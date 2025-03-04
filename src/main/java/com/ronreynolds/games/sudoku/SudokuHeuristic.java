package com.ronreynolds.games.sudoku;

/**
 * rules for finding cell values in a sudoku puzzle
 */
public enum SudokuHeuristic {
    NAKED_SINGLES {
        @Override
        public boolean findValues(SudokuPuzzle puzzle) {
            // TODO the really tough parts...
            return false;
        }
    };

    /**
     * given a puzzle apply the rule to it to see if it can find any new cell values
     * @param puzzle the puzzle to be checked and modified with found values
     * @return if any values were found (i.e., did this rule make progress to solving the puzzle?)
     */
    public abstract boolean findValues(SudokuPuzzle puzzle);
}
