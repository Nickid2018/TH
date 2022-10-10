package io.github.nickid2018.tiny2d.math;

import java.util.Arrays;

public class Matrix3f {

    private final float[][] matrix = new float[3][3];

    public static final Matrix3f IDENTITY = new Matrix3f(new float[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
    });

    public Matrix3f() {
        this(IDENTITY.matrix);
    }

    public Matrix3f(float[][] matrix) {
        this.matrix[0] = Arrays.copyOf(matrix[0], 3);
        this.matrix[1] = Arrays.copyOf(matrix[1], 3);
        this.matrix[2] = Arrays.copyOf(matrix[2], 3);
    }

    public Matrix3f multiply(Matrix3f matrix) {
        float[][] result = new float[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    result[i][j] += this.matrix[i][k] * matrix.matrix[k][j];
        return new Matrix3f(result);
    }

    public Matrix3f translate(float x, float y) {
        return multiply(translateMatrix(x, y));
    }

    public Matrix3f rotate(float angle) {
        return multiply(rotateMatrix(angle));
    }

    public Matrix3f scale(float x, float y) {
        return multiply(scaleMatrix(x, y));
    }

    public Matrix3f transpose() {
        float[][] result = new float[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                result[i][j] = matrix[j][i];
        return new Matrix3f(result);
    }

    public Matrix3f inverse() {
        float[][] result = new float[3][3];
        float det = determinant();
        if (det == 0)
            return null;
        result[0][0] = (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]) / det;
        result[0][1] = (matrix[0][2] * matrix[2][1] - matrix[0][1] * matrix[2][2]) / det;
        result[0][2] = (matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1]) / det;
        result[1][0] = (matrix[1][2] * matrix[2][0] - matrix[1][0] * matrix[2][2]) / det;
        result[1][1] = (matrix[0][0] * matrix[2][2] - matrix[0][2] * matrix[2][0]) / det;
        result[1][2] = (matrix[0][2] * matrix[1][0] - matrix[0][0] * matrix[1][2]) / det;
        result[2][0] = (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]) / det;
        result[2][1] = (matrix[0][1] * matrix[2][0] - matrix[0][0] * matrix[2][1]) / det;
        result[2][2] = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]) / det;
        return new Matrix3f(result);
    }

    public float determinant() {
        return matrix[0][0] * matrix[1][1] * matrix[2][2] +
                matrix[0][1] * matrix[1][2] * matrix[2][0] +
                matrix[0][2] * matrix[1][0] * matrix[2][1] -
                matrix[0][2] * matrix[1][1] * matrix[2][0] -
                matrix[0][1] * matrix[1][0] * matrix[2][2] -
                matrix[0][0] * matrix[1][2] * matrix[2][1];
    }

    public float[][] getMatrix() {
        return matrix;
    }

    public static Matrix3f translateMatrix(float x, float y) {
        float[][] result = new float[3][3];
        result[0][0] = 1;
        result[1][1] = 1;
        result[2][2] = 1;
        result[2][0] = x;
        result[2][1] = y;
        return new Matrix3f(result);
    }

    public static Matrix3f rotateMatrix(float angle) {
        float[][] result = new float[3][3];
        result[0][0] = (float) Math.cos(angle);
        result[0][1] = (float) -Math.sin(angle);
        result[1][0] = (float) Math.sin(angle);
        result[1][1] = (float) Math.cos(angle);
        result[2][2] = 1;
        return new Matrix3f(result);
    }

    public static Matrix3f scaleMatrix(float x, float y) {
        float[][] result = new float[3][3];
        result[0][0] = x;
        result[1][1] = y;
        result[2][2] = 1;
        return new Matrix3f(result);
    }

    public static Matrix3f identity() {
        return IDENTITY;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(matrix);
    }
}
