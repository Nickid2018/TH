package io.github.nickid2018.th.gui;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
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
    private static final Map<ResourceLocation, TextureDefinition> TEXTURE_DEFINITION_MAP = new HashMap<>();

    public static Texture MISSING_TEXTURE;

    public static TextureDefinition createDefinition(ResourceLocation location) {
        return TEXTURE_DEFINITION_MAP.computeIfAbsent(location, l -> TextureDefinition.CODEC.parse(
                new Dynamic<>(JsonOps.INSTANCE, PackManager.createJSON(l))).getOrThrow(false, str -> {})
        );
    }

    public static Texture createTexture(ResourceLocation definition) {
        return createTexture(createDefinition(definition));
    }

    public static Texture createTexture(TextureDefinition definition) {
        return createTexture(definition.location(), definition.isGIF(), definition.clamp(), definition.linearFilter());
    }

    public static Texture createTexture(ResourceLocation imageLocation, boolean isGIF, boolean clamp, boolean linear) {
        if (TEXTURE_MAP.containsKey(imageLocation))
            return TEXTURE_MAP.get(imageLocation);

        Texture texture;
        try (InputStream stream = PackManager.createInputStream(imageLocation)){
            if (stream == null)
                texture = getMissingTexture();
            else if (isGIF) {
                DynamicImage image = new DynamicImage(stream);
                texture = new DynamicTexture(image, mipmapLevel);
            } else {
                Image image = Image.read(stream);
                texture = new StaticTexture(image, mipmapLevel);
            }
        } catch (IOException e) {
            texture = getMissingTexture();
        }
        texture.setClamp(clamp);
        texture.setLinear(linear);
        texture.update();
        TEXTURE_MAP.put(imageLocation, texture);
        return texture;
    }

    public static Texture getTexture(ResourceLocation location) {
        return TEXTURE_MAP.getOrDefault(location, getMissingTexture());
    }

    public static Texture getMissingTexture() {
        if (MISSING_TEXTURE == null) {
            Image image = new Image(32, 32, false);
            image.fillRect(0, 0, 16, 16, 0xFFF800F8);
            image.fillRect(16, 16, 16, 16, 0xFFF800F8);
            image.fillRect(0, 16, 16, 16, 0xFF000000);
            image.fillRect(16, 0, 16, 16, 0xFF000000);
            MISSING_TEXTURE = new StaticTexture(image, 4).update();
        }
        return MISSING_TEXTURE;
    }
}
