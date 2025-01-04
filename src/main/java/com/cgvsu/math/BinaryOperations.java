package com.cgvsu.math;


import com.cgvsu.math.matrices.Matrix;
import com.cgvsu.math.matrices.Matrix2D;
import com.cgvsu.math.matrices.Matrix3D;
import com.cgvsu.math.matrices.Matrix4D;
import com.cgvsu.math.vectors.Vector;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.math.vectors.Vector4D;

public class BinaryOperations {
    /**
     * Commutator of two matrices
     *
     * @param A first matrix
     * @param B second matrix
     */
    public static Matrix commutator(Matrix A, Matrix B) {
        return add(product(A, B), product(B, A), false);
    }

    /**
     * Production of two matrices
     *
     * @param A first matrix
     * @param B second matrix
     */
    public static Matrix product(Matrix A, Matrix B) {
        int aCols = A.getCols();
        int aRows = A.getRows();
        int bCols = B.getCols();
        int bRows = B.getRows();
        if (aCols != bRows) {
            throw new IllegalArgumentException(
                    "The inner dimensions of the matrices must be equal. Provided: (" + aRows + ", " + aCols + "), (" + bRows + ", " + bCols + ")."
            );
        }
        Matrix res = new Matrix(aRows, bCols);
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bCols; j++) {
                double elem = 0;
                for (int k = 0; k < aCols; k++) {
                    elem += A.get(i, k) * B.get(k, j);
                }
                res.set(i, j, elem);
            }
        }
        return res;
    }

    /**
     * Sum or subtraction of two vectors
     *
     * @param x          first vector
     * @param y          second vector
     * @param isAddition {@code true} for addition and {@code false} for subtraction
     */
    public static Vector add(Vector x, Vector y, boolean isAddition) throws Exception {
        int xLength = x.getLength();
        int yLength = y.getLength();
        if (xLength != yLength) {
            throw new IllegalArgumentException(
                    "The dimensions of vectors must be equal. Provided: " + xLength + ", " + yLength + "."
            );
        }
        Vector res = new Vector(xLength);
        for (int i = 0; i < xLength; i++) {
            res.set(i, isAddition ? x.get(i) + y.get(i) : x.get(i) - y.get(i));
        }
        return res;
    }
    public static Vector2D add(Vector2D x, Vector2D y, boolean isAddition) {
        Vector2D res = new Vector2D();
        double x0 = x.get(0), x1 = x.get(1), y0 = y.get(0), y1 = y.get(1);
        if (isAddition) {
            res.set(0, x0 + y0);
            res.set(1, x1 + y1);
        } else {
            res.set(0, x0 - y0);
            res.set(1, x1 - y1);
        }
        return res;
    }

    public static Vector3D add(Vector3D x, Vector3D y, boolean isAddition) {
        Vector3D res = new Vector3D();
        double x0 = x.get(0), x1 = x.get(1), x2 = x.get(2), y0 = y.get(0), y1 = y.get(1), y2 = y.get(2);
        if (isAddition) {
            res.set(0, x0 + y0);
            res.set(1, x1 + y1);
            res.set(2, x2 + y2);
        } else {
            res.set(0, x0 - y0);
            res.set(1, x1 - y1);
            res.set(2, x2 - y2);
        }
        return res;
    }

    public static Vector4D add(Vector4D x, Vector4D y, boolean isAddition) {
        Vector4D res = new Vector4D();
        double x0 = x.get(0), x1 = x.get(1), x2 = x.get(2), x3 = x.get(3), y0 = y.get(0), y1 = y.get(1), y2 = y.get(2), y3 = y.get(3);
        if (isAddition) {
            res.set(0, x0 + y0);
            res.set(1, x1 + y1);
            res.set(2, x2 + y2);
            res.set(3, x3 + y3);
        } else {
            res.set(0, x0 - y0);
            res.set(1, x1 - y1);
            res.set(2, x2 - y2);
            res.set(3, x3 - y3);
        }
        return res;
    }

    public static Matrix2D product(Matrix2D A, Matrix2D B) {
        Matrix2D res = new Matrix2D();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                double elem = 0;
                for (int k = 0; k < 2; k++) {
                    elem += A.get(i, k) * B.get(k, j);
                }
                res.set(i, j, elem);
            }
        }
        return res;
    }

    public static Matrix3D product(Matrix3D A, Matrix3D B) {
        Matrix3D res = new Matrix3D();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double elem = 0;
                for (int k = 0; k < 3; k++) {
                    elem += A.get(i, k) * B.get(k, j);
                }
                res.set(i, j, elem);
            }
        }
        return res;
    }

    public static Matrix4D product(Matrix4D A, Matrix4D B) {
        Matrix4D res = new Matrix4D();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double elem = 0;
                for (int k = 0; k < 4; k++) {
                    elem += A.get(i, k) * B.get(k, j);
                }
                res.set(i, j, elem);
            }
        }
        return res;
    }

    public static Vector product(Matrix matrix, Vector vector) {
        int cols = matrix.getCols();
        int length = vector.getLength();
        if (cols != length) {
            throw new IllegalArgumentException("Length of vector must be equal to counts of columns in matrix. Provided");
        }
        int rows = matrix.getRows();
        Vector res = new Vector(length);
        for (int i = 0; i < rows; i++) {
            double elem = 0;
            for (int k = 0; k < rows; k++) {
                elem += matrix.get(i, k) * vector.get(k);
            }
            res.set(i, elem);
        }
        return res;
    }

    public static Matrix product(Vector x, Vector y) {
        int xLength = x.getLength();
        int yLength = y.getLength();
        Matrix res = new Matrix(xLength, yLength);
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {
                res.set(i, j, x.get(i) * y.get(j));
            }
        }
        return res;
    }

    /**
     * Sum or difference of two matrices
     *
     * @param A          first matrix
     * @param B          second matrix
     * @param isAddition {@code true} for addition and {@code false} for difference
     */
    public static Matrix add(Matrix A, Matrix B, boolean isAddition) {
        int aCols = A.getCols();
        int aRows = A.getRows();
        int bCols = B.getCols();
        int bRows = B.getRows();
        if (aRows != bRows || aCols != bCols) {
            throw new IllegalArgumentException(
                    "The dimensions of the matrices must be equal. Provided: (" + aRows + ", " + aCols + "), (" + bRows + ", " + bCols + ")."
            );
        }
        Matrix res = new Matrix(aRows, aCols);
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < aCols; j++) {
                res.set(i, j, isAddition ? A.get(i, j) + B.get(i, j) : A.get(i, j) - B.get(i, j));
            }
        }
        return res;
    }

    /**
     * Tensor product of two matrices
     *
     * @param A first matrix
     * @param B second matrix
     */
    public static Matrix tensorProduct(Matrix A, Matrix B) {
        int aRows = A.getCols();
        int bRows = B.getRows();
        int aCols = A.getCols();
        int bCols = B.getCols();

        Matrix res = new Matrix(aRows * bRows, aCols * bCols);
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < aCols; j++) {
                double scalar = A.get(i, j);
                for (int k = 0; k < bRows; k++) {
                    for (int l = 0; l < bCols; l++) {
                        res.set(i * bRows + k, j * bCols + l, scalar * B.get(k, l));
                    }
                }
            }
        }
        return res;
    }

    /**
     * Matrix concatenation by columns
     *
     * @param A first matrix
     * @param B second matrix
     */
    public static Matrix concatCols(Matrix A, Matrix B) {
        int aRows = A.getRows();
        int bRows = B.getRows();
        if (aRows != bRows) {
            throw new IllegalArgumentException(
                    "The count of rows in the matrices must be equal. Provided: (" + aRows + ", " + A.getCols() + "), (" + bRows + ", " + B.getCols() + ")."
            );
        }
        int aCols = A.getCols();
        int bCols = B.getCols();
        Matrix res = new Matrix(aRows, aCols + bCols);
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < aCols; j++) {
                res.set(i, j, A.get(i, j));
            }
            for (int j = 0; j < bCols; j++) {
                res.set(i, aCols + j, B.get(i, j));
            }
        }
        return res;
    }

    /**
     * Matrix concatenation by rows
     *
     * @param A first matrix
     * @param B second matrix
     */
    public static Matrix concatRows(Matrix A, Matrix B) {
        int aRows = A.getRows();
        int bRows = B.getRows();
        int aCols = A.getCols();
        int bCols = B.getCols();
        if (A.getCols() != B.getCols()) {
            throw new IllegalArgumentException(
                    "The count of columns in the matrices must be equal. Provided: (" + A.getRows() + ", " + A.getCols() + "), (" + B.getRows() + ", " + B.getCols() + ")."
            );
        }
        Matrix res = new Matrix(aRows + bRows, aCols);
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < aCols; j++) {
                res.set(i, j, A.get(i, j));
            }
        }
        for (int i = 0; i < bRows; i++) {
            for (int j = 0; j < bCols; j++) {
                res.set(aRows + i, j, B.get(i, j));
            }
        }
        return res;
    }

    /**
     * Scalar production of two vectors
     *
     * @param x first vector
     * @param y second vector
     */
    public static double dot(Vector x, Vector y) {
        int xLength = x.getLength();
        int yLength = y.getLength();
        if (xLength != yLength) {
            throw new IllegalArgumentException(
                    "The shapes of vectors must be equal. Provided: " + xLength + ", " + yLength + "."
            );
        }
        double res = 0;
        for (int i = 0; i < xLength; i++) {
            res += x.get(i) * y.get(i);
        }
        return res;
    }

    /**
     * Vector production of two vectors
     *
     * @param x first vector
     * @param y second vector
     */
    public static Vector3D cross(Vector3D x, Vector3D y) {
        Vector3D res = new Vector3D();
        res.set(0, x.get(1) * y.get(2) - x.get(2) * y.get(1));
        res.set(1, x.get(2) * y.get(0) - x.get(0) * y.get(2));
        res.set(2, x.get(0) * y.get(1) - x.get(1) * y.get(0));
        return res;
    }




}
