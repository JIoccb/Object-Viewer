package com.cgvsu.math.vectors;

public class Vector2D extends Vector {

    public Vector2D(double[] vector) {
        if (vector.length != 2) {
            throw new IllegalArgumentException(String.format("Length of vector must be equal to 2. Provided: %d.", vector.length));
        }
        setLength(2);
        setData(vector);
    }

    public Vector2D() {
        setLength(2);
        setData(new double[2]);
    }
}
