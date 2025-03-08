package com.ronreynolds.games.sudoku;

import com.ronreynolds.games.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

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
            for (CellGroup group : puzzle.getAllGroups()) {
                // only need to check possible values in group
                Map<Integer, List<Cell>> possiblesToCellsMap = group.getPossibleToCellMap();
                for (var entry : possiblesToCellsMap.entrySet()) {
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
    LOCKED_LINES {  // find cells within a block that contain certain possible values that line up (same row or column)
        @Override
        public boolean apply(SudokuPuzzle puzzle) {
            boolean changedPuzzle = false;
            // this approach only deals with blocks (but cares about the row/column of the cells)
            for (CellGroup block : puzzle.getBlocks()) {
                Map<Integer, List<Cell>> possibleValueMap = block.getPossibleToCellMap();
                for (var entry : possibleValueMap.entrySet()) {
                    // for each possible value find the row/column of the cells;
                    // if all cells are from the same row or column we can remove that value other cells' possibles in that row/column
                    List<Cell> cellsWithSamePossible = entry.getValue();
                    Optional<Integer> commonRow = findCommonRow(cellsWithSamePossible);
                    if (commonRow.isPresent()) {
                        // all cells in this block with this possible value have the same row; we can clear it from other cells in this row
                        log.info("{} has common-row {}", entry.getKey(), commonRow.get());
                        CellGroup row = puzzle.getRowForCell(cellsWithSamePossible.get(0));
                        for (Cell cell : row) {
                            // be sure to not clear the possible from this group's cells
                            if (!cellsWithSamePossible.contains(cell)) {
                                changedPuzzle |= cell.removePossibleValue(entry.getKey());
                            }
                        }
                    } else {
                        // possibly they have a column in common
                        Optional<Integer> commonCol = findCommonColumn(cellsWithSamePossible);
                        if (commonCol.isPresent()) {
                            // all cells in this block with this possible value have the same column; we can clear it from other cells in this column
                            log.info("{} has common-column {}", entry.getKey(), commonCol.get());
                            CellGroup column = puzzle.getColumnForCell(cellsWithSamePossible.get(0));
                            for (Cell cell : column) {
                                // be sure to not clear the possible from this group's cells
                                if (!cellsWithSamePossible.contains(cell)) {
                                    changedPuzzle |= cell.removePossibleValue(entry.getKey());
                                }
                            }
                        }
                    }

                }
            }
            return changedPuzzle;
        }
    },
    // https://hodoku.sourceforge.net/en/tech_intersections.php (type 2)
    LOCKED_BLOCKS {  // find cells within a row or column that contain certain possible values that are all in 1 block
        @Override
        public boolean apply(SudokuPuzzle puzzle) {
            return applyToGroup(puzzle, puzzle.getRows(), "row")
                    || applyToGroup(puzzle, puzzle.getColumns(), "column");
        }

        // because we process rows and columns the same we can use a shared method for both
        private boolean applyToGroup(SudokuPuzzle puzzle, List<CellGroup> groupList, String groupType) {
            boolean changedPuzzle = false;
            for (CellGroup row : groupList) {
                Map<Integer, List<Cell>> possibleValueMap = row.getPossibleToCellMap();
                for (var entry : possibleValueMap.entrySet()) {
                    // for each possible value find the blocks of the cells;
                    Integer possibleValue = entry.getKey();
                    List<Cell> cells = entry.getValue();
                    // if all cells are from the same block we can remove that value from other cells' possibles in block
                    Optional<CellGroup> commonBlock = findCommonBlock(puzzle, cells);
                    if (commonBlock.isPresent()) {
                        log.info("possible {} in {} of {} is common to a block",
                                possibleValue, groupType, cells.get(0).getCoordinates());
                        // remove this possible value from all OTHER cells in this block
                        for (Cell cell : commonBlock.get()) {
                            if (!cells.contains(cell)) {
                                changedPuzzle |= cell.removePossibleValue(possibleValue);
                            }
                        }
                    }
                }
            }
            return changedPuzzle;
        }
    },
    // https://hodoku.sourceforge.net/en/tech_hidden.php
    HIDDEN_PAIR {
        @Override
        public boolean apply(SudokuPuzzle puzzle) {
            boolean changedPuzzle = false;
            // find 2 cells that are the ONLY cells in a group (row, column, or block) to contain 2 possible values (<=72 combinations)
            // if 2 such cells exist remove all other possibles from those 2 cells
            for (CellGroup group : puzzle.getAllGroups()) {
                Map<Integer, List<Cell>> possibleValueMap = group.getPossibleToCellMap();
                List<Pair<Integer>> pairList = generateCombinations(possibleValueMap.keySet());
                for (Pair<Integer> pair : pairList) {
                    // we use a set to remove duplicates so each cell is counted only once
                    Set<Cell> cellsWithPossiblePair = new HashSet<>(possibleValueMap.get(pair.v1));
                    cellsWithPossiblePair.addAll(possibleValueMap.get(pair.v2));
                    if (cellsWithPossiblePair.size() == 2) {
                        // we found a hidden pair! :)  remove all other possibles from these cells
                        cellsWithPossiblePair.forEach(cell -> cell.setPossibleValues(Set.of(pair.v1, pair.v2)));
                        log.info("hidden-pair ({}) found at {}", pair, cellsWithPossiblePair);
                        changedPuzzle = true;
                    }
                }
            }
            return changedPuzzle;
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

    // static util methods for solvers
    /**
     * if this group of cells has a common row return it; otherwise empty
     *
     * @param cells the list of cells from which to check their coordinates
     * @return the common row value or empty if they have different row values
     */
    private static Optional<Integer> findCommonRow(List<Cell> cells) {
        if (cells.isEmpty()) {
            return Optional.empty();
        }
        int candidateRow = cells.get(0).getCoordinates().row;
        for (Cell cell : cells) {
            if (cell.getCoordinates().row != candidateRow) {
                return Optional.empty();    // a different row was found
            }
        }
        // all cells had this row in their coordinates
        return Optional.of(candidateRow);
    }

    /**
     * if this group of cells has a common column return it; otherwise empty
     *
     * @param cells the list of cells from which to check their coordinates
     * @return the common column value or empty if they have different column values
     */
    private static Optional<Integer> findCommonColumn(List<Cell> cells) {
        if (cells.isEmpty()) {
            return Optional.empty();
        }
        int candidateCol = cells.get(0).getCoordinates().col;
        for (Cell cell : cells) {
            if (cell.getCoordinates().col != candidateCol) {
                return Optional.empty();    // a different column was found
            }
        }
        // all cells had this column in their coordinates
        return Optional.of(candidateCol);
    }

    /**
     * if this group of cells has a common block return it; otherwise empty
     *
     * @param cells the list of cells from which to check their block
     * @return the common block or empty if they are in different blocks
     */
    private static Optional<CellGroup> findCommonBlock(SudokuPuzzle puzzle, List<Cell> cells) {
        if (cells.isEmpty()) {
            return Optional.empty();
        }

        CellGroup candidateBlock = puzzle.getBlockForCell(cells.get(0));
        for (Cell cell : cells) {
            if (puzzle.getBlockForCell(cell) != candidateBlock) {
                return Optional.empty();    // this cell is from a different block
            }
        }
        // all cells had this block in common
        return Optional.of(candidateBlock);
    }

    // visible for testing
    static <T> List<Pair<T>> generateCombinations(Collection<T> collection) {
        if (collection.isEmpty()) {
            return List.of();
        }
        int size = collection.size();
        if (size == 1) {
            return List.of();   // you can't have combinations with only 1 value
        }
        List<Pair<T>> list = new ArrayList<>(size * (size - 1));    // the number of combinations of N items is N*(N-1)
        List<T> values = new ArrayList<>(collection);    // simplest algorithm is to use a List (in which we can jump around)
        for (int x = 0; x < size - 1; ++x) {
            for (int y = x + 1; y < size; ++y) {
                list.add(Pair.of(values.get(x), values.get(y)));
            }
        }
        return list;
    }
}
