package io.github.nickid2018.tiny2d.texture;

import static org.lwjgl.opengl.GL11.*;

public enum InternalGLFormat {
    RGBA(GL_RGBA), RGB(GL_RGB), LUMINANCE_ALPHA(GL_LUMINANCE_ALPHA), LUMINANCE(GL_LUMINANCE),
    INTENSITY(GL_INTENSITY);

    private final int glFormat;

    InternalGLFormat(int i) {
        glFormat = i;
    }

    public int glFormat() {
        return glFormat;
    }
}
