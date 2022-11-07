package io.github.nickid2018.th.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.util.ResourceLocation;

public record TextureDefinition(ResourceLocation location, boolean clamp, boolean linearFilter, boolean isGIF) {

    public static final Codec<TextureDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("location").forGetter(TextureDefinition::location),
            Codec.BOOL.fieldOf("clamp").orElse(true).forGetter(TextureDefinition::clamp),
            Codec.BOOL.fieldOf("linear_filter").orElse(true).forGetter(TextureDefinition::linearFilter),
            Codec.BOOL.fieldOf("animated").orElse(false).forGetter(TextureDefinition::isGIF)
    ).apply(instance, TextureDefinition::new));
}
