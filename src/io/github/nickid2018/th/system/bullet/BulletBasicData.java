package io.github.nickid2018.th.system.bullet;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.gui.ImageRepository;
import io.github.nickid2018.th.gui.SpriteDefinition;
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

import java.util.HashMap;
import java.util.Map;

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
    private final Map<String, Texture> texture = new HashMap<>();
    @Getter
    private final Map<String, SpriteDefinition> sprites = new HashMap<>();
    @Getter
    private final Map<String, VertexArray> vertexArray = new HashMap<>();

    public static final Codec<BulletBasicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0, 50).fieldOf("radius").forGetter(BulletBasicData::getRadius),
            AABB.MIN_MAX_CODEC.fieldOf("render_size").forGetter(BulletBasicData::getRenderAABB),
            Codec.BOOL.fieldOf("has_render_angle").orElse(true).forGetter(BulletBasicData::isHasRenderAngle),
            Codec.BOOL.fieldOf("has_tint").orElse(true).forGetter(BulletBasicData::isHasTint),
            Codec.either(
                    SpriteDefinition.CODEC,
                    Codec.unboundedMap(Codec.STRING, SpriteDefinition.CODEC)
            ).fieldOf("sprite").forGetter(BulletBasicData::getSpriteData)
    ).apply(instance, BulletBasicData::new));

    public BulletBasicData(float radius, AABB renderAABB,
                           boolean hasRenderAngle, boolean hasTint,
                           Either<SpriteDefinition, Map<String, SpriteDefinition>> spriteData) {
        this.radius = radius;
        this.renderAABB = renderAABB;
        this.hasRenderAngle = hasRenderAngle;
        this.hasTint = hasTint;

        spriteData.ifLeft(sprite -> sprites.put("default", sprite)).ifRight(sprites::putAll);

        for (Map.Entry<String, SpriteDefinition> entry : this.sprites.entrySet()) {
            Texture textureData = ImageRepository.createTexture(entry.getValue().getTextureDefinition());

            if (textureData instanceof DynamicTexture)
                throw new IllegalArgumentException("Dynamic texture is not allowed");

            StaticTexture staticTexture = (StaticTexture) textureData;
            AABB spriteAABB = entry.getValue().createSpriteAABB(staticTexture);

            float x1 = renderAABB.getMinX() / Playground.PLAYGROUND_WIDTH * 2;
            float y1 = renderAABB.getMinY() / Playground.PLAYGROUND_HEIGHT * 2;
            float x2 = renderAABB.getMaxX() / Playground.PLAYGROUND_WIDTH * 2;
            float y2 = renderAABB.getMaxY() / Playground.PLAYGROUND_HEIGHT * 2;

            VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.TEXTURE_2D, IndexBufferProvider.DEFAULT);
            builder.pos(x1, y1).uv(spriteAABB.minX, spriteAABB.minY).end();
            builder.pos(x2, y1).uv(spriteAABB.maxX, spriteAABB.minY).end();
            builder.pos(x1, y2).uv(spriteAABB.minX, spriteAABB.maxY).end();
            builder.pos(x2, y2).uv(spriteAABB.maxX, spriteAABB.maxY).end();

            this.texture.put(entry.getKey(), staticTexture);
            this.vertexArray.put(entry.getKey(), builder.build());
        }
    }

    public Either<SpriteDefinition, Map<String, SpriteDefinition>> getSpriteData() {
        if (sprites.size() == 1)
            return Either.left(sprites.values().iterator().next());
        return Either.right(sprites);
    }

    public Texture getTexture(String variant) {
        return variant == null || !texture.containsKey(variant) ? texture.get("default") : texture.get(variant);
    }

    public VertexArray getVertexArray(String variant) {
        return variant == null || !vertexArray.containsKey(variant) ? vertexArray.get("default") : vertexArray.get(variant);
    }
}
