package com.ronreynolds.games.util;

import java.util.Arrays;

public class Strings {
    public static <T> String toString2DArray(T[][] array) {
        StringBuilder buf = new StringBuilder();
        for (T[] row : array) {
            buf.append('\n').append(Arrays.toString(row));
        }
        return buf.toString();
    }
}
