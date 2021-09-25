/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.isam.render.font;

import com.github.isam.render.texture.TextureUtil;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VectorFont {

    private static final Map<URL, STBTTFontinfo> fontBases = new HashMap<>();

    private final int size;
    private final STBTTFontinfo font;
    private final List<FontAtlas> atlases = Lists.newArrayList();
    private final Int2ObjectMap<FontAtlas> chars = new Int2ObjectAVLTreeMap<>();
    private final float ascent;
    private final float descent;
    private final float lineGap;
    private final float spaceLength;
    private final float scale;

    public VectorFont(URL fontFile, int size) throws IOException {
        this.size = size;
        if(fontBases.containsKey(fontFile))
            font = fontBases.get(fontFile);
        else {
            font = STBTTFontinfo.create();
            ByteBuffer buffer = TextureUtil.readResource(fontFile.openConnection().getInputStream());
            buffer.rewind();
            if (!STBTruetype.stbtt_InitFont(font, buffer))
                throw new IOException(String.format("Can't initialize font: %s", fontFile));
            fontBases.put(fontFile, font);
        }
        scale = STBTruetype.stbtt_ScaleForPixelHeight(font, size);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer ascentBuf = stack.mallocInt(1);
            IntBuffer descentBuf = stack.mallocInt(1);
            IntBuffer lineGapBuf = stack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(font, ascentBuf, descentBuf, lineGapBuf);
            ascent = ascentBuf.get(0) * scale;
            descent = descentBuf.get(0) * scale;
            lineGap = lineGapBuf.get(0) * scale;
            IntBuffer advanceWidthBuf = stack.mallocInt(1);
            IntBuffer leftSideBearingBuf = stack.mallocInt(1);
            STBTruetype.stbtt_GetCodepointHMetrics(font, ' ', advanceWidthBuf, leftSideBearingBuf);
            spaceLength = advanceWidthBuf.get(0) * scale;
        }
    }

    public int getSize() {
        return size;
    }

    @SuppressWarnings("unused")
    public float getAscent() {
        return ascent;
    }

    @SuppressWarnings("unused")
    public float getDescent() {
        return descent;
    }

    @SuppressWarnings("unused")
    public float getLineGap() {
        return lineGap;
    }

    public float getSpaceLength() {
        return spaceLength;
    }

    @Nonnull
    public FontVertexInfos getCodepointInfo(int codepoint) throws IllegalArgumentException {
        if (chars.containsKey(codepoint))
            return chars.get(codepoint).getChar(codepoint);
        try (MemoryStack stack = MemoryStack.stackPush()) {
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
            for (FontAtlas atlas : atlases) {
                Optional<FontVertexInfos> optional = atlas.putBitmap(font, codepoint, scale, width, height, left, top,
                        convertL, convertA, ascent + bottom);
                if (optional.isPresent()) {
                    chars.put(codepoint, atlas);
                    return optional.get();
                }
            }
            FontAtlas atlas = new FontAtlas(this);
            Optional<FontVertexInfos> optional = atlas.putBitmap(font, codepoint, scale, width, height, left, top,
                    convertL, convertA, ascent + bottom);
            if (optional.isPresent()) {
                atlases.add(atlas);
                chars.put(codepoint, atlas);
                return optional.get();
            }
        }
        throw new IllegalArgumentException(String.format("Can't generate the font bitmap - too large (Character: %x)", codepoint));
    }
}
