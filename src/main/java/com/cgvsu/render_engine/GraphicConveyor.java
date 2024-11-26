package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix;
import com.cgvsu.math.matrices.Matrix3D;
import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;

public class GraphicConveyor {

    //TODO починить типы данных
    public static Matrix3D rotation(double x, double y, double z) {
        return new Matrix3D(new double[][]{
                {Math.cos(y) * Math.cos(z), Math.cos(z) * Math.sin(y) * Math.sin(x) - Math.sin(z) * Math.cos(x),
                        Math.cos(z) * Math.sin(y) * Math.cos(x) + Math.sin(z) * Math.sin(x)},
                {Math.cos(y) * Math.sin(z), Math.sin(z) * Math.sin(y) * Math.sin(x) + Math.cos(z) * Math.cos(x),
                        Math.sin(z) * Math.sin(y) * Math.cos(x) - Math.cos(z) * Math.sin(x)},
                {-Math.sin(y), Math.cos(y) * Math.sin(x), Math.cos(y) * Math.cos(x)}
        });
    }

    public static Matrix3D scaling(double x, double y, double z) {
        return new Matrix3D(new double[][]{
                {x, 0, 0},
                {0, y, 0},
                {0, 0, z}
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
        Matrix RS = BinaryOperations.product(scaling(x, y, z), rotation(alpha, beta, gamma));
        return BinaryOperations.product(translation(translate.get(0), translate.get(1), translate.get(2)), RS.increaseDimensions());
    }

    public static Matrix4D lookAt(Vector3D eye, Vector3D target) throws Exception {
        return lookAt(eye, target, new Vector3D(new double[]{0, 1, 0}));
    }

    public static Matrix4D lookAt(Vector3D eye, Vector3D target, Vector3D up) throws Exception {


        Vector3D resultZ = BinaryOperations.add(target, eye, true);
        Vector3D resultX = BinaryOperations.cross(up, resultZ);
        Vector3D resultY = BinaryOperations.cross(resultZ, resultX);


        resultX = resultX.normalize();
        resultY = resultY.normalize();
        resultZ = resultZ.normalize();

        return new Matrix4D(new double[][]{
                {resultX.get(0), resultY.get(0), resultZ.get(0), 0},
                {resultX.get(1), resultY.get(1), resultZ.get(1), 0},
                {resultX.get(2), resultY.get(2), resultZ.get(2), 0},
                {-BinaryOperations.dot(resultX, eye), -BinaryOperations.dot(resultY, eye), -BinaryOperations.dot(resultZ, eye), 1}
        });
    }

    public static Matrix4D perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4D result = new Matrix4D();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0, 0, tangentMinusOnDegree / aspectRatio);
        result.set(1, 1, tangentMinusOnDegree);
        result.set(2, 2, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(2, 3, 1);
        result.set(3, 2, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        return result;
    }

    /*public static Vector3D multiplyMatrix4ByVector3(final Matrix4D matrix, final Vector3D vertex) {
        final float x = (vertex.x * matrix.m00) + (vertex.y * matrix.m10) + (vertex.z * matrix.m20) + matrix.m30;
        final float y = (vertex.x * matrix.m01) + (vertex.y * matrix.m11) + (vertex.z * matrix.m21) + matrix.m31;
        final float z = (vertex.x * matrix.m02) + (vertex.y * matrix.m12) + (vertex.z * matrix.m22) + matrix.m32;
        final float w = (vertex.x * matrix.m03) + (vertex.y * matrix.m13) + (vertex.z * matrix.m23) + matrix.m33;
        return new Vector3D(x / w, y / w, z / w);
    }*/

    public static Vector2D vertexToPoint(final Vector3D vertex, final int width, final int height) {
        return new Vector2D(new double[]{vertex.get(0) * width + width / 2.F, -vertex.get(1) * height + height / 2.F});
    }
}
