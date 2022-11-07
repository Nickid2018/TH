package io.github.nickid2018.tiny2d.texture;

import static org.lwjgl.opengl.GL11.*;

public enum ImageFormat {
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

    ImageFormat(int i, int i1, boolean bool, boolean bool1, boolean bool2, boolean bool3, boolean bool4, int i2, int i3,
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

    public static ImageFormat getStbFormat(int i) {
        return switch (i) {
            case 1 -> LUMINANCE;
            case 2 -> LUMINANCE_ALPHA;
            case 3 -> RGB;
            default -> RGBA;
        };
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
