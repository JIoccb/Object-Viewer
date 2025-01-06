package com.cgvsu.math.matrices;


import com.cgvsu.math.vectors.Vector;

public class Matrix2D extends Matrix {

    public Matrix2D() {
        setCols(2);
        setRows(2);
        setData(new double[2][2]);
    }

    public Matrix2D(double[][] data) {
        if (data.length != 2 || data[0].length != 2) {
            throw new IllegalArgumentException(
                    "The shapes of the array must be equal to 2. Provided: (" + data.length + ", " + data[0].length + ")."
            );
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
            throw new SingularMatrixException();

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
}


