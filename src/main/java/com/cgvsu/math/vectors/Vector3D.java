package com.cgvsu.math.vectors;

public class Vector3D extends Vector {

    public Vector3D(double[] vector) {
        if (vector.length != 3) {
            throw new IllegalArgumentException(STR."The length of the array must be equal to 3. Provides: \{vector.length}");
        }
        setLength(3);
        setData(vector);
    }

    public Vector3D() {
        setLength(3);
        setData(new double[3]);
    }
}
