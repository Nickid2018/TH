package io.github.nickid2018.tiny2d.font;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import io.github.nickid2018.tiny2d.math.AABB;
import io.github.nickid2018.tiny2d.texture.Image;
import io.github.nickid2018.tiny2d.texture.ImageFormat;
import io.github.nickid2018.tiny2d.texture.StaticTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.lwjgl.stb.STBTTFontinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FontAtlas {

    public static final int ATLAS_SIZE = 16;

    private final Image image;
    private final Int2ObjectMap<FontVertexInfos> chars = new Int2ObjectAVLTreeMap<>();
    private final List<AABB> spareSpace = new ArrayList<>();
    private final Queue<FontVertexInfos> toUpdates = new ConcurrentLinkedQueue<>();
    private StaticTexture texture;
    private final VectorFont font;
    private final int size;
    private final int atlasSize;
    private final float oversample;
    private int x = 0, y = 0;

    public FontAtlas(VectorFont font, int size, float oversample) {
        atlasSize = 1 << (32 - Integer.numberOfLeadingZeros((int) ((size * oversample + 2) * ATLAS_SIZE - 1)));
        image = new Image(ImageFormat.LUMINANCE, atlasSize, atlasSize, false);
        this.font = font;
        this.size = size;
        this.oversample = oversample;
    }

    public Optional<FontVertexInfos> putBitmap(STBTTFontinfo font, int codepoint, float scale, int width, int height,
                                               float leftSide, float advanceWidth, float topSide) {
        if (x + width > atlasSize) {
            x = 0;
            y += size * oversample + 2;
        }

        if (y + height > atlasSize)
            return Optional.empty();

        float atlasSize = (float) this.atlasSize;
        FontVertexInfos info = new FontVertexInfos();
        info.codepoint = codepoint;
        info.atlas = this;
        info.minU = x / atlasSize;
        info.minV = y / atlasSize;
        info.maxU = (x + width) / atlasSize;
        info.maxV = (y + height) / atlasSize;
        info.width = (int) (width / oversample);
        info.height = (int) (height / oversample);
        info.leftBearing = leftSide / oversample;
        info.advanceWidth = advanceWidth / oversample;
        info.topSide = topSide / oversample;

        chars.put(codepoint, info);
        image.copyFromFont(font, codepoint, width, height, scale, scale, 0, 0, x, y);
        toUpdates.offer(info);
        x += width + 2;

        return Optional.of(info);
    }

    public Image getImage() {
        return image;
    }

    public FontVertexInfos getChar(int codepoint) {
        return chars.get(codepoint);
    }

    @RenderThreadOnly
    public void refresh() {
        if (texture == null)
            texture = new StaticTexture(image, 0).setLinear(false).setClamp(true);
        while (!toUpdates.isEmpty()) {
            FontVertexInfos info = toUpdates.poll();
            int x = (int) (info.minU * atlasSize);
            int y = (int) (info.minV * atlasSize);
            int xSize = (int) ((info.maxU - info.minU) * atlasSize);
            int ySize = (int) ((info.maxV - info.minV) * atlasSize);
            texture.update(x, y, xSize, ySize);
        }
    }

    @RenderThreadOnly
    public StaticTexture getTexture() {
        refresh();
        return texture;
    }
}
