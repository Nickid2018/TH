package io.github.nickid2018.th.system.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.gui.ImageRepository;
import io.github.nickid2018.th.gui.PlayGroundGui;
import io.github.nickid2018.th.gui.SpriteDefinition;
import io.github.nickid2018.tiny2d.buffer.IndexBufferProvider;
import io.github.nickid2018.tiny2d.buffer.VertexArray;
import io.github.nickid2018.tiny2d.buffer.VertexArrayBuilder;
import io.github.nickid2018.tiny2d.buffer.VertexAttributeList;
import io.github.nickid2018.tiny2d.math.AABB;
import io.github.nickid2018.tiny2d.texture.DynamicTexture;
import io.github.nickid2018.tiny2d.texture.StaticTexture;
import io.github.nickid2018.tiny2d.texture.Texture;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PlayerRenderData {

    @Getter
    private final AABB renderAABB;
    @Getter
    private final List<SpriteDefinition> staticSprites;
    @Getter
    private final List<SpriteDefinition> leftSprites;
    @Getter
    private final List<SpriteDefinition> rightSprites;

    @Getter
    private final List<Texture> staticTextures = new ArrayList<>();
    @Getter
    private final List<Texture> leftTextures = new ArrayList<>();
    @Getter
    private final List<Texture> rightTextures = new ArrayList<>();

    @Getter
    private final List<VertexArray> staticVertexArrays = new ArrayList<>();
    @Getter
    private final List<VertexArray> leftVertexArrays = new ArrayList<>();
    @Getter
    private final List<VertexArray> rightVertexArrays = new ArrayList<>();

    public static final Codec<PlayerRenderData> CODEC = RecordCodecBuilder.create(app -> app.group(
            AABB.MIN_MAX_CODEC.fieldOf("render_size").forGetter(PlayerRenderData::getRenderAABB),
            Codec.list(SpriteDefinition.CODEC).fieldOf("static").forGetter(PlayerRenderData::getStaticSprites),
            Codec.list(SpriteDefinition.CODEC).fieldOf("turn_left").forGetter(PlayerRenderData::getLeftSprites),
            Codec.list(SpriteDefinition.CODEC).fieldOf("turn_right").forGetter(PlayerRenderData::getRightSprites)
    ).apply(app, PlayerRenderData::new));

    public PlayerRenderData(AABB renderAABB,
                            List<SpriteDefinition> staticSprites,
                            List<SpriteDefinition> leftSprites,
                            List<SpriteDefinition> rightSprites) {
        this.renderAABB = renderAABB;
        this.staticSprites = staticSprites;
        this.leftSprites = leftSprites;
        this.rightSprites = rightSprites;

        float x1 = renderAABB.getMinX() / PlayGroundGui.SPRITE_SIZE;
        float y1 = renderAABB.getMinY() / PlayGroundGui.SPRITE_SIZE;
        float x2 = renderAABB.getMaxX() / PlayGroundGui.SPRITE_SIZE;
        float y2 = renderAABB.getMaxY() / PlayGroundGui.SPRITE_SIZE;

        staticSprites.forEach(s -> createPlayerRenderData(x1, y1, x2, y2, staticTextures, staticVertexArrays, s));
        leftSprites.forEach(s -> createPlayerRenderData(x1, y1, x2, y2, leftTextures, leftVertexArrays, s));
        rightSprites.forEach(s -> createPlayerRenderData(x1, y1, x2, y2, rightTextures, rightVertexArrays, s));
    }

    private static void createPlayerRenderData(float x1, float y1, float x2, float y2,
                                               List<Texture> toLoadTexture,
                                               List<VertexArray> toLoadArray,
                                               SpriteDefinition sprite) {
        Texture textureData = ImageRepository.createTexture(sprite.getTextureDefinition());

        if (textureData instanceof DynamicTexture)
            throw new IllegalArgumentException("Dynamic texture is not allowed");

        StaticTexture staticTexture = (StaticTexture) textureData;
        AABB spriteAABB = sprite.createSpriteAABB(staticTexture);

        VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.TEXTURE_2D, IndexBufferProvider.DEFAULT);
        builder.pos(x1, y1).uv(spriteAABB.minX, spriteAABB.minY).end();
        builder.pos(x2, y1).uv(spriteAABB.maxX, spriteAABB.minY).end();
        builder.pos(x1, y2).uv(spriteAABB.minX, spriteAABB.maxY).end();
        builder.pos(x2, y2).uv(spriteAABB.maxX, spriteAABB.maxY).end();

        toLoadTexture.add(textureData);
        toLoadArray.add(builder.build());
    }
}
