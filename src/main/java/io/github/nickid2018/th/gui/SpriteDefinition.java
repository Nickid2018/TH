package io.github.nickid2018.th.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.util.ResourceLocation;
import io.github.nickid2018.tiny2d.math.AABB;
import io.github.nickid2018.tiny2d.texture.StaticTexture;
import lombok.Getter;

public class SpriteDefinition {

    @Getter
    private final ResourceLocation texture;
    @Getter
    private final TextureDefinition textureDefinition;
    @Getter
    private final int x;
    @Getter
    private final int y;
    @Getter
    private final int width;
    @Getter
    private final int height;

    public static final Codec<SpriteDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SpriteDefinition::getTexture),
            Codec.INT.fieldOf("x").forGetter(SpriteDefinition::getX),
            Codec.INT.fieldOf("y").forGetter(SpriteDefinition::getY),
            Codec.INT.fieldOf("width").forGetter(SpriteDefinition::getWidth),
            Codec.INT.fieldOf("height").forGetter(SpriteDefinition::getHeight)
    ).apply(instance, SpriteDefinition::new));

    public SpriteDefinition(ResourceLocation texture, int x, int y, int width, int height) {
        this.texture = texture;
        this.textureDefinition = ImageRepository.createDefinition(texture);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

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
