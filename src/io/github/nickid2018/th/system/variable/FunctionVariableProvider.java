package io.github.nickid2018.th.system.variable;

import java.util.function.Supplier;

public class FunctionVariableProvider<T> implements VariableProvider<T> {

    private final Supplier<T> provider;

    public FunctionVariableProvider(Supplier<T> provider) {
        this.provider = provider;
    }

    @Override
    public T getValue(VariableCollection collection) {
        return provider.get();
    }
}
