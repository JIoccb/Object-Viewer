package com.cgvsu.math.matrices;

public class IllegalMatrixShapesException extends RuntimeException {
    public IllegalMatrixShapesException(int rows, int cols, int pRows, int pCols) {
        super("Shapes of matrix must be equal to " + rows + "x" + cols + " . Provided: " + pRows + ", " + pCols + ".");
    }
}