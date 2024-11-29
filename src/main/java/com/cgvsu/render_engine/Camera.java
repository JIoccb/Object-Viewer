package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector3D;

public class Camera {

    private Vector3D position;  // Позиция камеры
    private Vector3D target;    // Точка, на которую смотрит камера
    private final float fov;          // Угол обзора
    private float aspectRatio;  // Соотношение сторон
    private final float nearPlane;    // Ближняя плоскость отсечения
    private final float farPlane;     // Дальняя плоскость отсечения

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
    }

    public void setPosition(final Vector3D position) {
        this.position = position;
    }

    public void setTarget(final Vector3D target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3D getPosition() {
        return position;
    }

    public Vector3D getTarget() {
        return target;
    }

    public void movePosition(final Vector3D translation) {
        this.position = BinaryOperations.add(position, translation, true);
    }

    public void moveTarget(final Vector3D translation) {
        this.target = BinaryOperations.add(target, translation, true);
    }

    public Matrix4D getViewMatrix() throws Exception {
        return GraphicConveyor.lookAt(position, target);
    }

    public Matrix4D getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    /**
     * Обновляет целевую точку камеры (target) на основе углов yaw и pitch.
     * @param yaw   Угол поворота по горизонтали (в градусах)
     * @param pitch Угол поворота по вертикали (в градусах)
     */
    public void updateTarget(double yaw, double pitch) {
        // Преобразование углов в радианы
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        // Вычисление новой целевой точки на основе углов
        double x = Math.cos(radPitch) * Math.sin(radYaw);
        double y = Math.sin(radPitch);
        double z = Math.cos(radPitch) * Math.cos(radYaw);

        // Установка нового значения target относительно текущей позиции
        this.target = BinaryOperations.add(
                position,
                new Vector3D(new double[]{x, y, z}),
                true
        );
    }
}
