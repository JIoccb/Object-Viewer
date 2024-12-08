package com.cgvsu.math.affine_transformations;

import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector3D;

public class AffineTransformations {
    public static Matrix4D rotation(double x, double y, double z) {
        return new Matrix4D(new double[][]{
                {Math.cos(y) * Math.cos(z), Math.cos(z) * Math.sin(y) * Math.sin(x) - Math.sin(z) * Math.cos(x),
                        Math.cos(z) * Math.sin(y) * Math.cos(x) + Math.sin(z) * Math.sin(x), 0},
                {Math.cos(y) * Math.sin(z), Math.sin(z) * Math.sin(y) * Math.sin(x) + Math.cos(z) * Math.cos(x),
                        Math.sin(z) * Math.sin(y) * Math.cos(x) - Math.cos(z) * Math.sin(x), 0},
                {-Math.sin(y), Math.cos(y) * Math.sin(x), Math.cos(y) * Math.cos(x), 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4D scaling(double x, double y, double z) {
        return new Matrix4D(new double[][]{
                {x, 0, 0, 0},
                {0, y, 0, 0},
                {0, 0, z, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4D translation(double x, double y, double z) {
        return new Matrix4D(new double[][]{
                {1, 0, 0, -x},
                {0, 1, 0, -y},
                {0, 0, 1, -z},
                {0, 0, 0, 1},
        });
    }
    public static Matrix4D rotateScaleTranslate(Vector3D translate, double alpha, double beta, double gamma, double x, double y, double z) {
        Matrix4D RS = BinaryOperations.product(scaling(x, y, z), rotation(alpha, beta, gamma));
        return BinaryOperations.product(translation(translate.get(0), translate.get(1), translate.get(2)), RS);
    }
}
