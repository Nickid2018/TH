package io.github.nickid2018.th.system.variable;

public class ConstantVariableProvider<T> implements VariableProvider<T> {

    private final T value;

    public ConstantVariableProvider(T value) {
        this.value = value;
    }

    @Override
    public T getValue(VariableCollection collection) {
        return value;
    }
}
