package com.cgvsu.math.vectors;

public class IllegalVectorLengthException extends RuntimeException {
    public IllegalVectorLengthException(int length, int prov) {
        super("Length of vector must be equal to " + length + ". Provided: " + prov + ".");
    }
}
