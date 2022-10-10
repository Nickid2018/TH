package io.github.nickid2018.tiny2d.shader;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.lwjgl.opengl.GL30.*;

public class ShaderSource {

    private final ShaderType type;
    private final String source;

    private final int id;

    @RenderThreadOnly
    public ShaderSource(ShaderType type, String source) {
        this.type = type;
        this.source = source;
        id = glCreateShader(type.glType);
        glShaderSource(id, source);
        glCompileShader(id);
        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Failed to compile shader: " + glGetShaderInfoLog(id));
    }

    @RenderThreadOnly
    public static ShaderSource createShader(ShaderType type, String location) throws IOException {
        return new ShaderSource(type, IOUtils.toString(
                Objects.requireNonNull(ShaderSource.class.getResourceAsStream(location)), StandardCharsets.UTF_8));
    }

    public ShaderType getType() {
        return type;
    }

    @RenderThreadOnly
    public void delete() {
        glDeleteShader(id);
    }

    public int getId() {
        return id;
    }
}
