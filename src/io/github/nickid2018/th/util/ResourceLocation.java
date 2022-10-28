package io.github.nickid2018.th.util;

import com.mojang.serialization.Codec;

public record ResourceLocation(String namespace, String path) {

    public static final Codec<ResourceLocation> CODEC = Codec.STRING.xmap(ResourceLocation::fromString, ResourceLocation::toString);

    public static ResourceLocation fromString(String path) {
        int index = path.indexOf(':');
        if (index == -1)
            return new ResourceLocation("internal", path);
        return new ResourceLocation(path.substring(0, index), path.substring(index + 1));
    }

    public String toString() {
        return namespace + ":" + path;
    }

    public static ResourceLocation internal(String path) {
        return new ResourceLocation("internal", path);
    }
}
