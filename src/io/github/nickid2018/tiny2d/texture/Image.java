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
package io.github.nickid2018.tiny2d.texture;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.User32;

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
import java.util.EnumSet;
import java.util.Set;

import static org.lwjgl.opengl.GL30.*;

public class Image implements AutoCloseable {

    static {
        STBImage.stbi_set_flip_vertically_on_load(true);
    }

    private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    private final ImageFormat format;

    private final int width;

    private final int height;

    private final boolean useStbFree;
    private final int size;
    private long pixels;

    public Image(int width, int height, boolean calloc) {
        this(ImageFormat.RGBA, width, height, calloc);
    }

    public Image(ImageFormat format, int width, int height, boolean calloc) {
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
        MemoryUtil.memSet(pixels, (byte) 0, size);
    }

    public Image(ImageFormat format, int width, int height, boolean useStbFree, long addr) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.useStbFree = useStbFree;
        pixels = addr;
        size = width * height * format.components();
    }


    public static Image read(File file) throws IOException {
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            return read(stream);
        }
    }

    public static Image read(@Nullable ImageFormat format, File file) throws IOException {
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            return read(format, stream);
        }
    }

    public static Image read(InputStream inputStream) throws IOException {
        return read(ImageFormat.RGBA, inputStream);
    }

    public static Image read(@Nullable ImageFormat format, InputStream inputStream) throws IOException {
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
        return read(ImageFormat.RGBA, byteBuffer);
    }

    public static Image read(@Nullable ImageFormat format, ByteBuffer byteBuffer) throws IOException {
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
            return new Image(format == null ? ImageFormat.getStbFormat(channel.get(0)) : format, width.get(0), height.get(0),
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

    public ImageFormat format() {
        return format;
    }

    public int getPixelRGBA(int x, int y) {
        if (format != ImageFormat.RGBA)
            throw new IllegalArgumentException(
                    String.format("getPixelRGBA only works on RGBA images; have %s", format));
        if (x > width || y > height)
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, width, height));
        checkAllocated();
        return MemoryUtil.memIntBuffer(pixels, size).get(x + y * width);
    }

    public void setPixelRGBA(int x, int y, int rgba) {
        if (format != ImageFormat.RGBA)
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
        if (format != ImageFormat.RGBA)
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
        if (format != ImageFormat.RGBA)
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

    @RenderThreadOnly
    public void upload(int level, int x, int y, boolean useMipmap) {
        upload(level, x, y, 0, 0, width, height, useMipmap);
    }

    @RenderThreadOnly
    public void upload(int level, int x, int y, int skipPixels, int skipRows, int sizeX, int sizeY, boolean useMipmap) {
        upload(level, x, y, skipPixels, skipRows, sizeX, sizeY, false, false, useMipmap);
    }

    @RenderThreadOnly
    public void upload(int level, int x, int y, int skipPixels, int skipRows, int sizeX, int sizeY, boolean linear,
                       boolean clamp, boolean useMipmap) {
        checkAllocated();
        setFilter(linear, useMipmap);
        setClamp(clamp);
        glPixelStorei(GL_UNPACK_ROW_LENGTH, sizeX == getWidth() ? 0 : getWidth());
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, skipPixels);
        glPixelStorei(GL_UNPACK_SKIP_ROWS, skipRows);
        format.setUnpackPixelStoreState();
        glTexSubImage2D(GL_TEXTURE_2D, level, x, y, sizeX, sizeY, format.glFormat(), GL_UNSIGNED_BYTE, pixels);
    }

    @RenderThreadOnly
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
