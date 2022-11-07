package io.github.nickid2018.th.util;

import com.mojang.serialization.Codec;
import io.github.nickid2018.th.pack.PackManager;

import java.util.Objects;

public record ResourceLocation(String namespace, String path) {

    public static final Codec<ResourceLocation> CODEC = Codec.STRING.xmap(ResourceLocation::fromString, ResourceLocation::toString);

    public static ResourceLocation fromString(String path) {
        int index = path.indexOf(':');
        if (index == -1)
            return new ResourceLocation(null, path);
        return new ResourceLocation(path.substring(0, index), path.substring(index + 1));
    }

    public ResourceLocation normalize() {
        if (namespace == null)
            return new ResourceLocation(PackManager.getNamespaceDefaultSelect(path), path);
        return this;
    }

    public String toString() {
        return namespace == null ? path : namespace + ":" + path;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof ResourceLocation other) {
            return path.equals(other.path) &&
                    (Objects.equals(namespace, other.namespace) ||
                            (namespace == null && Objects.equals(PackManager.getNamespaceDefaultSelect(path), other.namespace)));
        }
        return false;
    }

    public static ResourceLocation internal(String path) {
        return new ResourceLocation("internal", path);
    }
}
