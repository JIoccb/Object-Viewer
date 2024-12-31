package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.rasterization.FullRasterization;
import com.cgvsu.rasterization.Z_Buffer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import static com.cgvsu.render_engine.GraphicConveyor.vertexToPoint;

public class RenderEngWithTriangFill {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) throws Exception {

        // Получаем полигоны для триангуляции и текстуру
        List<Polygon> triangulatingPolygons = mesh.getTriangulatingPolygons();
        Image texture = mesh.getTexture();

        // Триангулируем модель, если нужно
        mesh.setTriangulatingPolygons(mesh.triangulateModel());

        // Если полигоны или вершины пусты, нечего отрисовывать
        if (triangulatingPolygons.isEmpty() || mesh.getVertices().isEmpty()) {
            return;
        }

        // Матрицы для камеры
        Matrix4D viewMatrix = camera.getViewMatrix();
        Matrix4D projectionMatrix = camera.getProjectionMatrix();
        Matrix4D modelViewProjectionMatrix = BinaryOperations.product(projectionMatrix, viewMatrix);

        // Инициализация Z-буфера и вспомогательных массивов
        Z_Buffer zBuffer = new Z_Buffer(width, height);
        int[] arrX = new int[3];
        int[] arrY = new int[3];
        double[] arrZ = new double[3];
        List<Vector2D> textureVertices = new ArrayList<>(3); // Мы ожидаем 3 текстурных вершины для треугольника

        // Итерация по полигонам
        for (Polygon polygon : triangulatingPolygons) {
            int nVerticesInPolygon = polygon.getVertexIndices().size();

            // Пропуск полигона с менее чем 2 вершинами
            if (nVerticesInPolygon < 2) continue;

            // Массивы для точек и текстурных координат
            textureVertices.clear(); // Очистим перед новым использованием
            ArrayList<Vector2D> resultPoints = new ArrayList<>(3);

            // Преобразуем каждую вершину полигона
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {

                // Получаем вершину из модели
                Vector3D vertex = mesh.getVertices().get(polygon.getVertexIndices().get(vertexInPolygonInd));
                Vector2D textVert = mesh.getTextureVertices().get(polygon.getTextureVertexIndices().get(vertexInPolygonInd));

                // Заполняем Z-буфер
                arrZ[vertexInPolygonInd] = vertex.get(2);

                // Преобразование вершины с использованием матрицы модели, вида и проекции
                Vector4D result = BinaryOperations.product(modelViewProjectionMatrix, vertex.increaseDimension()).toVector4D();
                double w = result.get(3);

                // Если w = 0, пропускаем вершину (вырождение)
                if (w == 0) {
                    continue;
                }

                // Нормализация и преобразование в 2D
                result = result.scale(1 / w).toVector4D();
                Vector2D resultPoint = vertexToPoint(new Vector3D(result.get(0), result.get(1), result.get(2)), width, height);

                // Добавляем результат в массивы
                arrX[vertexInPolygonInd] = (int) resultPoint.get(0);
                arrY[vertexInPolygonInd] = (int) resultPoint.get(1);
                textureVertices.add(textVert);
                resultPoints.add(resultPoint);
            }

            // Растеризация треугольника
            FullRasterization.fillTriangle(graphicsContext, arrX, arrY, arrZ, Color.BLUE, texture, textureVertices, zBuffer, false);
        }
    }
}
