package com.ronreynolds.games.util;

import java.util.Objects;

/**
 * a pair (tuple) of values of some type T
 */
public class Pair<T> {
    // names like left/right and first/second and so forth seemed just as arbitrary (or worse) as v1 and v2
    public final T v1;
    public final T v2;

    public static <T> Pair<T> of(T v1, T v2) {
        return new Pair<>(v1, v2);
    }

    public Pair(T v1, T v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?> pair = (Pair<?>) o;
        return Objects.equals(v1, pair.v1) && Objects.equals(v2, pair.v2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(v1, v2);
    }
    @Override
    public String toString() {
        return "(" + v1 + ", " + v2 + ")";
    }
}
