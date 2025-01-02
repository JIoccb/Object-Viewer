package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector3D;

public class Camera {

    private Vector3D position;
    private Vector3D target;
    private final float fov;
    private float aspectRatio;
    private final float nearPlane;
    private final float farPlane;
    private double yaw; // Угол поворота по горизонтали
    private double pitch; // Угол поворота по вертикали

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Camera(
            final Vector3D position,
            final Vector3D target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.yaw = 0;
        this.pitch = 0;
    }

    public void movePosition(final Vector3D translation) {
        this.position = BinaryOperations.add(position, translation, true);
        updateTarget();
    }

    public void rotate(double deltaYaw, double deltaPitch) {
        // Параметры сглаживания
        double rotationSmoothing = 0.1;
        this.yaw += deltaYaw * rotationSmoothing; // Сглаживаем поворот
        this.pitch += deltaPitch * rotationSmoothing;

        // Ограничение угла наклона камеры
        this.pitch = Math.max(-89, Math.min(89, this.pitch));

        updateTarget();
    }

    private void updateTarget() {
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        double x = Math.cos(radPitch) * Math.cos(radYaw);
        double y = Math.sin(radPitch);
        double z = Math.cos(radPitch) * Math.sin(radYaw);

        Vector3D direction = new Vector3D(x, y, z).normalize().toVector3D();
        this.target = BinaryOperations.add(this.position, direction, true);
    }

    public Matrix4D getViewMatrix() throws Exception {
        return GraphicConveyor.lookAt(position, target);
    }

    public Matrix4D getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Vector3D getTarget() {
        return target;
    }

    public void setTarget(Vector3D target) {
        this.target = target;
    }
}
