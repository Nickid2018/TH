package io.github.nickid2018.th.system.compute;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class Registry<T> {

    private final Map<ResourceLocation, T> registry = new HashMap<>();

    private final Codec<T> codec;

    public Registry(Codec<T> codec) {
        this.codec = codec;
    }

    public void register(ResourceLocation location, T object) {
        registry.put(location.normalize(), object);
    }

    public T get(ResourceLocation location) {
        if (!registry.containsKey(location.normalize())) {
            T object = PackManager.createObject(location, codec);
            if (object != null)
                register(location, object);
        }
        return registry.get(location);
    }

    public ResourceLocation getKey(T object) {
        return registry.entrySet().stream()
                .filter(entry -> entry.getValue().equals(object))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
