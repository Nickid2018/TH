package io.github.nickid2018.tiny2d.shader;

import io.github.nickid2018.tiny2d.RenderThreadOnly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {

    private final int program;
    private final Map<String, Uniform> uniforms = new HashMap<>();

    @RenderThreadOnly
    public ShaderProgram() {
        program = glCreateProgram();
    }

    @RenderThreadOnly
    public ShaderProgram attachShader(ShaderSource source) {
        glAttachShader(program, source.getId());
        source.delete();
        return this;
    }

    @RenderThreadOnly
    public ShaderProgram link() {
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Failed to link shader program: " + glGetProgramInfoLog(program));
        return this;
    }

    @RenderThreadOnly
    public Uniform addUniform(String name) {
        Uniform uniform = new Uniform(name, glGetUniformLocation(program, name));
        uniforms.put(name, uniform);
        return uniform;
    }

    public Uniform getUniform(String name) {
        return uniforms.get(name);
    }

    @RenderThreadOnly
    public void use() {
        glUseProgram(program);
    }

    private static final Map<String, ShaderProgram> DEFAULT_SHADERS = new HashMap<>();

    public static ShaderProgram getDefaultShader(String name) {
        return DEFAULT_SHADERS.computeIfAbsent(name, s -> {
            ShaderProgram program = new ShaderProgram();
            try {
                program.attachShader(ShaderSource.createShader(ShaderType.VERTEX, "/assets/shader/" + name + ".vsh"));
                program.attachShader(ShaderSource.createShader(ShaderType.FRAGMENT, "/assets/shader/" + name + ".fsh"));
                program.link();
                return program;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
