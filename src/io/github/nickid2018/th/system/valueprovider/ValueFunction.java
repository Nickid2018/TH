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
            throw new IllegalArgumentException("%s: The number of arguments is not correct! Should be %d but found %d."
                    .formatted(name, this.arguments.length, arguments.size()));
        for (int i = 0; i < arguments.size(); i++)
            if (!this.arguments[i].isInstance(arguments.get(i)))
                throw new IllegalArgumentException("%s: The type of argument %d is not correct! Should be %s but found %s."
                        .formatted(name, i, this.arguments[i].getSimpleName(), arguments.get(i).getClass().getSimpleName()));
        return getValue(item, arguments);
    }

    protected abstract T getValue(HittableItem item, List<ValueProvider<?>> arguments);
}
