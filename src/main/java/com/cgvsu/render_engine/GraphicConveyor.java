package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;

import static com.cgvsu.math.affine_transformations.AffineTransformations.*;

public class GraphicConveyor {

    public static Matrix4D rotateScaleTranslate(Vector3D translate, double alpha, double beta, double gamma, double x, double y, double z) throws Exception {
        Matrix4D RS = BinaryOperations.product(scaling(x, y, z), rotation(alpha, beta, gamma));
        return BinaryOperations.product(translation(translate.get(0), translate.get(1), translate.get(2)), RS);
    }

    public static Matrix4D lookAt(Vector3D eye, Vector3D target) throws Exception {
        return lookAt(eye, target, new Vector3D(new double[]{0, 1, 0}));
    }

    public static Matrix4D lookAt(Vector3D eye, Vector3D target, Vector3D up) throws Exception {


        Vector3D resultZ = BinaryOperations.add(target, eye, false);
        Vector3D resultX = BinaryOperations.cross(up, resultZ);
        Vector3D resultY = BinaryOperations.cross(resultZ, resultX);


        resultX = resultX.normalize().toVector3D();
        resultY = resultY.normalize().toVector3D();
        resultZ = resultZ.normalize().toVector3D();

        Matrix4D trans = Matrix4D.id(4).toMatrix4D();
        trans.set(0,3,-BinaryOperations.dot(resultX, eye));
        trans.set(1,3,-BinaryOperations.dot(resultY, eye));
        trans.set(2,3,-BinaryOperations.dot(resultZ, eye));

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
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));

        result.set(0, 0, tangentMinusOnDegree);
        result.set(1, 1, tangentMinusOnDegree / aspectRatio);
        result.set(2, 2, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(2, 3, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        result.set(3, 2, 1);

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
        return new Vector2D(new double[]{vertex.get(0) * width + width / 2.0F, -vertex.get(1) * height + height / 2.0F});
    }
}
