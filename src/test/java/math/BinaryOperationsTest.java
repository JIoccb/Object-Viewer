package math;



import com.cgvsu.math.matrices.Matrix;
import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector3D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryOperationsTest {
    Matrix matrix = new Matrix(new double[][]{
            {-10, 9, -8},
            {7, -6, 5},
            {-4, 3, -2}
    });
    Matrix matrix1 = new Matrix(new double[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
    });

    Vector3D vector3D = new Vector3D(1, 2, 3);
    Vector3D vector3D1 = new Vector3D(6, 5, 4);

    @Test
    void product() {
        Matrix expected = new Matrix(new double[][]{
                {-30.0, -39.0, -48.0},
                {18.0, 24.0, 30.0},
                {-6, -9.0, -12.0}
        });
        Matrix result = BinaryOperations.product(matrix, matrix1);
        assertEquals(expected, result);
    }

    @Test
    void commutator() {
        Matrix result = BinaryOperations.commutator(matrix, matrix1);
        Matrix expected = new Matrix(new double[][]{
                {-22, -45, -44},
                {47, 0, 49},
                {44, -51, 22}
        });
        assertEquals(result, expected);
    }

    @Test
    void add() {
        Matrix expected = new Matrix(new double[][]{
                {-9, 11, -5},
                {11, -1, 11},
                {3, 11, 7}
        });
        Matrix result = BinaryOperations.add(matrix, matrix1, true);
        assertEquals(expected, result);
    }


    @Test
    void tensorProduct() {
        Matrix expected = new Matrix(new double[][]{
                {-10.0, -20.0, -30.0, 9.0, 18.0, 27.0, -8.0, -16.0, -24.0},
                {-40.0, -50.0, -60.0, 36.0, 45.0, 54.0, -32.0, -40.0, -48.0},
                {-70.0, -80.0, -90.0, 63.0, 72.0, 81.0, -56.0, -64.0, -72.0},
                {7.0, 14.0, 21.0, -6.0, -12.0, -18.0, 5.0, 10.0, 15.0},
                {28.0, 35.0, 42.0, -24.0, -30.0, -36.0, 20.0, 25.0, 30.0},
                {49.0, 56.0, 63.0, -42.0, -48.0, -54.0, 35.0, 40.0, 45.0},
                {-4.0, -8.0, -12.0, 3.0, 6.0, 9.0, -2.0, -4.0, -6.0},
                {-16.0, -20.0, -24.0, 12.0, 15.0, 18.0, -8.0, -10.0, -12.0},
                {-28.0, -32.0, -36.0, 21.0, 24.0, 27.0, -14.0, -16.0, -18.0}
        });
        Matrix result = BinaryOperations.tensorProduct(matrix, matrix1);
        assertEquals(result, expected);
    }

    @Test
    void concatCols() {
        Matrix expected = new Matrix(new double[][]{
                {-10, 9, -8, 1, 2, 3},
                {7, -6, 5, 4, 5, 6},
                {-4, 3, -2, 7, 8, 9}
        });
        Matrix result = BinaryOperations.concatCols(matrix, matrix1);
        assertEquals(expected, result);
    }


    @Test
    void concatRows() {
        Matrix expected = new Matrix(new double[][]{
                {-10, 9, -8},
                {7, -6, 5},
                {-4, 3, -2},
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });
        Matrix result = BinaryOperations.concatRows(matrix, matrix1);
        assertEquals(result, expected);
    }

    @Test
    void dot() {
        assertEquals(28, BinaryOperations.dot(vector3D, vector3D1));
    }

    @Test
    void cross() {
        Vector3D expected = new Vector3D(-7, 14, -7);
        Vector3D result = BinaryOperations.cross(vector3D, vector3D1);
        assertEquals(expected, result);
    }
}