package io.github.nickid2018.th.system.valueprovider;

import io.github.nickid2018.th.system.compute.HittableItem;
import lombok.Getter;

import java.util.List;

public abstract class ValueFunction<T> {

    @Getter
    private final String name;
    private final Class<? extends ValueProvider<?>>[] arguments;

    @SafeVarargs
    public ValueFunction(String name, Class<? extends ValueProvider<?>>... arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public T compute(HittableItem item, List<ValueProvider<?>> arguments) {
        if (arguments.size() != this.arguments.length)
            throw new IllegalArgumentException("The number of arguments is not correct");
        for (int i = 0; i < arguments.size(); i++) {
            if (!this.arguments[i].isInstance(arguments.get(i)))
                throw new IllegalArgumentException("The type of argument " + i + " is not correct");
        }
        return getValue(item, arguments);
    }

    protected abstract T getValue(HittableItem item, List<ValueProvider<?>> arguments);
}
