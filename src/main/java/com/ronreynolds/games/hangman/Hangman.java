package com.ronreynolds.games.hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Hangman {
    private static final boolean testingMode = true;
    private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        GameDifficulty difficulty = getGameDifficulty();
        GameRules gameRules = new GameRules(difficulty);
        GameState gameState = new GameState(getRandomWord());
        GameResult result = GameResult.NOT_DONE;
        while (result == GameResult.NOT_DONE) {
            displayGameState(gameState);
            GameMove move = getMove();
            result = gameRules.getResult(gameState, move);
        }
        print("done.");
    }

    private static GameDifficulty getGameDifficulty() {
        while (true) {
            print("Enter your difficulty: Easy(e), Intermediate(i), or Hard(h)");
            String input = read();
            switch (input.charAt(0)) {
                case 'e':
                    return GameDifficulty.EASY;
                case 'i':
                    return GameDifficulty.INTERMEDIATE;
                case 'h':
                    return GameDifficulty.DIFFICULT;
            }
        }
    }

    private static void print(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    private static String read() {
        try {
            return input.readLine();
        } catch (IOException fail) {
            throw new RuntimeException(fail);
        }
    }

    private static String getRandomWord() {
        print("enter 'random' word");
        return read();
    }

    private static void displayGameState(GameState state) {
        if (testingMode) {
            System.out.println(String.format("word:%s%n", state.answer));
        }
        System.out.println(String.format("guess-count:%d%nguess-word:%s%n", state.guessCount, state.guessSoFar));
    }

    private static GameMove getMove() {
        char guess = '\0';
        Set<Integer> positions = null;

        while (true) {
            print("next letter guess:");
            String input = read();
            if (input.length() > 1) {
                print("only enter 1 character, please");
            } else {
                guess = input.charAt(0);
                if (!Character.isLetter(guess)) {
                    print("%c is not a valid character", guess);
                    continue;
                }
                break;
            }
        }
        while (true) {
            try {
                print("guessed positions:");
                String input = read();
                positions = new HashSet<>();
                for (String part : input.split(" ")) {
                    positions.add(Integer.parseInt(part));
                }
                break;
            } catch (NumberFormatException notANumber) {
                print("at least 1 position is not a number; try again");
            }
        }

        return new GameMove(guess, positions);
    }

    private enum GameResult {
        NOT_DONE, WIN, LOSS;
    }

    private enum GameDifficulty {
        EASY(15, 4),
        INTERMEDIATE(12, 3),
        DIFFICULT(10, 2);
        final int guessCount;
        final int locCount;

        GameDifficulty(int guessCount, int locCount) {
            this.guessCount = guessCount;
            this.locCount = locCount;
        }
    }

    private static class GameMove {
        final char letter;
        final Set<Integer> locations;

        GameMove(char c, Set<Integer> locations) {
            this.letter = c;
            this.locations = Set.copyOf(locations);
        }
    }

    private static class GameState {
        static char DASH = '-';
        final String answer;
        // this doesn't apply because of the position wrinkle
//        final Set<Character> guessedChars = new HashSet<>();
        String guessSoFar;
        int guessCount;

        GameState(String answer) {
            this.answer = answer;
            this.guessSoFar = String.valueOf(DASH).repeat(answer.length());
        }
    }

    private static class GameRules {
        final GameDifficulty difficulty;

        GameRules(GameDifficulty difficulty) {
            this.difficulty = difficulty;
        }

        GameResult getResult(GameState state, GameMove move) {
//            if (state.guessedChars.contains(move.letter)) {
//                print("you already guessed %c; try another letter", move.letter);
//                return GameResult.NOT_DONE;
//            }
            if (move.locations.size() > difficulty.locCount) {
                print("you aren't allowed to guess %d positions (only %d)", move.locations.size(), difficulty.locCount);
                return GameResult.NOT_DONE;
            }

            // move looks valid; apply to state
            boolean moveCounts = applyMoveToState(move, state);

            if (state.answer.equals(state.guessSoFar)) {
                print("YOU GOT IT! :)");
                return GameResult.WIN;
            }
            if (moveCounts) {
                state.guessCount++;
            }
            if (state.guessCount >= difficulty.guessCount) {
                print("%d moves reached; sorry, no more guesses :(", difficulty.guessCount);
                return GameResult.LOSS;
            }
            return GameResult.NOT_DONE;
        }

        private boolean applyMoveToState(GameMove move, GameState state) {
            boolean moveCounts = false;
            for (int location : move.locations) {
                // check if spot in guess was already guessed
                if (state.guessSoFar.charAt(location) == GameState.DASH) {
                    moveCounts = true;  // right or wrong they haven't guessed this location right (yet)
                    if (state.answer.charAt(location) == move.letter) {
                        // not the most efficient way to do it but it should work
                        state.guessSoFar = state.guessSoFar.substring(0, location) + move.letter + state.guessSoFar.substring(location + 1);
                    }
                }
            }
            return moveCounts;
        }
    }
}
