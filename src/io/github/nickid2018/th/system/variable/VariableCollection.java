package io.github.nickid2018.th.system.variable;

public interface VariableCollection {

    VariableProvider<?> getProvider(String name);

    VariableCollection getParent();
}
