package com.cgvsu.render_engine;

import java.util.ArrayList;

import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;
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
        Matrix4D modelMatrix = rotateScaleTranslate(new Vector3D(new double[]{0, 0, 0}), 0, 0, 0, 1, 1, 1); //TODO задать параметры
        Matrix4D viewMatrix = camera.getViewMatrix();
        Matrix4D projectionMatrix = camera.getProjectionMatrix();

        Matrix4D modelViewProjectionMatrix = modelMatrix;

        modelViewProjectionMatrix = BinaryOperations.product(projectionMatrix, BinaryOperations.product(viewMatrix,
                modelViewProjectionMatrix));

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Vector2D> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3D vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));


                Vector3D vertexMath = new Vector3D(new double[]{vertex.get(0), vertex.get(1), vertex.get(2)});

                //TODO исправить этот ужас
                Vector4D result = BinaryOperations.product(modelViewProjectionMatrix, vertexMath.increaseDimension()).toVector4D();
                double[] resultData = result.getData();
                Vector2D resultPoint = vertexToPoint(new Vector3D(new double[]{resultData[0], resultData[1], resultData[2]}), width, height);
                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).get(0),
                        resultPoints.get(vertexInPolygonInd - 1).get(1),
                        resultPoints.get(vertexInPolygonInd).get(0),
                        resultPoints.get(vertexInPolygonInd).get(1));
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).get(0),
                        resultPoints.get(nVerticesInPolygon - 1).get(1),
                        resultPoints.getFirst().get(0),
                        resultPoints.getFirst().get(1));
        }
    }
}