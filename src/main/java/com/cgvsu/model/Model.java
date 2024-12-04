package com.cgvsu.model;

import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;

import java.util.*;

public class Model {

    public ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
    public ArrayList<Vector2D> textureVertices = new ArrayList<Vector2D>();
    public ArrayList<Polygon> polygons = new ArrayList<>();
    public ArrayList<Vector3D> normals = calculateNormals();


    public Model() throws Exception {
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
        if (determinant(vectorA, vectorB, vectorC) < 0) {
            vectorC = BinaryOperations.cross(vectorB, vectorA);
        }

// приходится делать приведение к 3-х мерному вектору, тк библиотека реализована так, что операции работают для произвольных векторов
        return vectorC.normalize().toVector3D();
    }

    public Vector3D calcNormalOfVertex(List<Vector3D> vertices) throws Exception {
        double xx = 0.0, yy = 0.0, zz = 0.0;

        for (Vector3D v : vertices) {
            xx += v.get(0);
            yy += v.get(1);
            zz += v.get(2);
        }
        double[] dataForNormal = {xx / vertices.size(), yy / vertices.size(), zz / vertices.size()};

        Vector normal = new Vector3D(dataForNormal);
        return normal.normalize().toVector3D();
    }

    private double determinant(Vector3D a, Vector3D b, Vector3D c) {
        return a.get(0) * (b.get(1) * c.get(2)) -
                a.get(1) * (b.get(0) * c.get(2) - c.get(0) * b.get(2)) +
                a.get(2) * (b.get(0) * c.get(1) - c.get(0) * b.get(1));
        //return a.x * (b.y * c.z) - a.y * (b.x * c.z - c.x * b.z) + a.z * (b.x * c.y - c.x * b.y);
    }

}
