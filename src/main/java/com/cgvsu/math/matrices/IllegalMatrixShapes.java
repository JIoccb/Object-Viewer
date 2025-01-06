package com.cgvsu.math.matrices;

public class IllegalMatrixShapes extends RuntimeException {
    public IllegalMatrixShapes(int rows, int cols, int pRows, int pCols) {
        super("Shapes of matrix must be equal to " + rows + "x" + cols + " . Provided: " + pRows + ", " + pCols + ".");
    }
}