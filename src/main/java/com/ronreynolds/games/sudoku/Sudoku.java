package com.ronreynolds.games.sudoku;

import com.ronreynolds.games.util.TriConsumer;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Sudoku {
    public static final int dimension = 9;  // kept as constant in case we someday want to do 16x16 Sudoku. :)
    public static final int blockSize = (int) Math.sqrt(dimension);

    public static void main(String[] args) {
        for (String file : args) {
            try {
                String compactGrid = Files.readString(Path.of(file));
                SudokuPuzzle puzzle = SudokuPuzzle.create(compactGrid);
                solve(puzzle);
                log.info("finished {}", file);
            } catch (Exception fail) {
                log.error("failed to process {}", file, fail);
            }
        }
    }

    public static boolean check(char[][] puzzle) {
        SudokuPuzzle sudokuPuzzle = SudokuPuzzle.create(puzzle);
        return sudokuPuzzle.isValid();
    }

    public static boolean solve(char[][] puzzle) {
        SudokuPuzzle sudokuPuzzle = SudokuPuzzle.create(puzzle);
        if (!sudokuPuzzle.isValid()) {
            System.out.println("The given Sudoku puzzle is invalid");
            return false;
        }
        solve(sudokuPuzzle);
        return sudokuPuzzle.isSolved();
    }

    // no pauses between solvers
    private static final TriConsumer<SudokuSolver, Boolean, SudokuPuzzle> LOG_RESULT = (solver, helped, puzzle) -> {
        log.info("applied {} helped:{} puzzle:\n{}", solver, helped, puzzle);
    };

    public static void solve(SudokuPuzzle sudokuPuzzle) {
        solve(sudokuPuzzle, LOG_RESULT);
    }

    public static void solve(SudokuPuzzle sudokuPuzzle, TriConsumer<SudokuSolver, Boolean, SudokuPuzzle> afterSolver) {
        log.info("starting solve of puzzle:\n{}", sudokuPuzzle);

        while (!sudokuPuzzle.isSolved()) {
            boolean anySolverHelped = false;
            // apply all solvers to the puzzle
            for (SudokuSolver solver : SudokuSolver.getSolvers()) {
                boolean solverHelped = solver.apply(sudokuPuzzle);
                afterSolver.accept(solver, solverHelped, sudokuPuzzle);
                anySolverHelped |= solverHelped;
                if (sudokuPuzzle.isSolved()) {
                    break;  // once we've solved it no point applying any more solvers
                }
            }
            // we ran all the solvers and none of them helped :(
            if (!anySolverHelped) {
                System.out.println("The given Sudoku puzzle is unsolvable (by this app)");
                break;
            }
        }
    }

    // return a set of all possible values in a puzzle of the Sudoku.dimension size
    public static Set<Integer> newAllValuesSet() {
        Set<Integer> values = new HashSet<>();
        for (int val = 1; val <= Sudoku.dimension; ++val) {
            values.add(val);
        }
        return values;
    }
}
