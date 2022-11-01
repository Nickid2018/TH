package io.github.nickid2018.th.system.variable;

public interface VariableProvider<T> {

    T getValue(VariableCollection collection);
}
