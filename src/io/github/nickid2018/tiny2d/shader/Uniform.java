package io.github.nickid2018.tiny2d.shader;

import io.github.nickid2018.tiny2d.math.Matrix4f;
import io.github.nickid2018.tiny2d.math.Vec2f;

import static org.lwjgl.opengl.GL30.*;

public class Uniform {

    private final String name;
    private final int location;

    public Uniform(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setMatrix4f(boolean transpose, Matrix4f matrix) {
        glUniformMatrix4fv(location, transpose, matrix.getMatrix());
    }

    public void setFloat(float value) {
        glUniform1f(location, value);
    }

    public void set2fv(Vec2f vector) {
        glUniform2fv(location, new float[]{vector.x, vector.y});
    }
}
