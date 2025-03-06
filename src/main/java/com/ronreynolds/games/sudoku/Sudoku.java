package com.ronreynolds.games.sudoku;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Sudoku {
    public static final int dimension = 9;
    public static final int blockSize = (int) Math.sqrt(dimension);

    public static void main(String[] args) {
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

    public static void solve(SudokuPuzzle sudokuPuzzle) {
        log.info("starting solve of puzzle:\n{}", sudokuPuzzle);

        while (!sudokuPuzzle.isSolved()) {
            boolean anySolverFoundValue = false;
            // apply all heuristics to the puzzle
            for (SudokuSolver solver : SudokuSolver.getSolvers()) {
                boolean solverFoundValue = solver.apply(sudokuPuzzle);
                if (sudokuPuzzle.isSolved()) {
                    break;  // once we've solved it no point it applying any more solvers
                }
                anySolverFoundValue |= solverFoundValue;
                log.info("applied {} (found:{}) puzzle:\n{}", solver, solverFoundValue, sudokuPuzzle);
            }
            // we ran all the solvers and no cells were solved
            if (!anySolverFoundValue) {
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
