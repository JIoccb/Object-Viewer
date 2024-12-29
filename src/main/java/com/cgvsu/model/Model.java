package com.cgvsu.model;

import com.cgvsu.math.matrices.Matrix3D;
import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;
import com.cgvsu.triangulation.Triangulation;
import javafx.scene.image.Image;

import java.util.*;

public class Model {
   private Image texture; //= new Image("D:/My/java/cg/Object-Viewer/3DModels/CaracalCube/caracal_texture.png");


    public ArrayList<Vector3D> vertices = new ArrayList<>();
    public ArrayList<Vector2D> textureVertices = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();
    public ArrayList<Polygon> triangulatingPolygons = new ArrayList<>();
    public ArrayList<Vector3D> normals = new ArrayList<>();
/*
вызов методов для триангуляции и расчета нормалей не должен происходить в момент создания экземпляра модели
это делается по необходимости в методах
 */

    public Model() throws Exception {
      triangulatingPolygons = triangulateModel();
      normals = calculateNormals();
    }

    public Image getTexture() {
        return texture;
    }

    public void setTexture(Image texture) {
        this.texture = texture;
    }

    public ArrayList<Polygon> triangulateModel() {
        ArrayList<Polygon> ps = new ArrayList<>();
        for (Polygon p : polygons) {
            List<int[]> listWithVertexIndices = Triangulation.convexPolygonTriangulate(p.getVertexIndices());
            List<int[]> listWithTextureIndices = Triangulation.convexPolygonTriangulate(p.getTextureVertexIndices());
            for (int i = 0; i < listWithVertexIndices.size(); i++) {
                Polygon newP = getPolygon(listWithVertexIndices, i, listWithTextureIndices);
                ps.add(newP);
            }
        }

        return ps;
    }

    private static Polygon getPolygon(List<int[]> listWithVertexIndices, int i, List<int[]> listWithTextureIndices) {
        ArrayList<Integer> newVertices = new ArrayList<>();
        ArrayList<Integer> newTVertices = new ArrayList<>();
        for (int j = 0; j < listWithVertexIndices.get(i).length; j++) {
            newVertices.add(listWithVertexIndices.get(i)[j]);
            newTVertices.add(listWithTextureIndices.get(i)[j]);
        }
        Polygon newP = new Polygon();
        newP.setVertexIndices(newVertices);
        newP.setTextureVertexIndices(newTVertices);
        return newP;
    }

    public ArrayList<Vector3D> calculateNormals() throws Exception {

        ArrayList<Vector3D> temporaryNormals = new ArrayList<>();
        ArrayList<Vector3D> normals = new ArrayList<>();

        for (Polygon p : polygons) { //нормали полигонов получатся в том порядке, в каком и идут сами полигоны
            temporaryNormals.add(calcNormalOfPolygon(p));
        }

        // ключ - индекс вершины, значение - список нормалей полигонов

        Map<Integer, List<Vector3D>> vertexPolygonsMap = new HashMap<>();
        for (int j = 0; j < polygons.size(); j++) {
            List<Integer> vertexIndices = polygons.get(j).getVertexIndices();
            for (Integer index : vertexIndices) {
                if (!vertexPolygonsMap.containsKey(index)) {
                    vertexPolygonsMap.put(index, new ArrayList<>());
                }
                vertexPolygonsMap.get(index).add(temporaryNormals.get(j));
            }
        }

        for (int i = 0; i < addVertex.size(); i++) {
            normals.add(calcNormalOfVertex(vertexPolygonsMap.get(i)));
        }

        return normals;
    }

    public Vector3D calcNormalOfPolygon(Polygon polygon) throws Exception {
        Vector3D vertice1, vertice2, vertice3;
        vertice1 = addVertex.get(polygon.getVertexIndices().get(0));
        vertice2 = addVertex.get(polygon.getVertexIndices().get(1));
        vertice3 = addVertex.get(polygon.getVertexIndices().get(2));


        Vector3D vectorA = BinaryOperations.add(vertice2, vertice1, false).toVector3D();
        Vector3D vectorB = BinaryOperations.add(vertice3, vertice1, false).toVector3D();
        Vector3D vectorC = BinaryOperations.cross(vectorA, vectorB);

        Matrix3D matrix = new Matrix3D();
        matrix = matrix.setRow(0, vectorA).toMatrix3D();
        matrix = matrix.setRow(1, vectorB).toMatrix3D();
        matrix = matrix.setRow(2, vectorC).toMatrix3D();

        if (matrix.det() < 0) vectorC = BinaryOperations.cross(vectorB, vectorA);

        return vectorC.normalize().toVector3D();
    }

    public Vector3D calcNormalOfVertex(List<Vector3D> vertices) {
        double xx = 0.0, yy = 0.0, zz = 0.0;

        for (Vector3D v : vertices) {
            xx += v.get(0);
            yy += v.get(1);
            zz += v.get(2);
        }

        Vector normal = new Vector3D(xx / vertices.size(), yy / vertices.size(), zz / vertices.size());
        return normal.normalize().toVector3D();
    }

    public Model transform(Matrix4D TRS) {
        Model res = this;
        for (int i = 0; i < this.addVertex.size(); i++) {
            Vector3D vertex = this.addVertex.get(i);

            // Добавляем четвертую координату w=1 для аффинного преобразования
            Vector4D vertex4d = vertex.increaseDimension().toVector4D();

            // Умножаем вектор вершины на матрицу трансформации
            Vector4D transformed = BinaryOperations.product(TRS, vertex4d).toVector4D();

            // Создаем новый вектор из преобразованных координат (игнорируем w)
            double w = transformed.get(3);
            Vector3D transformedVertex = new Vector3D(
                    transformed.get(0),
                    transformed.get(1),
                    transformed.get(2)).scale(w).toVector3D();

            // Обновляем вершину модели
            res.addVertex.set(i, transformedVertex);
        }
        return res;
    }
}
