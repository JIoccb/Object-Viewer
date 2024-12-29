package com.cgvsu.math.vectors;

public class Vector4D extends Vector {
    public Vector4D(double[] vector) {
        if (vector.length != 4) {
            throw new IllegalArgumentException(String.format("Length of vector must be equal to 4. Provided: %d.", vector.length));
        }
        setLength(4);
        setData(vector);
    }

    public Vector4D() {
        setLength(4);
        setData(new double[4]);
    }

    public Vector4D(double x, double y, double z, double w) {
        setLength(4);
        setData(new double[]{x, y, z, w});
    }
}
