package io.github.nickid2018.th.system.valueprovider.integer;

import io.github.nickid2018.th.system.valueprovider.ValueProvider;

public interface IntProvider extends ValueProvider<Integer> {

    default String getValueProviderType() {
        return "int";
    }
}
