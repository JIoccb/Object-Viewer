package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.BinaryOperations;
import com.cgvsu.math.vectors.Vector;
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

import static com.cgvsu.render_engine.GraphicConveyor.vertexToPoint;

public class RenderEngWithTriangFill {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final boolean[] flags) throws Exception {

        boolean drawWireframe = flags[0];
        boolean useTexture = flags[1];
        boolean useLighting = flags[2];

        Image texture;
        if (useTexture) {
            texture = mesh.getTexture();
        } else {
            texture = null;
        }

        ArrayList<Polygon> triangulatingPolygons = mesh.getTriangulatingPolygons();
        mesh.setTriangulatingPolygons(mesh.triangulateModel());
        mesh.setNormals(mesh.calculateNormals());

        //Vector3D cameraView = BinaryOperations.add(camera.getTarget(), camera.getPosition(), false).normalize().toVector3D();

        if (triangulatingPolygons.isEmpty() || mesh.getVertices().isEmpty()) return;

        Matrix4D viewMatrix = camera.getViewMatrix();
        Matrix4D projectionMatrix = camera.getProjectionMatrix();
        Matrix4D modelViewProjectionMatrix = BinaryOperations.product(projectionMatrix, viewMatrix);

        Z_Buffer zBuffer = new Z_Buffer(width, height);

        int[] arrX = new int[3];
        int[] arrY = new int[3];
        double[] arrZ = new double[3];

        ArrayList<Vector2D> textureVertices = new ArrayList<>(3);
        ArrayList<Vector3D> normals = new ArrayList<>();

        for (Polygon polygon : triangulatingPolygons) {
            final int nVerticesInPolygon = polygon.getVertexIndices().size();

            if (nVerticesInPolygon < 2) continue; // Пропуск недопустимого полигона

            normals.clear();
            textureVertices.clear();

            //ArrayList<Vector2D> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {

                //идем по точкам полигона
                Vector3D vertex = mesh.getVertices().get(polygon.getVertexIndices().get(vertexInPolygonInd));
                normals.add(mesh.getNormals().get(polygon.getVertexIndices().get(vertexInPolygonInd)));
                Vector2D textVert = mesh.getTextureVertices().get(polygon.getTextureVertexIndices().get(vertexInPolygonInd));

                Vector4D result = BinaryOperations.product(modelViewProjectionMatrix, vertex.increaseDimension()).toVector4D();
                double w = result.get(3);

                arrZ[vertexInPolygonInd] = result.get(2);

                if (w == 0) continue;

                result = result.scale(1 / w).toVector4D();
                Vector2D resultPoint = vertexToPoint(new Vector3D(result.get(0), result.get(1), result.get(2)), width, height);

                arrX[vertexInPolygonInd] = (int) resultPoint.get(0);
                arrY[vertexInPolygonInd] = (int) resultPoint.get(1);
                textureVertices.add(textVert);
                //resultPoints.add(resultPoint);
            }
            //new Vector3D(1000, 1000, 1000)
            // Vector3D l = new Vector3D(0, 0, 1);
            Vector3D l = new Vector3D(viewMatrix.get(0,2), viewMatrix.get(1,2), viewMatrix.get(2,2));

           // Vector3D worldLightDirection = new Vector3D(0, 0, -1); // Направление света в мировой системе координат

            //Vector3D worldLightDirection = new Vector3D(viewMatrix.get(0,2), viewMatrix.get(1,2), viewMatrix.get(2,2)); // Направление света в мировой системе координат
            //Vector4D cameraLightDirection = BinaryOperations.product(viewMatrix, worldLightDirection.increaseDimension()).normalize().toVector4D();
            //Vector3D lightDirection = new Vector3D(cameraLightDirection.get(0), cameraLightDirection.get(1), cameraLightDirection.get(2));
            FullRasterization.fillTriangle(graphicsContext, arrX, arrY, arrZ, Color.BLUE, texture, textureVertices, zBuffer,
                    drawWireframe, useLighting , normals, l);
        }
    }
}
