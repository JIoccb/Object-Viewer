package com.cgvsu.model;

import com.cgvsu.objreader.ObjReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VertexNormalsTest {
    private static final Model m;

    static {
        try {
            m = ObjReader.read("""
                    # Blender 4.0.1\r
                    # www.blender.org\r
                    o Cube\r
                    v -1.000000 -1.000000 1.000000\r
                    v -1.000000 1.000000 1.000000\r
                    v -1.000000 -1.000000 -1.000000\r
                    v -1.000000 1.000000 -1.000000\r
                    v 1.000000 -1.000000 1.000000\r
                    v 1.000000 1.000000 1.000000\r
                    v 1.000000 -1.000000 -1.000000\r
                    v 1.000000 1.000000 -1.000000\r
                    vn -1.0000 -0.0000 -0.0000\r
                    vn -0.0000 -0.0000 -1.0000\r
                    vn 1.0000 -0.0000 -0.0000\r
                    vn -0.0000 -0.0000 1.0000\r
                    vn -0.0000 -1.0000 -0.0000\r
                    vn -0.0000 1.0000 -0.0000\r
                    vt 0.657719 0.070615\r
                    vt 0.657719 0.288779\r
                    vt 0.343923 0.288779\r
                    vt 0.343923 0.070615\r
                    vt 0.982747 0.538081\r
                    vt 0.982747 0.758131\r
                    vt 0.677420 0.758131\r
                    vt 0.677420 0.538418\r
                    vt 0.654372 0.535544\r
                    vt 0.654372 0.758215\r
                    vt 0.344166 0.758215\r
                    vt 0.344166 0.535750\r
                    vt 0.327407 0.537083\r
                    vt 0.327407 0.759706\r
                    vt 0.015414 0.759706\r
                    vt 0.015414 0.536937\r
                    vt 0.656108 0.299368\r
                    vt 0.656108 0.524271\r
                    vt 0.344171 0.524271\r
                    vt 0.344171 0.299368\r
                    vt 0.655295 0.773650\r
                    vt 0.655295 0.993820\r
                    vt 0.346502 0.993820\r
                    vt 0.346502 0.773650\r
                    s 0\r
                    f 1/1/1 2/2/1 4/3/1 3/4/1\r
                    f 3/5/2 4/6/2 8/7/2 7/8/2\r
                    f 7/9/3 8/10/3 6/11/3 5/12/3\r
                    f 5/13/4 6/14/4 2/15/4 1/16/4\r
                    f 3/17/5 7/18/5 5/19/5 1/20/5\r
                    f 8/21/6 4/22/6 2/23/6 6/24/6\r
                    """);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void init() throws Exception {
        m.setNormals(m.calculateNormals());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void findVertexTestX(int i) {
        assertEquals(58, Math.abs(Math.round(m.getNormals().get(i).get(0) * 100)));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void findVertexTestY(int i) {
        assertEquals(58, Math.abs(Math.round(m.getNormals().get(i).get(1) * 100)));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void findVertexTestZ(int i) {
        assertEquals(58, Math.abs(Math.round(m.getNormals().get(i).get(2) * 100)));
    }

}