package com.ronreynolds.games.sudoku;

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
        while (!sudokuPuzzle.isSolved()) {
            boolean foundValue = false;
            // apply all heuristics to the puzzle
            for (SudokuHeuristic heuristic : SudokuHeuristic.getHeuristics()) {
                foundValue = heuristic.apply(sudokuPuzzle);
            }

            if (!foundValue) {
                System.out.println("The given Sudoku puzzle is unsolvable");
                return false;
            }
        }
        return true;
    }
}
