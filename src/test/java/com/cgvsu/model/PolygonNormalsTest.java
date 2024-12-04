package com.cgvsu.model;

import com.cgvsu.math.vectors.Vector3D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PolygonNormalsTest {
    private static ArrayList<Vector3D> temporaryNormals = new ArrayList<>();

    @BeforeAll
    static void init() throws Exception {
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
            //  p.getVertexIndices().set(0, p.getVertexIndices().get(0) - 1) ;
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
        assertEquals(temporaryNormals.get(0).get(0), -1.0);
        assertEquals(temporaryNormals.get(0).get(1), 0.0);
        assertEquals(temporaryNormals.get(0).get(2), 0.0);
    }

    @Test
    void calcPolygonNormalForCube1() {
        assertEquals(temporaryNormals.get(1).get(0), 0.0);
        assertEquals(temporaryNormals.get(1).get(1), -0.0);
        assertEquals(temporaryNormals.get(1).get(2), -1.0);
    }

    @Test
    void calcPolygonNormalForCube2() {
        assertEquals(temporaryNormals.get(2).get(0), 1.0);
        assertEquals(temporaryNormals.get(2).get(1), 0.0);
        assertEquals(temporaryNormals.get(2).get(2), 0.0);
    }

    @Test
    void calcPolygonNormalForCube3() {
        assertEquals(temporaryNormals.get(3).get(0), 0.0);
        assertEquals(temporaryNormals.get(3).get(1), 0.0);
        assertEquals(temporaryNormals.get(3).get(2), 1.0);
    }

    @Test
    void calcPolygonNormalForCube4() {
        assertEquals(temporaryNormals.get(4).get(0), -0.0);
        assertEquals(temporaryNormals.get(4).get(1), -1.0);
        assertEquals(temporaryNormals.get(4).get(2), 0.0);

    }

    @Test
    void calcPolygonNormalForCube5() {
        assertEquals(temporaryNormals.get(5).get(0), -0.0);
        assertEquals(temporaryNormals.get(5).get(1), 1.0);
        assertEquals(temporaryNormals.get(5).get(2), 0.0);
    }

}