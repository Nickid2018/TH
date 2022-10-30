package io.github.nickid2018.th.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.tiny2d.math.AABB;
import io.github.nickid2018.tiny2d.texture.StaticTexture;

public record SpriteDefinition(TextureDefinition texture, int x, int y, int width, int height) {

    public static final Codec<SpriteDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextureDefinition.CODEC.fieldOf("texture").forGetter(SpriteDefinition::texture),
            Codec.INT.fieldOf("x").forGetter(SpriteDefinition::x),
            Codec.INT.fieldOf("y").forGetter(SpriteDefinition::y),
            Codec.INT.fieldOf("width").forGetter(SpriteDefinition::width),
            Codec.INT.fieldOf("height").forGetter(SpriteDefinition::height)
    ).apply(instance, SpriteDefinition::new));

    public AABB createSpriteAABB(StaticTexture texture) {
        // Not a valid AABB
        return AABB.newAABBSize(
                x / (float) texture.getImage().getWidth(),
                1 - y / (float) texture.getImage().getHeight(),
                width / (float) texture.getImage().getWidth(),
                -height / (float) texture.getImage().getHeight()
        );
    }
}
