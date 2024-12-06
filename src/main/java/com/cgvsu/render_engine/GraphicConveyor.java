package com.cgvsu.render_engine;

import com.cgvsu.math.affine_transformations.AffineTransformations;
import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;

import static com.cgvsu.math.affine_transformations.AffineTransformations.*;

public class GraphicConveyor {

    public static Matrix4D rotateScaleTranslate(Vector3D translate, double alpha, double beta, double gamma, double x, double y, double z) {
        Matrix4D RS = BinaryOperations.product(scaling(x, y, z), rotation(alpha, beta, gamma));
        return BinaryOperations.product(translation(translate.get(0), translate.get(1), translate.get(2)), RS);
    }

    public static Matrix4D lookAt(Vector3D eye, Vector3D target) throws Exception {
        return lookAt(eye, target, new Vector3D(new double[]{0, -1, 0}));
    }

    public static Matrix4D lookAt(Vector3D eye, Vector3D target, Vector3D up) throws Exception {
        Vector3D resultZ = BinaryOperations.add(target, eye, false).normalize().toVector3D(); // Z-ось (направление взгляда)
        Vector3D adjustedUp = up;

        // Проверяем, не параллелен ли up направлению взгляда
        if (BinaryOperations.cross(up, resultZ).norm() < 1e-6) {
            adjustedUp = new Vector3D(new double[]{0, 0, 1}); // Сменить "вверх" на безопасное значение
            if (BinaryOperations.cross(adjustedUp, resultZ).norm() < 1e-6) {
                adjustedUp = new Vector3D(new double[]{0, 1, 0}); // Если все еще параллельно, сменить снова
            }
        }

        Vector3D resultX = BinaryOperations.cross(adjustedUp, resultZ).normalize().toVector3D(); // X-ось
        Vector3D resultY = BinaryOperations.cross(resultZ, resultX).normalize().toVector3D(); // Y-ось

        Matrix4D trans = AffineTransformations.translation(
                -BinaryOperations.dot(resultX, eye),
                -BinaryOperations.dot(resultY, eye),
                -BinaryOperations.dot(resultZ, eye)
        );

        Matrix4D proj = new Matrix4D(new double[][]{
                {resultX.get(0), resultY.get(0), resultZ.get(0), 0},
                {resultX.get(1), resultY.get(1), resultZ.get(1), 0},
                {resultX.get(2), resultY.get(2), resultZ.get(2), 0},
                {0, 0, 0, 1}
        });

        return BinaryOperations.product(proj, trans).toMatrix4D();
    }


    public static Matrix4D perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4D result = new Matrix4D();

        float fovRadians = (float) Math.toRadians(fov); // fov в градусах -> радианы
        float tangent = (float) Math.tan(fovRadians / 2.0); // Тангенс половины угла обзора

        float yScale = 1.0F / tangent; // Масштабирование по Y
        float xScale = yScale / aspectRatio; // Масштабирование по X, учитывающее aspectRatio
        float frustumLength = farPlane - nearPlane;

        result.set(0, 0, xScale); // Масштабирование по ширине
        result.set(1, 1, yScale); // Масштабирование по высоте
        result.set(2, 2, -(farPlane + nearPlane) / frustumLength); // Z-координата
        result.set(2, 3, -2 * nearPlane * farPlane / frustumLength); // Смещение по глубине
        result.set(3, 2, -1); // W для перспективного деления
        result.set(3, 3, 0); // Перспективное деление

        return result;
    }


    /*public static Vector3D multiplyMatrix4ByVector3(final Matrix4D matrix, final Vector3D vertex) {
        final float x = (vertex.x * matrix.m00) + (vertex.y * matrix.m10) + (vertex.z * matrix.m20) + matrix.m30;
        final float y = (vertex.x * matrix.m01) + (vertex.y * matrix.m11) + (vertex.z * matrix.m21) + matrix.m31;
        final float z = (vertex.x * matrix.m02) + (vertex.y * matrix.m12) + (vertex.z * matrix.m22) + matrix.m32;
        final float w = (vertex.x * matrix.m03) + (vertex.y * matrix.m13) + (vertex.z * matrix.m23) + matrix.m33;
        return new Vector3D(x / w, y / w, z / w);
    }*/

    /*public static Vector2D vertexToPoint(final Vector3D vertex, final int width, final int height) {
        return new Vector2D(new double[]{vertex.get(0) * width + width / 2.0F, -vertex.get(1) * height + height / 2.0F});
    }*/
    public static Vector2D vertexToPoint(final Vector3D vertex, final int width, final int height) {
        return new Vector2D(new double[]{(width - 1) / 2.0D * (vertex.get(0) + 1), (height - 1) / 2.0D * (-vertex.get(1) + 1)});
    }
}
