package com.ronreynolds.games.sudoku;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * rules for finding cell values in a sudoku puzzle
 */
public enum SudokuSolver {
    NAKED_SINGLES { // find all cells with only 1 possible value and set their value to that
        @Override
        public boolean apply(SudokuPuzzle puzzle) {
            boolean foundValue = false;
            // iterate all cells in puzzle
            for (Cell cell : puzzle.getCellList()) {
                // we only care about cells that don't yet have a value and only have 1 possible
                if (!cell.hasValue() && cell.getPossibleCount() == 1) {
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
    },
    HIDDEN_SINGLES {
        @Override
        public boolean apply(SudokuPuzzle puzzle) {
            boolean foundValue = false;
            // iterate all cell groups in puzzle (rows, columns, and blocks)
            for (CellGroup group : puzzle.getCellGroupList()) {
                // only need to check possible values in group
                Map<Integer, List<Cell>> possiblesToCellsMap = group.getPossibleToCellMap();
                for (Map.Entry<Integer, List<Cell>> entry : possiblesToCellsMap.entrySet()) {
                    // if there is only 1 cell with this possible value that's a hidden single
                    if (entry.getValue().size() == 1) {
                        puzzle.setCellValue(entry.getValue().get(0).getCoordinates(), entry.getKey());
                        foundValue = true;
                    }
                }
            }

            return foundValue;
        }
    }
    // TODO - add more solvers
    ;

    // we make a copy of Enum.values() to avoid repeat creation of new arrays on every call
    static final SudokuSolver[] solvers = values();

    public static SudokuSolver[] getSolvers() {
        return solvers;
    }

    /**
     * given a puzzle apply the rule to it to see if it can find any new cell values
     * @param puzzle the puzzle to be checked and modified with found values
     * @return if any values were found (i.e., did this rule make progress to solving the puzzle?)
     */
    public abstract boolean apply(SudokuPuzzle puzzle);
}
