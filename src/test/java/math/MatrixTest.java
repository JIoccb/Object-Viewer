package math;


import com.cgvsu.math.matrices.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatrixTest {

    Matrix matrix = new Matrix(new double[][]{
            {-10, 9, -8},
            {7, -6, 5},
            {-4, 3, -2}
    });

    @Test
    void getRows() {
        assertEquals(matrix.getRows(), 3);
    }

    @Test
    void getCols() {
        assertEquals(matrix.getCols(), 3);
    }

    @Test
    void get() {
        assertEquals(matrix.get(0, 0), -10);
    }

    @Test
    void transpose() {
        Matrix tr = new Matrix(new double[][]{
                {-10, 7, -4},
                {9, -6, 3},
                {-8, 5, -2}
        });
        assertEquals(tr, matrix.transpose());
    }

    @Test
    void scale() {
        Matrix res = new Matrix(new double[][]{
                {-20, 18, -16},
                {14, -12, 10},
                {-8, 6, -4}
        });
        assertEquals(res, matrix.scale(2));
    }

    @Test
    void gaussianElimination() {
        Matrix res = new Matrix(new double[][]{
                {-10.0, 9.0, -8.0},
                {0.0, -0.6, 1.2},
                {0.0, 0.0, 0.0}
        });
        assertEquals(res, matrix.gaussianElimination());
    }

    @Test
    void rank() {
        assertEquals(matrix.rank(), 2);
    }

    @Test
    void id() {
        Matrix tr = new Matrix(new double[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        });
        assertEquals(tr, Matrix.id(3));
    }

    @Test
    void ones() {
        Matrix tr = new Matrix(new double[][]{
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        });
        assertEquals(tr, Matrix.ones(3, 3));
    }

    @Test
    void trace() {
        assertEquals(matrix.trace(), -18);
    }

    @Test
    void removeColumn() {
        Matrix res = new Matrix(new double[][]{
                {7, -6, 5},
                {-4, 3, -2}
        });
        assertEquals(matrix.removeRow(0), res);
    }

    @Test
    void removeRow() {
        Matrix res = new Matrix(new double[][]{
                {-10, 9},
                {7, -6},
                {-4, 3}
        });
        assertEquals(res, matrix.removeColumn(2));
    }

    @Test
    void increaseDimensions() {
        Matrix res = new Matrix(new double[][]{
                {-10, 9, -8, 0},
                {7, -6, 5, 0},
                {-4, 3, -2, 0},
                {0, 0, 0, 1}
        });
        assertEquals(res, matrix.increaseDimensions());
    }


    @Test
    void det() {
        assertEquals(0, matrix.det());
    }
}