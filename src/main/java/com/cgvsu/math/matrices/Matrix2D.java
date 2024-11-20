package com.cgvsu.math.matrices;

import vectors.Vector;

public class Matrix2D extends Matrix {

    public Matrix2D() {
        setCols(2);
        setRows(2);
        setData(new double[2][2]);
    }

    public Matrix2D(double[][] data) {
        if (data.length != 2 || data[0].length != 2) {
            throw new IllegalArgumentException(STR."The shapes of the array must be equal to 2. Provides: (\{data.length}, \{data[0].length}).");
        }
        setCols(2);
        setRows(2);
        setData(data);
    }

    /**
     * Determinant of matrix
     */
    @Override
    public double det() {
        return get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0);
    }

    /**
     * Inversion of matrix
     */
    public Matrix inverse() throws Exception {
        double det = det();
        if (det == 0) {
            throw new Exception("The matrix is singular, there is no inverse");

        } else {
            double[][] inv = {{get(1, 1), -get(0, 1)},
                    {-get(1, 0), get(1, 1)}};
            return new Matrix(inv).scale(1 / det);
        }
    }

    /**
     * Vector of eigenvalues of matrix
     */
    public Vector eigenvalues() {
        double det = det();
        double trace = trace();

        double discriminant = trace * trace - 4 * det;

        if (discriminant < 0) {
            throw new IllegalArgumentException("Matrix has complex eigenvalues.");
        }
        if (discriminant == 0) {
            return new Vector(new double[]{trace / 2});
        }
        double sqrtDiscriminant = Math.sqrt(discriminant);

        return new Vector(new double[]{(trace + sqrtDiscriminant) / 2, (trace - sqrtDiscriminant) / 2});
    }

    /** Matrix of eigenvectors of matrix*/
    /*public Matrix eigenvectors() throws Exception{
        int cols = getCols();
        int rows = getRows();
        if (cols != rows){
            throw new Exception("Matrix must be square. Provided: (" + rows + ", " + cols + ").");
        }
        Matrix eigenvectors = new Matrix();
        Vector eigenvalues = eigenvalues();
        for (int i = 0; i < eigenvalues.getLength(); i++){
            Vector res = Matrix.id(rows).scale(eigenvalues.get(i)).solveSystem();
            eigenvectors.setCol(i, res);
        }
        return eigenvectors;
    }*/
}

