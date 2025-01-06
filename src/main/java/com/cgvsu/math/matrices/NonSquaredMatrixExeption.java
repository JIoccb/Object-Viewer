package com.cgvsu.math.matrices;

public class NonSquaredMatrixExeption extends RuntimeException {
    public NonSquaredMatrixExeption(int rows, int cols) {
        super("The matrix must be square. Provided shapes: (" + rows + ", " + cols + ").");
    }
}