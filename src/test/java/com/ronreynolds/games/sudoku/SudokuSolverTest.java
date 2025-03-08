package com.ronreynolds.games.sudoku;

import com.ronreynolds.games.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SudokuSolverTest {

    @Test
    void generateCombinations() {
        assertThat(SudokuSolver.generateCombinations(List.of())).isEmpty();
        assertThat(SudokuSolver.generateCombinations(List.of(1))).isEmpty();
        assertThat(SudokuSolver.generateCombinations(List.of(1, 2))).containsExactly(Pair.of(1, 2));
        assertThat(SudokuSolver.generateCombinations(List.of(1, 2, 3)))
                .containsExactly(Pair.of(1, 2), Pair.of(1, 3), Pair.of(2, 3));
        assertThat(SudokuSolver.generateCombinations(List.of(1, 2, 3, 4, 5)))
                .containsExactly(
                        Pair.of(1, 2), Pair.of(1, 3), Pair.of(1, 4), Pair.of(1, 5),
                        Pair.of(2, 3), Pair.of(2, 4), Pair.of(2, 5),
                        Pair.of(3, 4), Pair.of(3, 5),
                        Pair.of(4, 5));
    }
}