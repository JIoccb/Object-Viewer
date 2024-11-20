package com.cgvsu.math.vectors;

public class Vector2D extends Vector{
    public Vector2D(double[] vector) {
        if (vector.length != 2) {
            throw new IllegalArgumentException(STR."The length of the array must be equal to 2. Provides: \{vector.length}");
        }
        setLength(2);
        setData(vector);
    }

    public Vector2D() {
        setLength(2);
        setData(new double[2]);
    }
}
