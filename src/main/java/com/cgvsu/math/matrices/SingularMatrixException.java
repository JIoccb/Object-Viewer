package com.cgvsu.math.matrices;

public class SingularMatrixException extends RuntimeException {
    public SingularMatrixException() {
        super("The matrix is singular, there is no inverse");
    }
}