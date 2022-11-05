package io.github.nickid2018.th.gui;

import io.github.nickid2018.th.crash.CrashReport;
import io.github.nickid2018.th.crash.DetectedCrashError;
import io.github.nickid2018.tiny2d.shader.ShaderProgram;
import io.github.nickid2018.tiny2d.shader.ShaderSource;
import io.github.nickid2018.tiny2d.shader.ShaderType;
import io.github.nickid2018.tiny2d.shader.Uniform;
import io.github.nickid2018.tiny2d.util.LazyLoadValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            makeCrashReportAndThrow(e, "tex_color");
        }
        return program;
    });

    public static final List<Uniform> INSTANCED_UNIFORMS = new ArrayList<>();
    public static Uniform INSTANCED_COLOR;

    public static final LazyLoadValue<ShaderProgram> TEX_COLOR_INSTANCED = new LazyLoadValue<>(() -> {
        ShaderProgram program = new ShaderProgram();
        try {
            program.attachShader(ShaderSource.createShader(ShaderType.VERTEX, "/assets/shader/tex_color_instanced.vsh"));
            program.attachShader(ShaderSource.createShader(ShaderType.FRAGMENT, "/assets/shader/tex_color.fsh"));
            program.link();
            for (int i = 0; i < 100; i++)
                INSTANCED_UNIFORMS.add(program.addUniform("transform[" + i + "]"));
            INSTANCED_COLOR = program.addUniform("color");
        } catch (IOException e) {
            makeCrashReportAndThrow(e, "tex_color_instanced");
        }
        return program;
    });

    private static void makeCrashReportAndThrow(Exception e, String key) throws Error {
        CrashReport report = new CrashReport("Failed to load shader " + key, e);
        throw new DetectedCrashError(report);
    }
}
