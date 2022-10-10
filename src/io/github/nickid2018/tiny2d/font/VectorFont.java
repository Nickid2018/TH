package io.github.nickid2018.tiny2d.font;

import com.google.common.collect.Lists;
import io.github.nickid2018.tiny2d.texture.TextureUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Optional;

public class VectorFont {
    private final STBTTFontinfo font;
    private final Int2ObjectMap<List<FontAtlas>> atlases = new Int2ObjectAVLTreeMap<>();
    private final Int2ObjectMap<Int2ObjectMap<FontAtlas>> chars = new Int2ObjectAVLTreeMap<>();
    private final float oversample;

    private final int ascent;
    private final int descent;
    private final int lineGap;

    public VectorFont(InputStream fontFile) throws IOException {
        this(fontFile, 3.0f);
    }

    public VectorFont(InputStream fontFile, float oversample) throws IOException {
        font = STBTTFontinfo.create();
        this.oversample = oversample;
        ByteBuffer buffer = TextureUtil.readResource(fontFile);
        buffer.rewind();
        if (!STBTruetype.stbtt_InitFont(font, buffer))
            throw new IOException(String.format("Can't initialize font: %s", fontFile));
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer ascent = stack.mallocInt(1);
            IntBuffer descent = stack.mallocInt(1);
            IntBuffer lineGap = stack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(font, ascent, descent, lineGap);
            this.ascent = ascent.get(0);
            this.descent = descent.get(0);
            this.lineGap = lineGap.get(0);
        }
    }

    public float getSize(int size) {
        return getAscent(size) - getDescent(size);
    }

    public float getAscent(int size) {
        return ascent * STBTruetype.stbtt_ScaleForPixelHeight(font, size);
    }

    public float getDescent(int size) {
        return descent * STBTruetype.stbtt_ScaleForPixelHeight(font, size);
    }

    public float getLineGap(int size) {
        return lineGap * STBTruetype.stbtt_ScaleForPixelHeight(font, size);
    }

    public float getSpaceLength(int size) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer advance = stack.mallocInt(1);
            STBTruetype.stbtt_GetCodepointHMetrics(font, ' ', advance, null);
            return advance.get(0) * STBTruetype.stbtt_ScaleForPixelHeight(font, size);
        }
    }

    public float getKerning(int size, int left, int right) {
        return STBTruetype.stbtt_GetCodepointKernAdvance(font, left, right) * STBTruetype.stbtt_ScaleForPixelHeight(font, size);
    }

    public FontVertexInfos getCodepointInfo(int size, int codepoint) throws IllegalArgumentException {
        if (codepoint == ' ')
            return new FontVertexInfos(' ', null, 0, 0, 0, 0, 0, getSpaceLength(size), 0, 0, 0);
        if (chars.getOrDefault(size, new Int2ObjectAVLTreeMap<>()).containsKey(codepoint))
            return chars.get(size).get(codepoint).getChar(codepoint);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            float oversampledSize = size * oversample;
            float scale = STBTruetype.stbtt_ScaleForPixelHeight(font, oversampledSize);
            float ascent = getAscent(size) * oversample;
            IntBuffer leftBuf = stack.mallocInt(1);
            IntBuffer bottomBuf = stack.mallocInt(1);
            IntBuffer rightBuf = stack.mallocInt(1);
            IntBuffer topBuf = stack.mallocInt(1);
            STBTruetype.stbtt_GetCodepointBitmapBox(font, codepoint, scale, scale, leftBuf, bottomBuf, rightBuf,
                    topBuf);
            IntBuffer advanceWidthBuf = stack.mallocInt(1);
            IntBuffer leftSideBearingBuf = stack.mallocInt(1);
            STBTruetype.stbtt_GetCodepointHMetrics(font, codepoint, advanceWidthBuf, leftSideBearingBuf);
            int left = leftBuf.get(0);
            int bottom = bottomBuf.get(0);
            int right = rightBuf.get(0);
            int top = topBuf.get(0);
            int width = right - left;
            int height = top - bottom;
            int advanceWidth = advanceWidthBuf.get(0);
            int leftSideBearing = leftSideBearingBuf.get(0);
            float convertL = leftSideBearing * scale;
            float convertA = advanceWidth * scale;
            if (width == 0 || height == 0)
                throw new IllegalArgumentException(String.format("Can't generate the font bitmap - unrecorded character %x", codepoint));
            for (FontAtlas atlas : atlases.computeIfAbsent(size, s -> Lists.newArrayList())) {
                Optional<FontVertexInfos> optional = atlas.putBitmap(font, codepoint, scale, width, height,
                        convertL, convertA, ascent + bottom);
                if (optional.isPresent()) {
                    chars.computeIfAbsent(size, s -> new Int2ObjectAVLTreeMap<>()).put(codepoint, atlas);
                    return optional.get();
                }
            }
            FontAtlas atlas = new FontAtlas(this, size, oversample);
            Optional<FontVertexInfos> optional = atlas.putBitmap(font, codepoint, scale, width, height,
                    convertL, convertA, ascent + bottom);
            if (optional.isPresent()) {
                atlases.computeIfAbsent(size, s -> Lists.newArrayList()).add(atlas);
                chars.computeIfAbsent(size, s -> new Int2ObjectAVLTreeMap<>()).put(codepoint, atlas);
                return optional.get();
            }
        }
        throw new IllegalArgumentException(String.format("Can't generate the font bitmap - too large (Character: %x)", codepoint));
    }
}
