package com.cgvsu.render_engine;


import java.util.Vector;
import com.cgvsu.math.matrices.Matrix;

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
import java.util.Arrays;
import java.util.List;


import java.util.ArrayList;

import static com.cgvsu.render_engine.GraphicConveyor.multiplyMatrix4ByVector3;
import static com.cgvsu.render_engine.GraphicConveyor.vertexToPoint;

public class RenderEngWithTriangFill {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) throws Exception {

        ArrayList<Polygon> triangulatingPolygons = mesh.getTriangulatingPolygons();
        Image texture = mesh.getTexture();

        mesh.setTriangulatingPolygons(mesh.triangulateModel());
        mesh.setNormals(mesh.calculateNormals());

        Vector3D cameraView = BinaryOperations.add(camera.getTarget(), camera.getPosition(), false).normalize().toVector3D();

        if (triangulatingPolygons.isEmpty() || mesh.getVertices().isEmpty()) {
            return; // Нечего отрисовывать
        }

        Matrix4D modelMatrix = Matrix.id(4).toMatrix4D();
        Matrix4D viewMatrix = camera.getViewMatrix();
        Matrix4D projectionMatrix = camera.getProjectionMatrix();

        Matrix4D modelViewProjectionMatrix = BinaryOperations.product(projectionMatrix,  BinaryOperations.product(viewMatrix, modelMatrix));
        //Matrix4D modelViewProjectionMatrix = BinaryOperations.product(projectionMatrix, viewMatrix);

        Z_Buffer zBuffer = new Z_Buffer(width, height);

        int[] arrX = new int[3];
        int[] arrY = new int[3];
        double[] arrZ = new double[3];

        List<Vector2D> textureVertices = new ArrayList<>();
        List<Vector3D> normals = new ArrayList<>();

        final int nPolygons = triangulatingPolygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = triangulatingPolygons.get(polygonInd).getVertexIndices().size();

            if (nVerticesInPolygon < 2) continue; // Пропуск недопустимого полигона
            /*
            вместо всего остального снизу будем вызывать растеризацию нашего полигона
             */
            ArrayList<Vector2D> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {

                //идем по точкам полигона
                Vector3D vertex = mesh.getVertices().get(mesh.getTriangulatingPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
                normals.add(mesh.getNormals().get(mesh.getTriangulatingPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd)));
                Vector2D textVert = mesh.getTextureVertices().get(mesh.getTriangulatingPolygons().get(polygonInd).getTextureVertexIndices().get(vertexInPolygonInd));

                Vector3D vertexVecmath = new Vector3D(vertex.get(0), vertex.get(1), vertex.get(2));
                vertexVecmath =  multiplyMatrix4ByVector3(viewMatrix, vertexVecmath);
                arrZ[vertexInPolygonInd] = vertexVecmath.get(2);

                Vector4D result = BinaryOperations.product(modelViewProjectionMatrix, vertex.increaseDimension()).toVector4D();
                double w = result.get(3);

                if (w != 0) {
                    result = result.scale(1 / w).toVector4D();
                } else {
                    continue; // Если w = 0, пропускаем эту вершину (вырождение)
                }

                Vector2D resultPoint = vertexToPoint(new Vector3D(result.get(0), result.get(1), result.get(2)), width, height);
                arrX[vertexInPolygonInd] = (int) resultPoint.get(0);
                arrY[vertexInPolygonInd] = (int) resultPoint.get(1);
                textureVertices.add(textVert);
                resultPoints.add(resultPoint);


            }
            //new Vector3D(1000, 1000, 1000)
             //final Vector3D l = new Vector3D(-1, 0, 0);
             Vector3D l = new Vector3D(viewMatrix.get(0,2), viewMatrix.get(1,2), viewMatrix.get(2,2));
            FullRasterization.fillTriangle(graphicsContext, arrX, arrY, arrZ, Color.BLUE, texture, textureVertices, zBuffer,
                    true, true, normals, l);
            normals.clear();
            textureVertices.clear();
        }
    }
}
