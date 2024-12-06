package com.cgvsu.model;

import com.cgvsu.math.vectors.Vector3D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PolygonNormalsTest {
    private static final ArrayList<Vector3D> temporaryNormals = new ArrayList<>();

    @BeforeAll
    static void init() {
        Model m = new Model();

        m.vertices.add(new Vector3D(new double[]{-1, -1, 1}));
        m.vertices.add(new Vector3D(new double[]{-1, 1, 1}));
        m.vertices.add(new Vector3D(new double[]{-1, -1, -1}));
        m.vertices.add(new Vector3D(new double[]{-1, 1, -1}));

        m.vertices.add(new Vector3D(new double[]{1, -1, 1}));
        m.vertices.add(new Vector3D(new double[]{1, 1, 1}));
        m.vertices.add(new Vector3D(new double[]{1, -1, -1}));
        m.vertices.add(new Vector3D(new double[]{1, 1, -1}));

        m.polygons.add(new Polygon(Arrays.asList(0, 1, 3, 2)));
        m.polygons.add(new Polygon(Arrays.asList(2, 3, 7, 6)));
        m.polygons.add(new Polygon(Arrays.asList(6, 7, 5, 6)));
        m.polygons.add(new Polygon(Arrays.asList(4, 5, 1, 0)));
        m.polygons.add(new Polygon(Arrays.asList(2, 6, 4, 0)));
        m.polygons.add(new Polygon(Arrays.asList(7, 3, 1, 5)));


        for (Polygon p : m.polygons) { //нормали полигонов получатся в том порядке, в каком и идут сами полигоны
            temporaryNormals.add(m.calcNormalOfPolygon(p));
        }

        for (int i = 0; i < m.vertices.size(); i++) {
            List<Vector3D> polygonNormalsList = new ArrayList<>();
            for (int j = 0; j < m.polygons.size(); j++) {
                if (m.polygons.get(j).getVertexIndices().contains(i)) {
                    polygonNormalsList.add(temporaryNormals.get(j));
                }
            }
            m.normals.add(m.calcNormalOfVertex(polygonNormalsList));
        }


    }

    @Test
    void calcPolygonNormalForCube0() {
        Vector3D vector3D = new Vector3D(new double[]{-1, 0, 0});
        assertEquals(temporaryNormals.getFirst(), vector3D);
    }

    @Test
    void calcPolygonNormalForCube1() {
        Vector3D vector3D = new Vector3D(new double[]{0, 0, -1});
        assertEquals(temporaryNormals.get(1), vector3D);
    }

    @Test
    void calcPolygonNormalForCube2() {
        Vector3D vector3D = new Vector3D(new double[]{1, 0, 0});
        assertEquals(temporaryNormals.get(2), vector3D);
    }

    @Test
    void calcPolygonNormalForCube3() {
        Vector3D vector3D = new Vector3D(new double[]{0, 0, 1});
        assertEquals(temporaryNormals.get(3), vector3D);
    }

    @Test
    void calcPolygonNormalForCube4() {
        Vector3D vector3D = new Vector3D(new double[]{0, -1, 0});
        assertEquals(temporaryNormals.get(4), vector3D);
    }

    @Test
    void calcPolygonNormalForCube5() {
        Vector3D vector3D = new Vector3D(new double[]{0, 1, 0});
        assertEquals(temporaryNormals.get(5), vector3D);
    }

}