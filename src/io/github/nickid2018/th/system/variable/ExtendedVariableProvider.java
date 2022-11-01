package io.github.nickid2018.th.system.variable;

public class ExtendedVariableProvider<T> implements VariableProvider<T> {

    private final String name;

    public ExtendedVariableProvider(String name) {
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue(VariableCollection collection) {
        return (T) collection.getProvider(name).getValue(collection.getParent());
    }
}
