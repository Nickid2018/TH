package io.github.nickid2018.th.gui;

import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.util.ResourceLocation;
import io.github.nickid2018.tiny2d.texture.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageRepository {

    public static int mipmapLevel = 4;
    private static final Map<ResourceLocation, Texture> TEXTURE_MAP = new HashMap<>();

    public static Texture createTexture(TextureDefinition definition) throws IOException {
        return createTexture(definition.location(), definition.isGIF());
    }

    public static Texture createTexture(ResourceLocation location, boolean isGIF) throws IOException {
        Texture texture;
        try (InputStream stream = PackManager.createInputStream(location)){
            if (isGIF) {
                DynamicImage image = new DynamicImage(stream);
                texture = new DynamicTexture(image, mipmapLevel);
            } else {
                Image image = Image.read(stream);
                texture = new StaticTexture(image, mipmapLevel);
            }
        }
        texture.update();
        TEXTURE_MAP.put(location, texture);
        return texture;
    }

    public static Texture getTexture(ResourceLocation location) {
        return TEXTURE_MAP.get(location);
    }
}
