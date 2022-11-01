package io.github.nickid2018.th.gui;

import io.github.nickid2018.tiny2d.shader.ShaderProgram;
import io.github.nickid2018.tiny2d.shader.ShaderSource;
import io.github.nickid2018.tiny2d.shader.ShaderType;
import io.github.nickid2018.tiny2d.util.LazyLoadValue;

import java.io.IOException;

public class Shaders {

    public static final LazyLoadValue<ShaderProgram> TEX_COLOR = new LazyLoadValue<>(() -> {
        ShaderProgram program = new ShaderProgram();
        try {
            program.attachShader(ShaderSource.createShader(ShaderType.VERTEX, "/assets/shader/tex_color.vsh"));
            program.attachShader(ShaderSource.createShader(ShaderType.FRAGMENT, "/assets/shader/tex_color.fsh"));
            program.link();
            program.addUniform("transform");
            program.addUniform("color");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return program;
    });

    public static final LazyLoadValue<ShaderProgram> TEX_COLOR_INSTANCED = new LazyLoadValue<>(() -> {
        ShaderProgram program = new ShaderProgram();
        try {
            program.attachShader(ShaderSource.createShader(ShaderType.VERTEX, "/assets/shader/tex_color_instanced.vsh"));
            program.attachShader(ShaderSource.createShader(ShaderType.FRAGMENT, "/assets/shader/tex_color.fsh"));
            program.link();
            for (int i = 0; i < 100; i++)
                program.addUniform("transform[" + i + "]");
            program.addUniform("color");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return program;
    });
}
