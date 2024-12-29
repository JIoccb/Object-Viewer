package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix;
import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;
import com.cgvsu.model.Model;

import com.cgvsu.model.Polygon;
import com.cgvsu.rasterization.RasterezationOneColor;
import com.cgvsu.rasterization.Rasterization;

import com.cgvsu.rasterization.Z_Buffer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.cgvsu.render_engine.GraphicConveyor.vertexToPoint;

public class RenderEngWithTriangFill {
    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) throws Exception {

        ArrayList<Polygon> triangulatingPolygons = mesh.getTriangulatingPolygons();
    

        Image texture = new Image("D:/My/java/cg/Object-Viewer/3DModels/CaracalCube/caracal_texture.png"); //пока так, но должно быть так:
       //Image texture1 = mesh.getTexture();

        mesh.triangulatingPolygons = mesh.triangulateModel();

        if (triangulatingPolygons.isEmpty() || mesh.getVertices().isEmpty()) {
            return; // Нечего отрисовывать
        }

        Matrix4D modelMatrix = Matrix.id(4).toMatrix4D();
        Matrix4D viewMatrix = camera.getViewMatrix();
        Matrix4D projectionMatrix = camera.getProjectionMatrix();

        Matrix4D modelViewProjectionMatrix = BinaryOperations.product(projectionMatrix,
                BinaryOperations.product(viewMatrix, modelMatrix));

        Z_Buffer zBuffer = new Z_Buffer(width, height);
        int[] arrX = new int[3];
        int[] arrY = new int[3];
        double[] arrZ = new double[3];
        List<Vector2D> textureVertices = new ArrayList<>();


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
                Vector3D vertex = mesh.vertices.get(mesh.triangulatingPolygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
                Vector2D textVert = mesh.textureVertices.get(mesh.triangulatingPolygons.get(polygonInd).getTextureVertexIndices().get(vertexInPolygonInd));


                arrZ[vertexInPolygonInd] = vertex.get(2);

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
            //здесь мы вызываем ту или иную растерезацию

               //RasterezationOneColor.fillTriangle(graphicsContext, arrX, arrY, arrZ, Color.ORANGE, zBuffer);
            Rasterization.fillTriangleWithTexture(graphicsContext, arrX,arrY,arrZ, textureVertices, texture, zBuffer);

           /* for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).get(0),
                        resultPoints.get(vertexInPolygonInd - 1).get(1),
                        resultPoints.get(vertexInPolygonInd).get(0),
                        resultPoints.get(vertexInPolygonInd).get(1));
            }

            */

            /*
            насколько я понимаю, вызов растеризации треугольника с текстурой, должен быть где-то здесь
             */

            /*graphicsContext.strokeLine(
                    resultPoints.get(nVerticesInPolygon - 1).get(0),
                    resultPoints.get(nVerticesInPolygon - 1).get(1),
                    resultPoints.getFirst().get(0),
                    resultPoints.getFirst().get(1));

             */


        }
    }
}
