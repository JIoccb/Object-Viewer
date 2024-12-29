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
        if (polygons.isEmpty() || vertices.isEmpty()) {
            return; // Нечего отрисовывать
        }

        //Matrix4D modelMatrix = Matrix.id(4).toMatrix4D();
        Matrix4D viewMatrix = camera.getViewMatrix();
        Matrix4D projectionMatrix = camera.getProjectionMatrix();

        Matrix4D modelViewProjectionMatrix = BinaryOperations.product(projectionMatrix, viewMatrix);

        Z_Buffer zBuffer = new Z_Buffer(width, height);

        for (Polygon polygon : polygons) {
            final int nVerticesInPolygon = polygon.getVertexIndices().size();

            if (nVerticesInPolygon < 2) continue; // Пропуск недопустимого полигона

            ArrayList<Vector2D> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3D vertex = vertices.get(polygon.getVertexIndices().get(vertexInPolygonInd));

                Vector4D result = BinaryOperations.product(modelViewProjectionMatrix, vertex.increaseDimension()).toVector4D();
                double w = result.get(3);

                if (w != 0) {
                    result = result.scale(1 / w).toVector4D();
                } else {
                    continue; // Если w = 0, пропускаем эту вершину (вырождение)
                }
                Vector2D resultPoint = vertexToPoint(new Vector3D(result.get(0), result.get(1), result.get(2)), width, height);
                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).get(0),
                        resultPoints.get(vertexInPolygonInd - 1).get(1),
                        resultPoints.get(vertexInPolygonInd).get(0),
                        resultPoints.get(vertexInPolygonInd).get(1));
            }

            graphicsContext.strokeLine(
                    resultPoints.get(nVerticesInPolygon - 1).get(0),
                    resultPoints.get(nVerticesInPolygon - 1).get(1),
                    resultPoints.getFirst().get(0),
                    resultPoints.getFirst().get(1));
        }
    }
}
