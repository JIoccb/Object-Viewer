package com.cgvsu.math.vectors;

import matrices.Matrix;

public class Vector {
    private int length;
    private double[] data;

    Vector() {
    }

    public Vector(int length) {
        this.length = length;
        this.data = new double[length];
    }

    public Vector(double[] data) {
        this.length = data.length;
        this.data = data;
    }

    public int getLength() {
        return length;
    }

    public double[] getData() {
        return data;
    }

    public double get(int index) {
        return data[index];
    }

    public void set(int index, double value) {
        data[index] = value;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    void setLength(int length) {
        this.length = length;
    }

    /**
     * Norm of a vector
     */
    public double norm() {
        double res = 0;
        for (double i : data) {
            res += i * i;
        }
        return Math.sqrt(res);
    }

    /**
     * Converting a vector to a matrix
     */
    public Matrix toMatrix() {
        double[][] res = new double[1][];
        res[0] = data;
        return new Matrix(res);
    }

    /**
     * Vector normalization
     */
    public Vector normalize() {
        int length = getLength();
        double norm = norm();

        Vector res = new Vector(length);

        for (int i = 0; i < length; i++) {
            res.set(i, get(i) / norm);
        }
        return res;
    }

    /**
     * Scaling of vector
     *
     * @param k coefficient of scaling
     */
    public Vector scale(double k) {
        int length = getLength();
        Vector res = new Vector(length);
        for (int i = 0; i < length; i++) {
            res.set(i, get(i) * k);
        }
        return res;
    }

    public void print() {
        for (double i : data) {
            System.out.print(STR."\{i} ");
        }
    }

    /**
     * Converting to 2D vector
     */
    public Vector2D toVector2D() throws Exception {
        if (length != 2) {
            throw new Exception(STR."Length of vector must be equal to 2. Provided: \{length}.");
        }
        Vector2D res = new Vector2D();
        res.set(0, get(0));
        res.set(1, get(1));
        return res;
    }
    /**
     * Converting to 3D vector
     */
    public Vector3D toVector3D() throws Exception {
        if (length != 3) {
            throw new Exception(STR."Length of vector must be equal to 3. Provided: \{length}.");
        }
        Vector3D res = new Vector3D();
        res.set(0, get(0));
        res.set(1, get(1));
        res.set(2, get(2));
        return res;
    }
    /**
     * Converting to 4D vector
     */
    public Vector4D toVector4D() throws Exception {
        if (length != 4) {
            throw new Exception(STR."Length of vector must be equal to 4. Provided: \{length}.");
        }
        Vector4D res = new Vector4D();
        res.set(0, get(0));
        res.set(1, get(1));
        res.set(2, get(2));
        res.set(3, get(3));
        return res;
    }
    /**
     * Increasing the dimension by 1 by adding a uniform coordinate
     */
    public Vector increaseDimension() {
        int length = getLength();
        Vector res = new Vector(length + 1);
        for (int i = 0; i < length; i++) {
            res.set(i, get(i));
        }
        res.set(length, 1);
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash = 7199369 * hash + Double.hashCode(get(i));
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector vector = (Vector) o;

        int length = vector.getLength();
        if (this.length != length) {
            return false;
        }
        float eps = 1e-6F;
        for (int i = 0; i < length; i++) {
            if (Math.abs(get(i) - vector.get(i)) > eps) {
                return false;
            }
        }
        return true;
    }
    /*public Matrix transpose() {
        int length = getLength();
        Matrix res = new Matrix(length, 1);
        for (int i = 0; i < length; i++) {
            res.set(i, 0, get(i));
        }
        return res;
    }*/
}