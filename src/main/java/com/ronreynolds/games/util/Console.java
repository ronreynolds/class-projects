package com.ronreynolds.games.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
