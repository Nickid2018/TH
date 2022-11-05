package io.github.nickid2018.th.system.valueprovider.function;

import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class UserValueFunctions {

    private static final Map<ResourceLocation, ValueFunction<?>> VALUE_FUNCTION_MAP = new HashMap<>();

    public static void registerValueProvider(ResourceLocation location, ValueFunction<?> provider) {
        VALUE_FUNCTION_MAP.put(location.normalize(), provider);
    }
    @SuppressWarnings("unchecked")
    public static <T> ValueFunction<T> getValueProvider(ResourceLocation location) {
        if (VALUE_FUNCTION_MAP.containsKey(location))
            return (ValueFunction<T>) VALUE_FUNCTION_MAP.get(location);
        UserDefinedValueFunction<T> function =
                (UserDefinedValueFunction<T>) PackManager.createObject(location, UserDefinedValueFunction.CODEC);
        registerValueProvider(location, function);
        return function;
    }

    public static <T> ValueFunction<T> getValueProvider(String key,
                                                        Map<String, ValueFunction<T>> defaults) {
        if (defaults.containsKey(key))
            return defaults.get(key);
        ResourceLocation location = ResourceLocation.fromString(key).normalize();
        return getValueProvider(location);
    }
}
