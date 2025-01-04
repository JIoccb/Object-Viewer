package com.cgvsu.render_engine;

import java.util.ArrayList;

import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;
import com.cgvsu.model.Polygon;
import com.cgvsu.rasterization.Z_Buffer;
import javafx.scene.canvas.GraphicsContext;

import com.cgvsu.model.Model;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) throws Exception {

        ArrayList<Polygon> polygons = mesh.getPolygons();
        ArrayList<Vector3D> vertices = mesh.getVertices();

        // Прерываем, если нет полигонов или вершин
        if (polygons.isEmpty() || vertices.isEmpty()) {
            return;
        }

        Matrix4D viewMatrix = camera.getViewMatrix();
        Matrix4D projectionMatrix = camera.getProjectionMatrix();
        Matrix4D modelViewProjectionMatrix = BinaryOperations.product(projectionMatrix, viewMatrix);

        Z_Buffer zBuffer = new Z_Buffer(width, height);

        // Итерация по полигонам
        for (Polygon polygon : polygons) {

            // Пропускаем полигоны с менее чем 2 вершинами
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            int nVerticesInPolygon = vertexIndices.size();
            if (nVerticesInPolygon < 2) {
                continue;
            }

            // Массив для хранения точек после преобразования
            Vector2D[] resultPoints = new Vector2D[nVerticesInPolygon];

            // Преобразуем вершины в 2D точки
            for (int i = 0; i < nVerticesInPolygon; i++) {
                Vector3D vertex = vertices.get(vertexIndices.get(i));

                // Умножение матрицы на вершину
                Vector4D result = BinaryOperations.product(modelViewProjectionMatrix, vertex.increaseDimension()).toVector4D();
                double w = result.get(3);

                // Пропускаем вырождение (w == 0)
                if (w == 0) {
                    continue;
                }

                // Нормализация и преобразование в 2D
                result = result.scale(1 / w).toVector4D();
                Vector2D resultPoint = vertexToPoint(new Vector3D(result.get(0), result.get(1), result.get(2)), width, height);

                // Запоминаем результат
                resultPoints[i] = resultPoint;
            }

            // Отрисовка полигона (соединение вершин)
            for (int i = 1; i < nVerticesInPolygon; i++) {
                graphicsContext.strokeLine(
                        resultPoints[i - 1].get(0),
                        resultPoints[i - 1].get(1),
                        resultPoints[i].get(0),
                        resultPoints[i].get(1)
                );
            }

            // Закрытие полигона
            graphicsContext.strokeLine(
                    resultPoints[nVerticesInPolygon - 1].get(0),
                    resultPoints[nVerticesInPolygon - 1].get(1),
                    resultPoints[0].get(0),
                    resultPoints[0].get(1)
            );
        }
    }
}
