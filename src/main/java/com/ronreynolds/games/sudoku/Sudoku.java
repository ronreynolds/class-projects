package com.ronreynolds.games.sudoku;

import java.util.HashSet;
import java.util.Set;

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
        sudokuPuzzle = solve(sudokuPuzzle);
        return sudokuPuzzle.isSolved();
    }

    public static SudokuPuzzle solve(SudokuPuzzle sudokuPuzzle) {
        while (!sudokuPuzzle.isSolved()) {
            boolean foundValue = false;
            // apply all heuristics to the puzzle
            for (SudokuSolver solver : SudokuSolver.getSolvers()) {
                foundValue = solver.apply(sudokuPuzzle);
                if (sudokuPuzzle.isSolved()) {
                    break;  // once we've solved it no point it applying any more solvers
                }
            }
            // we ran all the heuristics and no cells were solved
            if (!foundValue) {
                System.out.println("The given Sudoku puzzle is unsolvable (by this app)");
                break;
            }
        }
        return sudokuPuzzle;
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
