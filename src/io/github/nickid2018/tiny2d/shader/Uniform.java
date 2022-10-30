package io.github.nickid2018.tiny2d.shader;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

@RenderThreadOnly
public class Uniform {

    public static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
    private final String name;
    private final int location;

    public Uniform(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setMatrix4f(Matrix4f matrix) {
        glUniformMatrix4fv(location, false, matrix.get(new float[16]));
    }

    public void setMatrix4f(boolean transpose, Matrix4f matrix) {
        glUniformMatrix4fv(location, transpose, matrix.get(new float[16]));
    }

    public void setFloat(float value) {
        glUniform1f(location, value);
    }

    public void set2fv(Vector2f vector) {
        glUniform2fv(location, new float[]{vector.x, vector.y});
    }

    public void set2fv(float x, float y) {
        glUniform2fv(location, new float[]{x, y});
    }

    public void set3fv(Vector3f vector) {
        glUniform3fv(location, new float[]{vector.x, vector.y, vector.z});
    }

    public void set3fv(float r, float g, float b) {
        glUniform3fv(location, new float[]{r, g, b});
    }
}
