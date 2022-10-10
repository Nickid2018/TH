package io.github.nickid2018.tiny2d.util;

import java.util.function.Supplier;

public class LazyLoadValue<T> {

    private T value;
    private final Supplier<T> loader;

    public LazyLoadValue(Supplier<T> loader) {
        this.loader = loader;
    }

    public T get() {
        if (value == null)
            value = loader.get();
        return value;
    }
}
