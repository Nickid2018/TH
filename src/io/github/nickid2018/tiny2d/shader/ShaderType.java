package io.github.nickid2018.tiny2d.shader;

import org.lwjgl.opengl.GL30;

public enum ShaderType {
    VERTEX(GL30.GL_VERTEX_SHADER),
    FRAGMENT(GL30.GL_FRAGMENT_SHADER);

    public final int glType;

    ShaderType(int glType) {
        this.glType = glType;
    }
}
