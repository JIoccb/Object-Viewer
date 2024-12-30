package com.cgvsu.math.matrices;


import com.cgvsu.math.vectors.Vector;

public class Matrix3D extends Matrix {
    public Matrix3D() {
        setCols(3);
        setRows(3);
        setData(new double[3][3]);
    }

    public Matrix3D(double[][] data) {
        if (data.length != 3 || data[0].length != 3) {
            throw new IllegalArgumentException(
                    "The shapes of the array must be equal to 3. Provided: (" + data.length + ", " + data[0].length + ")."
            );
        }
        setCols(3);
        setRows(3);
        setData(data);
    }

    /**
     * Determinant of matrix
     */
    @Override
    public double det() {
        double a = get(0, 0);
        double b = get(0, 1);
        double c = get(0, 2);
        double d = get(1, 0);
        double e = get(1, 1);
        double f = get(1, 2);
        double g = get(2, 0);
        double h = get(2, 1);
        double i = get(2, 2);
        return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
    }

    /**
     * Inversion of matrix
     */
    public Matrix inverse() throws Exception {
        double det = det();
        if (det == 0) {
            throw new Exception("The matrix is singular, there is no inverse");
        }
        double a = get(0, 0), b = get(0, 1), c = get(0, 2);

        double d = get(1, 0), e = get(1, 1), f = get(1, 2);

        double g = get(2, 0), h = get(2, 1), i = get(2, 2);

        double[][] adjugate = {
                {e * i - f * h, c * h - b * i, b * f - c * e},
                {f * g - d * i, a * i - c * g, c * d - a * f},
                {d * h - e * g, b * g - a * h, a * e - b * d}
        };

        return new Matrix3D(adjugate).scale(1 / det);
    }

    /**
     * Vector of eigenvalues of matrix
     */
    public Vector eigenvalues() {
        double a = get(0, 0), b = get(0, 1), c = get(0, 2);

        double d = get(1, 0), e = get(1, 1), f = get(1, 2);

        double g = get(2, 0), h = get(2, 1), i = get(2, 2);

        double p = -(a + e + i);
        double q = a * e + a * i + e * i - b * f - c * d - g * h;
        double r = -(a * e * i + b * f * g + c * d * h - c * e * g - b * d * i - a * f * h);

        double Q = (p * p - 3 * q) / 9;
        double R = (2 * p * p * p - 9 * p * q + 27 * r) / 54;

        double Q3 = Q * Q * Q;
        double D = Q3 - R * R;

        if (D >= 0) {

            double theta = Math.acos(R / Math.sqrt(Q3));
            double sqrtQ = Math.sqrt(Q);

            double lambda1 = -2 * sqrtQ * Math.cos(theta / 3) - p / 3;
            double lambda2 = -2 * sqrtQ * Math.cos((theta + 2 * Math.PI) / 3) - p / 3;
            double lambda3 = -2 * sqrtQ * Math.cos((theta - 2 * Math.PI) / 3) - p / 3;

            return new Vector(new double[]{lambda1, lambda2, lambda3});
        } else {

            double sqrt_D = Math.sqrt(-D);
            double A = Math.cbrt(R + sqrt_D);
            double B = Math.cbrt(R - sqrt_D);

            double lambda = A + B - p / 3;


            return new Vector(new double[]{lambda});
        }
    }
}
