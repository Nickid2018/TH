package io.github.nickid2018.th.system.bullet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.gui.ImageRepository;
import io.github.nickid2018.th.gui.SpriteDefinition;
import io.github.nickid2018.th.gui.TextureDefinition;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.tiny2d.buffer.IndexBufferProvider;
import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.buffer.VertexArrayBuilder;
import io.github.nickid2018.tiny2d.buffer.VertexAttributeList;
import io.github.nickid2018.tiny2d.math.AABB;
import io.github.nickid2018.tiny2d.texture.DynamicTexture;
import io.github.nickid2018.tiny2d.texture.StaticTexture;
import io.github.nickid2018.tiny2d.texture.Texture;
import lombok.Getter;

public class BulletBasicData {

    @Getter
    private final float radius;
    @Getter
    private final AABB renderAABB;
    @Getter
    private final boolean hasRenderAngle;
    @Getter
    private final boolean hasTint;
    @Getter
    private final TextureDefinition texture;
    @Getter
    private final SpriteDefinition sprite;

    @Getter
    private final Texture textureInstance;

    private final VertexArray vertexArray;

    public static final Codec<BulletBasicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0, 50).fieldOf("radius").forGetter(BulletBasicData::getRadius),
            AABB.MIN_MAX_CODEC.fieldOf("render_size").forGetter(BulletBasicData::getRenderAABB),
            Codec.BOOL.fieldOf("has_render_angle").orElse(true).forGetter(BulletBasicData::isHasRenderAngle),
            Codec.BOOL.fieldOf("has_tint").orElse(true).forGetter(BulletBasicData::isHasTint),
            SpriteDefinition.CODEC.fieldOf("sprite").forGetter(BulletBasicData::getSprite)
    ).apply(instance, BulletBasicData::new));

    public BulletBasicData(float radius, AABB renderAABB,
                           boolean hasRenderAngle, boolean hasTint, SpriteDefinition sprite) {
        this.radius = radius;
        this.renderAABB = renderAABB;
        this.hasRenderAngle = hasRenderAngle;
        this.hasTint = hasTint;
        this.sprite = sprite;
        this.texture = sprite.getTextureDefinition();
        textureInstance = ImageRepository.createTexture(texture);

        if (textureInstance instanceof DynamicTexture)
            throw new IllegalArgumentException("Dynamic texture is not allowed");
        StaticTexture staticTexture = (StaticTexture) textureInstance;
        AABB spriteAABB = sprite.createSpriteAABB(staticTexture);

        float x1 = renderAABB.getMinX() / Playground.PLAYGROUND_WIDTH * 2;
        float y1 = renderAABB.getMinY() / Playground.PLAYGROUND_HEIGHT * 2;
        float x2 = renderAABB.getMaxX() / Playground.PLAYGROUND_WIDTH * 2;
        float y2 = renderAABB.getMaxY() / Playground.PLAYGROUND_HEIGHT * 2;

        VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.TEXTURE_2D, IndexBufferProvider.DEFAULT);
        builder.pos(x1, y1).uv(spriteAABB.minX, spriteAABB.minY).end();
        builder.pos(x2, y1).uv(spriteAABB.maxX, spriteAABB.minY).end();
        builder.pos(x1, y2).uv(spriteAABB.minX, spriteAABB.maxY).end();
        builder.pos(x2, y2).uv(spriteAABB.maxX, spriteAABB.maxY).end();
        vertexArray = builder.build();
    }

    public VertexArray getVertexArray() {
        return vertexArray;
    }
}
