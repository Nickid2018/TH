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
package com.github.isam.render.texture;

import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;

import static org.lwjgl.opengl.GL30.*;

public class Image implements AutoCloseable {

    private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    private final Format format;

    private final int width;

    private final int height;

    private final boolean useStbFree;
    private final int size;
    /**
     * Pointer of the texture data head
     */
    private long pixels;

    public Image(int width, int height, boolean calloc) {
        this(Format.RGBA, width, height, calloc);
    }

    public Image(Format format, int width, int height, boolean calloc) {
        this.format = format;
        this.width = width;
        this.height = height;
        size = width * height * format.components();
        useStbFree = false;
        if (calloc) {
            pixels = MemoryUtil.nmemCalloc(1L, size);
        } else {
            pixels = MemoryUtil.nmemAlloc(size);
        }
    }

    public Image(Format format, int width, int height, boolean useStbFree, long addr) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.useStbFree = useStbFree;
        pixels = addr;
        size = width * height * format.components();
    }

    public static Image read(InputStream inputStream) throws IOException {
        return read(Format.RGBA, inputStream);
    }

    @SuppressWarnings("deprecation")
    public static Image read(@Nullable Format format, InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.rewind();
            return read(format, byteBuffer);
        } finally {
            MemoryUtil.memFree(byteBuffer);
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static Image read(ByteBuffer byteBuffer) throws IOException {
        return read(Format.RGBA, byteBuffer);
    }

    public static Image read(@Nullable Format format, ByteBuffer byteBuffer) throws IOException {
        if (format != null && !format.supportedByStb())
            throw new UnsupportedOperationException("Don't know how to read format " + format);
        if (MemoryUtil.memAddress(byteBuffer) == 0L)
            throw new IllegalArgumentException("Invalid buffer");
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            IntBuffer width = memoryStack.mallocInt(1);
            IntBuffer height = memoryStack.mallocInt(1);
            IntBuffer channel = memoryStack.mallocInt(1);
            ByteBuffer pixels = STBImage.stbi_load_from_memory(byteBuffer, width, height, channel,
                    format == null ? 0 : format.components);
            if (pixels == null)
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            return new Image(format == null ? Format.getStbFormat(channel.get(0)) : format, width.get(0), height.get(0),
                    true, MemoryUtil.memAddress(pixels));
        }
    }

    private static void setClamp(boolean clamp) {
        if (clamp) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }
    }

    private static void setFilter(boolean linear, boolean useMipmap) {
        if (linear) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, useMipmap ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, useMipmap ? GL_NEAREST_MIPMAP_LINEAR : GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
    }

    public static Image fromBase64(String base64) throws IOException {
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            ByteBuffer pixInBase64 = memoryStack.UTF8(base64.replaceAll("\n", ""), false);
            ByteBuffer decoded = Base64.getDecoder().decode(pixInBase64);
            ByteBuffer pixels = memoryStack.malloc(decoded.remaining());
            pixels.put(decoded);
            pixels.rewind();
            return read(pixels);
        }
    }

    public String toString() {
        return "Texture[" + format + " " + width + "x" + height + "@" + pixels + (useStbFree ? "S" : "N") + "]";
    }

    private void checkAllocated() {
        if (pixels == 0L)
            throw new IllegalStateException("Image is not allocated.");
    }

    public void close() {
        if (pixels != 0L)
            if (useStbFree)
                STBImage.nstbi_image_free(pixels);
            else
                MemoryUtil.nmemFree(pixels);
        pixels = 0L;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Format format() {
        return format;
    }

    public int getPixelRGBA(int x, int y) {
        if (format != Format.RGBA)
            throw new IllegalArgumentException(
                    String.format("getPixelRGBA only works on RGBA images; have %s", format));
        if (x > width || y > height)
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, width, height));
        checkAllocated();
        return MemoryUtil.memIntBuffer(pixels, size).get(x + y * width);
    }

    public void setPixelRGBA(int x, int y, int rgba) {
        if (format != Format.RGBA)
            throw new IllegalArgumentException(
                    String.format("setPixelRGBA only works on RGBA images; have %s", format));
        if (x > width || y > height)
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, width, height));
        checkAllocated();
        MemoryUtil.memIntBuffer(pixels, size).put(x + y * width, rgba);
    }

    public byte getLuminanceOrAlpha(int x, int y) {
        if (!format.hasLuminanceOrAlpha())
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", format));
        if (x > width || y > height)
            throw new IllegalArgumentException(
                    String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, width, height));
        return MemoryUtil.memByteBuffer(pixels, size)
                .get((x + y * width) * format.components() + format.luminanceOrAlphaOffset() / 8);
    }

    public void blendPixel(int x, int y, int color) {
        if (format != Format.RGBA)
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        int now = getPixelRGBA(x, y);
        float colorA = (color >> 24 & 0xFF) / 255.0F;
        float colorR = (color >> 16 & 0xFF) / 255.0F;
        float colorG = (color >> 8 & 0xFF) / 255.0F;
        float colorB = (color & 0xFF) / 255.0F;
        float nowA = (now >> 24 & 0xFF) / 255.0F;
        float nowR = (now >> 16 & 0xFF) / 255.0F;
        float nowG = (now >> 8 & 0xFF) / 255.0F;
        float nowB = (now & 0xFF) / 255.0F;
        float back = 1.0F - colorA;
        float destA = colorA * colorA + nowA * back;
        float destR = colorR * colorA + nowR * back;
        float destG = colorG * colorA + nowG * back;
        float destB = colorB * colorA + nowB * back;
        if (destA > 1.0F)
            destA = 1.0F;
        if (destR > 1.0F)
            destR = 1.0F;
        if (destG > 1.0F)
            destG = 1.0F;
        if (destB > 1.0F)
            destB = 1.0F;
        int overA = (int) (destA * 255.0F);
        int overR = (int) (destR * 255.0F);
        int overG = (int) (destG * 255.0F);
        int overB = (int) (destB * 255.0F);
        setPixelRGBA(x, y, overA << 24 | overR << 16 | overG << 8 | overB);
    }

    @Deprecated
    public int[] makePixelArray() {
        if (format != Format.RGBA)
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        checkAllocated();
        int[] pixels = new int[getWidth() * getHeight()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                int color = getPixelRGBA(x, y);
                int a = color >> 24 & 0xFF;
                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;
                int now = a << 24 | b << 16 | g << 8 | r;
                pixels[x + y * getWidth()] = now;
            }
        }
        return pixels;
    }

    public void upload(int level, int x, int y, boolean useMipmap) {
        upload(level, x, y, 0, 0, width, height, useMipmap);
    }

    public void upload(int level, int x, int y, int skipPixels, int skipRows, int sizeX, int sizeY, boolean useMipmap) {
        upload(level, x, y, skipPixels, skipRows, sizeX, sizeY, false, false, useMipmap);
    }

    public void upload(int level, int x, int y, int skipPixels, int skipRows, int sizeX, int sizeY, boolean linear,
                       boolean clamp, boolean useMipmap) {
        checkAllocated();
        setFilter(linear, useMipmap);
        setClamp(clamp);
        if (sizeX == getWidth()) {
            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
        } else {
            glPixelStorei(GL_UNPACK_ROW_LENGTH, getWidth());
        }
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, skipPixels);
        glPixelStorei(GL_UNPACK_SKIP_ROWS, skipRows);
        format.setUnpackPixelStoreState();
        glTexSubImage2D(GL_TEXTURE_2D, level, x, y, sizeX, sizeY, format.glFormat(), GL_UNSIGNED_BYTE, pixels);
    }

    public void downloadTexture(int level, boolean ignoreAlpha) {
        checkAllocated();
        format.setPackPixelStoreState();
        glGetTexImage(GL_TEXTURE_2D, level, format.glFormat(), GL_UNSIGNED_BYTE, pixels);
        if (ignoreAlpha && format.hasAlpha())
            for (int y = 0; y < getHeight(); y++)
                for (int x = 0; x < getWidth(); x++)
                    setPixelRGBA(x, y, getPixelRGBA(x, y) | 255 << format.alphaOffset());
    }

    public void writeToFile(String file) throws IOException {
        writeToFile(FileSystems.getDefault().getPath(file));
    }

    public void writeToFile(File file) throws IOException {
        writeToFile(file.toPath());
    }

    public void copyFromFont(STBTTFontinfo font, int codepoint, int outW, int outH, float scaleX, float scaleY,
                             float shiftX, float shiftY, int x, int y) {
        if (x < 0 || x + outW > getWidth() || y < 0 || y + outH > getHeight())
            throw new IllegalArgumentException(
                    String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", x, y, outW, outH,
                            getWidth(), getHeight()));
        if (format.components() != 1)
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        STBTruetype.nstbtt_MakeCodepointBitmapSubpixel(font.address(), pixels + x + ((long) y * getWidth()), outW, outH,
                getWidth(), scaleX, scaleY, shiftX, shiftY, codepoint);
    }

    public void writeToFile(Path path) throws IOException {
        if (!format.supportedByStb())
            throw new UnsupportedOperationException("Don't know how to write format " + format);
        checkAllocated();
        try (WritableByteChannel writableByteChannel = Files.newByteChannel(path, OPEN_OPTIONS
        )) {
            WriteCallback writeCallback = new WriteCallback(writableByteChannel);
            try {
                if (!STBImageWrite.stbi_write_png_to_func(writeCallback, 0L, getWidth(), getHeight(),
                        format.components(), MemoryUtil.memByteBuffer(pixels, size), 0))
                    throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": "
                            + STBImage.stbi_failure_reason());
            } finally {
                writeCallback.free();
            }
            writeCallback.throwIfException();
        }
    }

    public void copyFrom(Image texture) {
        if (texture.format() != format)
            throw new UnsupportedOperationException("Image formats don't match.");
        int components = format.components();
        checkAllocated();
        texture.checkAllocated();
        if (width == texture.width) {
            MemoryUtil.memCopy(texture.pixels, pixels, Math.min(size, texture.size));
        } else {
            int width = Math.min(getWidth(), texture.getWidth());
            int height = Math.min(getHeight(), texture.getHeight());
            for (int y = 0; y < height; y++) {
                int from = y * texture.getWidth() * components;
                int to = y * getWidth() * components;
                MemoryUtil.memCopy(texture.pixels + from, pixels + to, width);
            }
        }
    }

    public void fillRect(int x, int y, int sizeX, int sizeY, int color) {
        for (int nowY = y; nowY < y + sizeY; nowY++) {
            for (int nowX = x; nowX < x + sizeX; nowX++)
                setPixelRGBA(nowX, nowY, color);
        }
    }

    public void copyRect(int x, int y, int xoff, int yoff, int sizeX, int sizeY, boolean flipX, boolean flipY) {
        for (int nowY = 0; nowY < sizeY; nowY++) {
            for (int nowX = 0; nowX < sizeX; nowX++) {
                int writeX = flipX ? (sizeX - 1 - nowX) : nowX;
                int writeY = flipY ? (sizeY - 1 - nowY) : nowY;
                int color = getPixelRGBA(x + nowX, y + nowY);
                setPixelRGBA(x + xoff + writeX, y + yoff + writeY, color);
            }
        }
    }

    public void flipY() {
        checkAllocated();
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            int component = format.components();
            int lineSize = getWidth() * component;
            long addr = memoryStack.nmalloc(lineSize);
            for (int y = 0; y < getHeight() / 2; y++) {
                int swapA = y * getWidth() * component;
                int swapB = (getHeight() - 1 - y) * getWidth() * component;
                MemoryUtil.memCopy(pixels + swapA, addr, lineSize);
                MemoryUtil.memCopy(pixels + swapB, pixels + swapA, lineSize);
                MemoryUtil.memCopy(addr, pixels + swapB, lineSize);
            }
        }
    }

    public void resizeSubRectTo(int x, int y, int inputW, int inputH, Image texture) {
        checkAllocated();
        if (texture.format() != format)
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        int components = format.components();
        STBImageResize.nstbir_resize_uint8(pixels + ((x + (long) y * getWidth()) * components), inputW, inputH,
                getWidth() * components, texture.pixels, texture.getWidth(), texture.getHeight(), 0, components);
    }

    public enum Format {
        RGBA(4, GL_RGBA, true, true, true, false, true, 0, 8, 16, 255, 24, true),
        RGB(3, GL_RGB, true, true, true, false, false, 0, 8, 16, 255, 255, true),
        LUMINANCE_ALPHA(2, GL_LUMINANCE_ALPHA, false, false, false, true, true, 255, 255, 255, 0, 8, true),
        LUMINANCE(1, GL_LUMINANCE, false, false, false, true, false, 0, 0, 0, 0, 255, true);

        public final int components;

        public final int glFormat;

        public final boolean hasRed;

        public final boolean hasGreen;

        public final boolean hasBlue;

        public final boolean hasLuminance;

        public final boolean hasAlpha;

        public final int redOffset;

        public final int greenOffset;

        public final int blueOffset;

        public final int luminanceOffset;

        public final int alphaOffset;

        public final boolean supportedByStb;

        Format(int i, int i1, boolean bool, boolean bool1, boolean bool2, boolean bool3, boolean bool4, int i2, int i3,
               int i4, int i5, int i6, boolean bool5) {
            components = i;
            glFormat = i1;
            hasRed = bool;
            hasGreen = bool1;
            hasBlue = bool2;
            hasLuminance = bool3;
            hasAlpha = bool4;
            redOffset = i2;
            greenOffset = i3;
            blueOffset = i4;
            luminanceOffset = i5;
            alphaOffset = i6;
            supportedByStb = bool5;
        }

        private static Format getStbFormat(int i) {
            switch (i) {
                case 1:
                    return LUMINANCE;
                case 2:
                    return LUMINANCE_ALPHA;
                case 3:
                    return RGB;
            }
            return RGBA;
        }

        public int components() {
            return components;
        }

        public void setPackPixelStoreState() {
            glPixelStorei(GL_PACK_ALIGNMENT, components());
        }

        public void setUnpackPixelStoreState() {
            glPixelStorei(GL_UNPACK_ALIGNMENT, components());
        }

        public int glFormat() {
            return glFormat;
        }

        public boolean hasAlpha() {
            return hasAlpha;
        }

        public int alphaOffset() {
            return alphaOffset;
        }

        public boolean hasLuminanceOrAlpha() {
            return hasLuminance || hasAlpha;
        }

        public int luminanceOrAlphaOffset() {
            return hasLuminance ? luminanceOffset : alphaOffset;
        }

        public boolean supportedByStb() {
            return supportedByStb;
        }
    }

    public enum InternalGlFormat {
        RGBA(GL_RGBA), RGB(GL_RGB), LUMINANCE_ALPHA(GL_LUMINANCE_ALPHA), LUMINANCE(GL_LUMINANCE),
        INTENSITY(GL_INTENSITY);

        private final int glFormat;

        InternalGlFormat(int i) {
            glFormat = i;
        }

        public int glFormat() {
            return glFormat;
        }
    }

    static class WriteCallback extends STBIWriteCallback {

        private final WritableByteChannel output;

        private IOException exception;

        private WriteCallback(WritableByteChannel writableByteChannel) {
            output = writableByteChannel;
        }

        public void invoke(long l, long l1, int i) {
            ByteBuffer byteBuffer = getData(l1, i);
            try {
                output.write(byteBuffer);
            } catch (IOException iOexception) {
                exception = iOexception;
            }
        }

        public void throwIfException() throws IOException {
            if (exception != null)
                throw exception;
        }
    }
}
