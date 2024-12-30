package com.cgvsu.math.vectors;

public class Vector2D extends Vector {

    public Vector2D(double[] vector) {
        int length = vector.length;
        if (length != 2) {
            throw new IllegalVectorLengthException(length, 2);
        }
        setLength(2);
        setData(vector);
    }

    public Vector2D() {
        setLength(2);
        setData(new double[2]);
    }

    public Vector2D(double x, double y) {
        setLength(2);
        setData(new double[]{x, y});
    }
}
