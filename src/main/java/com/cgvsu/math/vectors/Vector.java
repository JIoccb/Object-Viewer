package com.cgvsu.math.vectors;


import com.cgvsu.math.matrices.Matrix;

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
        Vector res = new Vector(length);
        double norm = norm();
        if (!(Math.abs(norm) < 1e-6)) {
            int length = getLength();

            for (int i = 0; i < length; i++) {
                res.set(i, get(i) / norm);
            }
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
            System.out.print(i + " "); // String interpolation replaced with concatenation
        }
        System.out.println(); // Add newline for better formatting after printing
    }

    /**
     * Converts this vector to a 2D vector.
     *
     * @return A 2D vector representation of this vector.
     * @throws IllegalArgumentException if the vector length is not 2.
     */
    public Vector2D toVector2D() {
        if (length != 2) {
            throw new IllegalArgumentException(String.format("Length of vector must be equal to 2. Provided: %d.", length));
        }
        Vector2D res = new Vector2D();
        res.set(0, get(0));
        res.set(1, get(1));
        return res;
    }

    /**
     * Converts this vector to a 3D vector.
     *
     * @return A 3D vector representation of this vector.
     * @throws IllegalArgumentException if the vector length is not 3.
     */
    public Vector3D toVector3D() {
        if (length != 3) {
            throw new IllegalArgumentException(String.format("Length of vector must be equal to 3. Provided: %d.", length));
        }
        Vector3D res = new Vector3D();
        res.set(0, get(0));
        res.set(1, get(1));
        res.set(2, get(2));
        return res;
    }

    /**
     * Converts this vector to a 4D vector.
     *
     * @return A 4D vector representation of this vector.
     * @throws IllegalArgumentException if the vector length is not 4.
     */
    public Vector4D toVector4D() {
        if (length != 4) {
            throw new IllegalArgumentException(String.format("Length of vector must be equal to 4. Provided: %d.", length));
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