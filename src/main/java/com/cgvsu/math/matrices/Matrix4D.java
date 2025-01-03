package com.cgvsu.math.matrices;

import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;

public class Matrix4D extends Matrix {
    public Matrix4D() {
        setCols(4);
        setRows(4);
        setData(new double[4][4]);
    }

    public Matrix4D(double[][] data) {
        if (data.length != 4 || data[0].length != 4) {
            throw new IllegalArgumentException(
                    "The shapes of the array must be equal to 4. Provided: (" + data.length + ", " + data[0].length + ")."
            );
        }
        setCols(4);
        setRows(4);
        setData(data);
    }

    /**
     * Determinant of matrix
     */
    @Override
    public double det() {
        double a = get(0, 0), b = get(0, 1), c = get(0, 2), d = get(0, 3);
        double e = get(1, 0), f = get(1, 1), g = get(1, 2), h = get(1, 3);
        double i = get(2, 0), j = get(2, 1), k = get(2, 2), l = get(2, 3);
        double m = get(3, 0), n = get(3, 1), o = get(3, 2), p = get(3, 3);
        return a * (f * (k * p - l * o) - g * (j * p - l * n) + h * (j * o - k * n))
                - b * (e * (k * p - l * o) - g * (i * p - l * m) + h * (i * o - k * m))
                + c * (e * (j * p - l * n) - f * (i * p - l * m) + h * (i * n - j * m))
                - d * (e * (j * o - k * n) - f * (i * o - k * m) + g * (i * n - j * m));
    }

    /**
     * Inversion of matrix
     */
    public Matrix inverse() throws Exception {
        double det = det();
        if (det == 0) {
            throw new Exception("The matrix is singular, there is no inverse");
        }

        double a = get(0, 0), b = get(0, 1), c = get(0, 2), d = get(0, 3);
        double e = get(1, 0), f = get(1, 1), g = get(1, 2), h = get(1, 3);
        double i = get(2, 0), j = get(2, 1), k = get(2, 2), l = get(2, 3);
        double m = get(3, 0), n = get(3, 1), o = get(3, 2), p = get(3, 3);

        double A = f * (k * p - l * o) - g * (j * p - l * n) + h * (j * o - k * n);
        double B = e * (k * p - l * o) - g * (i * p - l * m) + h * (i * o - k * m);
        double C = e * (j * p - l * n) - f * (i * p - l * m) + h * (i * n - j * m);
        double D = e * (j * o - k * n) - f * (i * o - k * m) + g * (i * n - j * m);

        double E = b * (k * p - l * o) - c * (j * p - l * n) + d * (j * o - k * n);
        double F = a * (k * p - l * o) - c * (i * p - l * m) + d * (i * o - k * m);
        double G = a * (j * p - l * n) - b * (i * p - l * m) + d * (i * n - j * m);
        double H = a * (j * o - k * n) - b * (i * o - k * m) + c * (i * n - j * m);

        double I = b * (g * p - h * o) - c * (f * p - h * n) + d * (f * o - g * n);
        double J = a * (g * p - h * o) - c * (e * p - h * m) + d * (e * o - g * m);
        double K = a * (f * p - h * n) - b * (e * p - h * m) + d * (e * n - f * m);
        double L = a * (f * o - g * n) - b * (e * o - g * m) + c * (e * n - f * m);

        double M = b * (g * l - h * k) - c * (f * l - h * j) + d * (f * k - g * j);
        double N = a * (g * l - h * k) - c * (e * l - h * i) + d * (e * k - g * i);
        double O = a * (f * l - h * j) - b * (e * l - h * i) + d * (e * j - f * i);
        double P = a * (f * k - g * j) - b * (e * k - g * i) + c * (e * j - f * i);

        double[][] inverse = {
                {A, -E, I, -M},
                {-B, F, -J, N},
                {C, -G, K, -O},
                {-D, H, -L, P}
        };

        return new Matrix4D(inverse).scale(1 / det);
    }

    public Vector4D mulVector(Vector4D vectorCol) {
        if (vectorCol == null) {
            throw new NullPointerException("Предоставленный вектор не может быть нулевым");
        }

        float[] values = new float[4];
        for (int i = 0; i < getData().length; i++) {
            values[i] = 0;
            for (int j = 0; j < getData()[0].length; j++) {
                values[i] += (float) (getData()[i][j] * vectorCol.get(j));
            }
        }
        return new Vector4D(values[0], values[1], values[2], values[3]);
    }

    public Vector3D mulVectorDivW(Vector3D vectorCol3D) {
        if (vectorCol3D == null) {
            throw new NullPointerException("Предоставленный вектор не может быть нулевым");
        }
        Vector4D vector4fCol = vectorCol3D.translationToVector4f();
        Vector4D vec = this.mulVector(vector4fCol);
        //return new Vector3D(vec.get(0) / vec.get(3), vec.get(1) / vec.get(3), vec.get(2) / vec.get(3));
        return new Vector3D(vec.get(0) , vec.get(1) , vec.get(2));

    }
}
