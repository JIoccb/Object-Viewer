package com.cgvsu.math.vectors;

import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {
    Vector vector = new Vector(new double[]{1, 2, 3});

    @Test
    void getLength() {
        assertEquals(vector.getData(), new double[]{1, 2, 3});
    }

    @Test
    void setData() {
    }

    @Test
    void toMatrix() {
    }
}