package io.github.nickid2018.th.system.bullet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.gui.TextureDefinition;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.tiny2d.buffer.IndexBufferProvider;
import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.buffer.VertexArrayBuilder;
import io.github.nickid2018.tiny2d.buffer.VertexAttributeList;
import lombok.Getter;

public class BulletBasicData {

    @Getter
    private final float radius;
    @Getter
    private final int renderWidth;
    @Getter
    private final int renderHeight;
    @Getter
    private final boolean hasRenderAngle;
    @Getter
    private final boolean hasTint;
    @Getter
    private final TextureDefinition texture;

    @Getter
    private float halfHorizontal;
    @Getter
    private float halfVertical;
    private VertexArray vertexArray;

    public static final Codec<BulletBasicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0, 50).fieldOf("radius").forGetter(BulletBasicData::getRadius),
            Codec.intRange(1, 100).fieldOf("renderWidth").forGetter(BulletBasicData::getRenderWidth),
            Codec.intRange(1, 100).fieldOf("renderHeight").forGetter(BulletBasicData::getRenderHeight),
            Codec.BOOL.orElse(true).fieldOf("hasRenderAngle").forGetter(BulletBasicData::isHasRenderAngle),
            Codec.BOOL.orElse(true).fieldOf("hasTint").forGetter(BulletBasicData::isHasTint),
            TextureDefinition.CODEC.fieldOf("texture").forGetter(BulletBasicData::getTexture)
    ).apply(instance, BulletBasicData::new));

    public BulletBasicData(float radius, int renderWidth, int renderHeight,
                           boolean hasRenderAngle, boolean hasTint, TextureDefinition texture) {
        this.radius = radius;
        this.renderWidth = renderWidth;
        this.renderHeight = renderHeight;
        this.hasRenderAngle = hasRenderAngle;
        this.hasTint = hasTint;
        this.texture = texture;
    }

    public VertexArray getVertexArray() {
        if (vertexArray == null) {
            halfHorizontal = (float) renderWidth / Playground.PLAYGROUND_WIDTH;
            halfVertical = (float) renderHeight / Playground.PLAYGROUND_HEIGHT;

            VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.TEXTURE_2D, IndexBufferProvider.DEFAULT);
            builder.pos(-halfHorizontal / 2, -halfVertical / 2).uv(0, 0).end();
            builder.pos(halfHorizontal / 2, -halfVertical / 2).uv(1, 0).end();
            builder.pos(-halfHorizontal / 2, halfVertical / 2).uv(0, 1).end();
            builder.pos(halfHorizontal / 2, halfVertical / 2).uv(1, 1).end();
        }
        return vertexArray;
    }
}
