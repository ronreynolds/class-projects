package com.ronreynolds.games.util;

import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * simple IO functions to read/write from/to stdin/stdout
 */
public class Console {
    private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    /**
     * read a line of text from console
     *
     * @return the line read (could be empty; will NEVER be null)
     * @throws RuntimeException if total fail to read from console
     */
    public static String readLine() {
        try {
            return input.readLine();
        } catch (IOException fail) {
            throw new RuntimeException(fail);
        }
    }

    /**
     * continue to prompt the user for an input until they provide something that's valid; then convert it to what the
     * caller expects
     * @param inputValidator validates the user's input; prints error messages as appropriate
     * @param valueConverter converts teh valid input into whatever type is required
     * @return the converted value
     * @param <T> the type of the converted and returned value
     */
    public static <T> T readLineUntil(@NonNull Predicate<String> inputValidator, @NonNull Function<String,T> valueConverter) {
        while (true) {
            String input = readLine();
            if (inputValidator.test(input)) {
                return valueConverter.apply(input);
            } else {
                print("invalid input '%s'", input);
            }
        }
    }

    /** convenience method for extracting the first char of a non-empty string */
    public static final Function<String,Character> stringToChar = s -> s.charAt(0);

    /**
     * print something to screen
     *
     * @param format format string
     * @param args   optional arguments for format string
     */
    public static void print(String format, Object... args) {
        System.out.printf(format + "%n", args);
    }

    public static void close() {
        try {
            // always close your streams when you're done with them
            input.close();
        } catch (IOException ignore) {
        }
    }
}
