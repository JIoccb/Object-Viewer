package com.cgvsu.math.vectors;

public class Vector4D extends Vector{
    public Vector4D(double[] vector) {
        if (vector.length != 4) {
            throw new IllegalArgumentException(STR."The length of the array must be equal to 4. Provides: \{vector.length}");
        }
        setLength(4);
        setData(vector);
    }

    public Vector4D() {
        setLength(4);
        setData(new double[4]);
    }
}
