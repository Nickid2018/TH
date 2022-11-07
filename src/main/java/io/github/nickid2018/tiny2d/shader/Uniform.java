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

    private final float[] floatBuffer2 = new float[2];
    private final float[] floatBuffer3 = new float[3];
    private final float[] floatBuffer16 = new float[16];

    public Uniform(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setMatrix4f(Matrix4f matrix) {
        glUniformMatrix4fv(location, false, matrix.get(floatBuffer16));
    }

    public void setMatrix4f(boolean transpose, Matrix4f matrix) {
        glUniformMatrix4fv(location, transpose, matrix.get(floatBuffer16));
    }

    public void setFloat(float value) {
        glUniform1f(location, value);
    }

    public void set2fv(Vector2f vector) {
        floatBuffer2[0] = vector.x;
        floatBuffer2[1] = vector.y;
        glUniform2fv(location, floatBuffer2);
    }

    public void set2fv(float x, float y) {
        floatBuffer2[0] = x;
        floatBuffer2[1] = y;
        glUniform2fv(location, floatBuffer2);
    }

    public void set3fv(Vector3f vector) {
        floatBuffer3[0] = vector.x;
        floatBuffer3[1] = vector.y;
        floatBuffer3[2] = vector.z;
        glUniform3fv(location, floatBuffer3);
    }

    public void set3fv(float r, float g, float b) {
        floatBuffer3[0] = r;
        floatBuffer3[1] = g;
        floatBuffer3[2] = b;
        glUniform3fv(location, floatBuffer3);
    }
}
