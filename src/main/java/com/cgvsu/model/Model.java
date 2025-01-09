package com.cgvsu.model;

import com.cgvsu.math.matrices.Matrix;
import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.BinaryOperations;
import com.cgvsu.math.vectors.Vector;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;
import com.cgvsu.triangulation.Triangulation;
import javafx.scene.image.Image;

import java.util.*;

public class Model {
    private Image texture;

    private final ArrayList<Vector3D> vertices = new ArrayList<>();
    private final ArrayList<Vector2D> textureVertices = new ArrayList<>();
    private final ArrayList<Polygon> polygons = new ArrayList<>();
    private ArrayList<Polygon> triangulatingPolygons = new ArrayList<>();
    private ArrayList<Vector3D> normals;
    private ArrayList<Vector3D> polygonNormals;


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
        setPolygonNormals(temporaryNormals);

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

        for (int i = 0; i < vertices.size(); i++) {
            normals.add(calcNormalOfVertex(vertexPolygonsMap.get(i)));
        }

        return normals;
    }

    public Vector3D calcNormalOfPolygon(Polygon polygon) throws Exception {
        Vector3D vertice1, vertice2, vertice3;
        vertice1 = vertices.get(polygon.getVertexIndices().get(0));
        vertice2 = vertices.get(polygon.getVertexIndices().get(1));
        vertice3 = vertices.get(polygon.getVertexIndices().get(2));


        Vector3D vectorA = BinaryOperations.add(vertice2, vertice1, false).toVector3D();
        Vector3D vectorB = BinaryOperations.add(vertice3, vertice1, false).toVector3D();
        Vector3D vectorC = BinaryOperations.cross(vectorA, vectorB);

        Matrix matrix = new Matrix(3, 3);
        matrix = matrix.setRow(0, vectorA);
        matrix = matrix.setRow(1, vectorB);
        matrix = matrix.setRow(2, vectorC).toMatrix3D();

        if (matrix.det() < 0) vectorC = BinaryOperations.cross(vectorB, vectorA);

        return vectorC.normalize().toVector3D();
    }

    public Vector3D calcNormalOfVertex(List<Vector3D> vertices) {
        int len = vertices.size();
        double xx = 0.0, yy = 0.0, zz = 0.0;

        for (Vector3D v : vertices) {
            xx += v.get(0);
            yy += v.get(1);
            zz += v.get(2);
        }

        Vector normal = new Vector3D(xx / len, yy / len, zz / len);
        return normal.normalize().toVector3D();
    }

    public void setPolygonNormals(ArrayList<Vector3D> polygonNormals) {
        this.polygonNormals = polygonNormals;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public ArrayList<Vector3D> getVertices() {
        return vertices;
    }

    public ArrayList<Polygon> getTriangulatingPolygons() {
        return triangulatingPolygons;
    }

    public void addVertex(Vector3D vector3D) {
        vertices.add(vector3D);
    }

    public void setNormals(ArrayList<Vector3D> normals) {
        this.normals = normals;
    }

    public ArrayList<Vector3D> getNormals() {
        return normals;
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public void addNormal(Vector3D vector3D) {
        normals.add(vector3D);
    }

    public ArrayList<Vector2D> getTextureVertices() {
        return textureVertices;
    }

    public void setTriangulatingPolygons(ArrayList<Polygon> triangulatingPolygons) {
        this.triangulatingPolygons = triangulatingPolygons;
    }
}
