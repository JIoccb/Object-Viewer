package com.cgvsu.math.operations;

import matrices.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class BinaryOperationsTest {
    Matrix matrix = new Matrix(new double[][] {
            {-10, 9, -8},
            {7, -6, 5},
            {-4, 3, -2}
    });
    Matrix matrix1 = new Matrix(new double[][] {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
    });
    @Test
    void commutator() {
        assertEquals(new Matrix(new double[][] {
                {0.0, 0.0, -3.0},
                {0.0, 0.0, -6.0},
                {7.0, 8.0, 0.0}
        }), BinaryOperations.commutator(matrix, matrix1));
    }

    @Test
    void product() {
        assertEquals(new Matrix(new double[][] {
                {30.0, 36.0, 42.0},
                {66.0, 81.0, 96.0},
                {109.0, 134.0, 159.0}
        }), BinaryOperations.product(matrix, matrix1));
    }

    @Test
    void add() {
        assertEquals(new Matrix(new double[][] {
                {2.0, 4.0, 6.0},
                {8.0, 10.0, 12.0},
                {14.0, 16.0, 19.0}
        }), BinaryOperations.add(matrix, matrix1, true));
    }

    @Test
    void tensorProduct() {
    }

    @Test
    void concatCols() {
    }

    @Test
    void concatRows() {
    }

    @Test
    void dot() {
    }

    @Test
    void cross() {
    }
}