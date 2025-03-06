package com.ronreynolds.games.sudoku;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * rules for finding cell values in a sudoku puzzle
 */
@Slf4j
public enum SudokuSolver {
    SIMPLE_SINGLES { // find all cells with only 1 possible value and set their value to that
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
    HIDDEN_SINGLES {    // find all cells where only they have a certain value as possible
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
                if (!foundValue) {
                    log.debug("unable to find hidden singles in puzzle:\n{} posToCellMap:\n{}", puzzle, possiblesToCellsMap);
                }
            }

            return foundValue;
        }
    },
    // https://hodoku.sourceforge.net/en/tech_intersections.php (type 1)
    LOCKED_LINES {  // find cells within a block that contain certain possible values that line up (row or column)
        @Override
        public boolean apply(SudokuPuzzle puzzle) {
            boolean foundValue = false;
            // this approach only deals with blocks (but cares about the row/column of the cells)
            for (CellGroup block : puzzle.getBlocks()) {
                // for each possible value find the row/column of the cells;
                // if all cells are from the same row or column we can remove that value other cells' possibles in that row/column
            }
            return foundValue;
        }
    },
    // https://hodoku.sourceforge.net/en/tech_intersections.php (type 2)
    LOCKED_BLOCKS {  // find cells within a row or column that contain certain possible values that are all in 1 block
        @Override
        public boolean apply(SudokuPuzzle puzzle) {
            boolean foundValue = false;
            // this approach deals with rows and columns but cares about the blocks of the cells
            for (CellGroup row : puzzle.getRows()) {
                // for each possible value find the blocks of the cells;
                // if all cells are from the same block we can remove that value other cells' possibles in block
            }
            for (CellGroup column : puzzle.getColumns()) {
                // for each possible value find the blocks of the cells;
                // if all cells are from the same block we can remove that value other cells' possibles in block
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
     *
     * @param puzzle the puzzle to be checked and modified with found values
     * @return if any values were found (i.e., did this rule make progress to solving the puzzle?)
     */
    public abstract boolean apply(SudokuPuzzle puzzle);
}
