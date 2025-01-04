package com.cgvsu.math.vectors;

public class Vector3D extends Vector {

    public Vector3D(double[] vector) {
        int length = vector.length;
        if (length != 3) {
            throw new IllegalVectorLengthException(length, 3);
        }
        setLength(3);
        setData(vector);
    }

    public Vector3D() {
        setLength(3);
        setData(new double[3]);
    }

    public Vector3D(double x, double y, double z) {
        setLength(3);
        setData(new double[]{x, y, z});
    }
    public Vector4D translationToVector4f() {
        return new Vector4D(get(0), get(1), get(2), 1);
    }
}
