package com.ronreynolds.games.hangman;

import com.ronreynolds.games.util.RandomWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Hangman {
    private static final boolean testingMode = true;
    private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        try {
            while (true) {
                GameDifficulty difficulty = getGameDifficulty();
                GameRules gameRules = new GameRules(difficulty);
                GameState gameState = new GameState(RandomWord.newWord());
                GameResult result = GameResult.CONTINUE;
                while (result == GameResult.CONTINUE) {
                    displayGameState(gameRules, gameState);
                    GameMove move = getMove(gameState);
                    result = gameRules.playMove(gameState, move);
                }
                print("would you like to play again? (Y/n)");
                String input = readLine().toLowerCase(Locale.ROOT);
                if (input.startsWith("n")) {
                    break;  // player said "no" (or something that started with n)  niet?  nine?  nope?
                }
            }
            print("good bye! :)");
        } finally {
            try {
                // always close your streams when you're done with them
                input.close();
            } catch (IOException ignore) {
            }
        }
    }

    /**
     * request game difficulty from user
     *
     * @return {@code GameDifficulty} selected
     */
    private static GameDifficulty getGameDifficulty() {
        while (true) {
            print("Enter your difficulty: Easy(e), Intermediate(i), or Hard(h)");
            String input = readLine().toLowerCase(Locale.ROOT);
            switch (input.charAt(0)) {
                case 'e':
                    return GameDifficulty.EASY;
                case 'i':
                    return GameDifficulty.INTERMEDIATE;
                case 'h':
                    return GameDifficulty.DIFFICULT;
            }
            // invalid input; try again
        }
    }

    /**
     * print something to screen
     *
     * @param format format string
     * @param args   optional arguments for format string
     */
    private static void print(String format, Object... args) {
        System.out.printf(format + "%n", args);
    }

    /**
     * read a line of text from console
     *
     * @return the line read (could be empty; will NEVER be null)
     * @throws RuntimeException if total fail to read from console
     */
    private static String readLine() {
        try {
            return input.readLine();
        } catch (IOException fail) {
            throw new RuntimeException(fail);
        }
    }

    private static void displayGameState(GameRules rules, GameState state) {
        if (testingMode) {
            print("word:%s", state.answer);
        }
        print("%d guesses left%nguess-word:%s%n", rules.difficulty.maxGuessCount - state.guessCount, state.guessSoFar);
    }

    /**
     * get a move from the player
     *
     * @param gameState is provided so player inputs can be validated sooner
     * @return the move made by the player
     */
    private static GameMove getMove(GameState gameState) {
        char guess;
        Set<Integer> positions;

        while (true) {
            print("next letter guess (or 'solve' if you're feeling lucky):");
            String input = readLine();
            if (input.equalsIgnoreCase("solve")) {
                print("guessed word:");
                input = readLine();
                return new GameMove(input);
            }
            // take the first letter; ignore all others
            guess = input.charAt(0);
            if (!Character.isLetter(guess)) {
                print("%c is not a valid character", guess);
                continue; // try again
            }
            break;
        }
        while (true) {
            try {
                final int maxPosition = gameState.answer.length() - 1;
                print("guessed positions space-delimited (0...%d):", maxPosition);
                String input = readLine();
                positions = new HashSet<>();
                for (String part : input.split(" ")) {
                    int position = Integer.parseInt(part);
                    if (position < 0 || position > maxPosition) {
                        print("invalid position %d; try again", position);
                        continue;
                    }
                    positions.add(position);
                }
                if (positions.isEmpty()) {
                    continue;   // no valid positions entered; try again
                }
                break;
            } catch (NumberFormatException notANumber) {
                print("at least 1 position is not a number; try again");
            }
        }

        return new GameMove(guess, positions);
    }

    private enum GameResult {
        CONTINUE, WIN, LOSS;
    }

    private enum GameDifficulty {
        EASY(15, 4),
        INTERMEDIATE(12, 3),
        DIFFICULT(10, 2);
        final int maxGuessCount;
        final int maxLocCount;

        GameDifficulty(int maxGuessCount, int maxLocCount) {
            this.maxGuessCount = maxGuessCount;
            this.maxLocCount = maxLocCount;
        }
    }

    /**
     * all data about a single player's move; in multi-player games this would include a player identifier
     */
    private static class GameMove {
        // letter guessed by player
        final char letter;
        // positions for letter guessed by player
        final Set<Integer> locations;
        // solution guessed by player; will be blank if no guess provided
        final String solveGuess;

        // player guessed a letter and location
        GameMove(char c, Set<Integer> locations) {
            this(c, locations, null);
        }

        // player guessed a whole word
        GameMove(String solve) {
            this(' ', Set.of(), solve);
        }

        // only used internally
        private GameMove(char c, Set<Integer> locations, String solve) {
            this.letter = Character.toLowerCase(c); // since we force the answer to lower-case
            this.locations = Set.copyOf(locations);
            this.solveGuess = solve != null ? solve.toLowerCase(Locale.ROOT) : "";
        }
    }

    /**
     * represents the current state of the game
     */
    private static class GameState {
        // place-holder for un-guessed letters
        static char DASH = '-';
        // the secret word to be guessed
        final String answer;
        // answer with letters guessed so far and '-' for un-guessed letters
        String guessSoFar;
        // how many guesses the player has made
        int guessCount;

        /**
         * @param answer the secret word to be guessed
         * @throws NullPointerException     if null passed in for answer
         * @throws IllegalArgumentException if answer empty or all whitespace passed in for answer
         */
        GameState(String answer) {
            // force answer to lower-case to avoid case differences (also remove whitespaces from ends just in case)
            this.answer = answer.trim().toLowerCase(Locale.ROOT);
            if (this.answer.isEmpty()) {
                throw new IllegalArgumentException("can't have an answer with no letters!");
            }
            this.guessSoFar = String.valueOf(DASH).repeat(answer.length());
        }
    }

    /**
     * the rules of the game
     * <ol>
     *   <li>validates moves</li>
     *   <li>applies moves to the game state</li>
     *   <li>determines result of move (win, loss, continue)</li>
     * </ol>
     */
    private static class GameRules {
        // dictates how difficult the game will be for the player (i.e., part of rules)
        final GameDifficulty difficulty;

        GameRules(GameDifficulty difficulty) {
            this.difficulty = difficulty;
        }

        GameResult playMove(GameState state, GameMove move) {
            if (move.locations.size() > difficulty.maxLocCount) {
                print("you aren't allowed to guess %d positions (only %d)", move.locations.size(), difficulty.maxLocCount);
                return GameResult.CONTINUE;
            }

            // move looks valid; apply to state
            boolean guessedWrong = applyMoveToState(move, state);

            if (state.answer.equals(state.guessSoFar)) {
                print("YOU GOT IT! :)");
                return GameResult.WIN;
            }
            if (guessedWrong) {
                state.guessCount++;
            }
            if (state.guessCount >= difficulty.maxGuessCount) {
                print("%d moves reached; sorry, no more guesses :(", difficulty.maxGuessCount);
                return GameResult.LOSS;
            }
            return GameResult.CONTINUE;
        }

        /**
         * apply the move to the game state and return if it costs the player 1 guess
         *
         * @param move  the move provided by the player for this turn
         * @param state the current (and mutable) state of the game
         * @return if the move costs the player 1 move
         */
        private boolean applyMoveToState(GameMove move, GameState state) {
            // check for solve guess first
            if (!move.solveGuess.isEmpty()) {
                if (move.solveGuess.equals(state.answer)) {
                    state.guessSoFar = state.answer;    // triggers win
                    return false;
                }
                return true;    // incorrect guess; costs 1 move
            }

            boolean atLeastOneRightGuess = false;
            for (int location : move.locations) {
                // check if spot in guess was already guessed
                if (state.guessSoFar.charAt(location) == GameState.DASH) {
                    if (state.answer.charAt(location) == move.letter) {
                        // replace the - at location with the guessed letter
                        state.guessSoFar = state.guessSoFar.substring(0, location) + move.letter + state.guessSoFar.substring(location + 1);
                        print("there is a '%c' at %d!", move.letter, location);
                        atLeastOneRightGuess = true;
                    } else {
                        print("there is not a '%c' at %d.", move.letter, location);
                    }
                } else {
                    print("you already guessed the letter at %d", location);
                }
            }
            return !atLeastOneRightGuess;
        }
    }
}
