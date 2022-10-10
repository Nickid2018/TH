package io.github.nickid2018.tiny2d.math;

import java.util.Arrays;

public class Matrix4f {

    private final float[][] matrix = new float[4][4];

    public static final Matrix4f IDENTITY = new Matrix4f(new float[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    });

    public Matrix4f() {
        this(IDENTITY.matrix);
    }

    public Matrix4f(float[][] matrix) {
        this.matrix[0] = Arrays.copyOf(matrix[0], 4);
        this.matrix[1] = Arrays.copyOf(matrix[1], 4);
        this.matrix[2] = Arrays.copyOf(matrix[2], 4);
        this.matrix[3] = Arrays.copyOf(matrix[3], 4);
    }

    public Matrix4f multiply(Matrix4f matrix) {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 4; k++)
                    result[i][j] += this.matrix[i][k] * matrix.matrix[k][j];
        return new Matrix4f(result);
    }

    public Matrix4f translate(float x, float y, float z) {
        return multiply(translateMatrix(x, y, z));
    }

    public Matrix4f rotate(float angle, float x, float y, float z) {
        return multiply(rotateMatrix(angle, x, y, z));
    }

    public Matrix4f scale(float x, float y, float z) {
        return multiply(scaleMatrix(x, y, z));
    }

    public Matrix4f transpose() {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                result[i][j] = matrix[j][i];
        return new Matrix4f(result);
    }

    public Matrix4f inverse() {
        float[][] result = new float[4][4];
        float det = determinant();
        if (det == 0)
            return null;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                result[j][i] = (float) (Math.pow(-1, i + j) * subMatrix(i, j).determinant() / det);
        return new Matrix4f(result);
    }

    private Matrix3f subMatrix(int i, int j) {
        float[][] result = new float[3][3];
        int x = 0;
        for (int k = 0; k < 4; k++) {
            if (k == i)
                continue;
            int y = 0;
            for (int l = 0; l < 4; l++)
                if (l != j)
                    result[x][y++] = matrix[k][l];
            x++;
        }
        return new Matrix3f(result);
    }

    private float determinant() {
        float result = 0;
        for (int i = 0; i < 4; i++)
            result += matrix[0][i] * (i % 2 == 0 ? 1 : -1) * subMatrix(0, i).determinant();
        return result;
    }

    public static Matrix4f translateMatrix(float x, float y, float z) {
        Matrix4f matrix = new Matrix4f();
        matrix.matrix[0][3] = x;
        matrix.matrix[1][3] = y;
        matrix.matrix[2][3] = z;
        return matrix;
    }

    public static Matrix4f rotateMatrixXY(float angle) {
        Matrix4f matrix = new Matrix4f();
        matrix.matrix[0][0] = (float) Math.cos(angle);
        matrix.matrix[0][1] = (float) -Math.sin(angle);
        matrix.matrix[1][0] = (float) Math.sin(angle);
        matrix.matrix[1][1] = (float) Math.cos(angle);
        return matrix;
    }

    public static Matrix4f rotateMatrix(float angle, float x, float y, float z) {
        Matrix4f matrix = new Matrix4f();
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        float omc = 1 - c;
        matrix.matrix[0][0] = x * x * omc + c;
        matrix.matrix[0][1] = x * y * omc + z * s;
        matrix.matrix[0][2] = x * z * omc - y * s;
        matrix.matrix[1][0] = y * x * omc - z * s;
        matrix.matrix[1][1] = y * y * omc + c;
        matrix.matrix[1][2] = y * z * omc + x * s;
        matrix.matrix[2][0] = z * x * omc + y * s;
        matrix.matrix[2][1] = z * y * omc - x * s;
        matrix.matrix[2][2] = z * z * omc + c;
        return matrix;
    }

    public static Matrix4f scaleMatrix(float x, float y, float z) {
        Matrix4f matrix = new Matrix4f();
        matrix.matrix[0][0] = x;
        matrix.matrix[1][1] = y;
        matrix.matrix[2][2] = z;
        return matrix;
    }

    public static Matrix4f identity() {
        return IDENTITY;
    }

    public float[] getMatrix() {
        float[] result = new float[16];
        System.arraycopy(matrix[0], 0, result, 0, 4);
        System.arraycopy(matrix[1], 0, result, 4, 4);
        System.arraycopy(matrix[2], 0, result, 8, 4);
        System.arraycopy(matrix[3], 0, result, 12, 4);
        return result;
    }

    public String toString() {
        return Arrays.deepToString(matrix);
    }
}
