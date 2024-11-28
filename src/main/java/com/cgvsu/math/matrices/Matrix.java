package com.cgvsu.math.matrices;


import com.cgvsu.math.vectors.Vector;



public class Matrix {
    private int rows;
    private int cols;
    private double[][] data = new double[rows][cols];

    Matrix() {
    }

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    public Matrix(double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
    }

    public int getRows() {
        return rows;
    }

    public double[][] getData() {
        return data;
    }

    public int getCols() {
        return cols;
    }

    public Vector getCol(int index) {
        Vector res = new Vector(rows);
        for (int i = 0; i < rows; i++) {
            res.set(i, data[i][index]);
        }
        return res;
    }

    public Vector getRow(int index) {
        Vector res = new Vector(cols);
        for (int i = 0; i < cols; i++) {
            res.set(i, data[index][i]);
        }
        return res;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public double get(int row, int col) {
        return data[row][col];
    }

    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    public Matrix setRow(int index, Vector vector) throws Exception {
        int rows = getRows();
        int cols = getCols();
        int length = vector.getLength();
        if (index < 0 || index > rows) {
            throw new IndexOutOfBoundsException("Row index is out of matrix bounds. Provided: " + index);
        }
        if (length != cols) {
            throw new Exception("Length of vector must be equal to count of cols in matrix. Provided: " + length);
        }
        Matrix res = new Matrix();
        for (int i = 0; i < cols; i++) {
            res.set(index, i, vector.get(i));
        }
        return res;
    }

    public Matrix setCol(int index, Vector vector) throws Exception {
        int rows = getRows();
        int cols = getCols();
        int length = vector.getLength();
        if (index < 0 || index > cols) {
            throw new IndexOutOfBoundsException("Column index is out of matrix bounds. Provided: " + index);
        }
        if (length != rows) {
            throw new Exception("Length of vector must be equal to count of rows in matrix. Provided: " + length);
        }
        Matrix res = new Matrix();
        for (int i = 0; i < rows; i++) {
            res.set(i, index, vector.get(i));
        }
        return res;
    }

    /**
     * Transposition of matrix
     */
    public Matrix transpose() {
        Matrix result = new Matrix(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[j][i] = this.data[i][j];
            }
        }
        return result;
    }

    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Scaling of matrix
     *
     * @param k coefficient of scaling
     */
    public Matrix scale(double k) {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.set(i, j, data[i][j] * k);
            }
        }
        return res;
    }


    /**
     * Upper-triangular reduction by Gaussian method
     */
    public Matrix gaussianElimination() {
        int rows = getRows();
        int cols = getCols();
        Matrix A = new Matrix(getData());
        for (int k = 0; k < rows; k++) {
            // Поиск максимального элемента в столбце k
            int maxRow = k;
            for (int i = k + 1; i < rows; i++) {
                if (Math.abs(A.get(i, k)) > Math.abs(A.get(maxRow, k))) {
                    maxRow = i;
                }
            }
            // Обмен строк k и maxRow
            for (int j = k; j < cols; j++) {
                double temp = A.get(k, j);
                A.set(k, j, A.get(maxRow, j));
                A.set(maxRow, j, temp);
            }
            // Приведение к верхнетреугольному виду
            for (int i = k + 1; i < rows; i++) {
                double factor = A.get(i, k) / A.get(k, k);
                for (int j = k; j < cols; j++) {
                    A.set(i, j, A.get(i, j) - factor * A.get(k, j));
                }
            }
        }
        return A;
    }

    /**
     * Backward motion for the Gaussian method
     */
    public Vector backSubstitution() {
        int rows = getRows();
        int cols = getCols();
        double[][] data = getData();
        Vector solution = new Vector(rows);

        for (int i = rows - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < cols - 1; j++) {
                sum += data[i][j] * solution.get(j);
            }
            solution.set(i, (data[i][cols - 1] - sum) / data[i][i]);
        }

        return solution;
    }

    /**
     * Solution of the system of equations
     */
    public Vector solveSystem() throws Exception {
        int cols = getCols();
        int rank = rank();
        if (removeColumn(cols - 1).rank() < rank) {
            throw new Exception("The system of equations has no solutions");
        } else {
            if (rank != cols - 1) {
                throw new Exception("The system of equations has many solutions");
            } else {
                return gaussianElimination().backSubstitution();
            }
        }
    }

    /**
     * Rank of matrix
     */
    public int rank() {
        Matrix upperTriangular = gaussianElimination();
        int rank = 0;

        for (int i = 0; i < upperTriangular.getRows(); i++) {
            boolean isZeroRow = true;
            for (int j = 0; j < upperTriangular.getCols(); j++) {
                if (Math.abs(upperTriangular.get(i, j)) > 0) {
                    isZeroRow = false;
                    break;
                }
            }
            if (!isZeroRow) {
                rank++;
            }
        }
        return rank;
    }

    /**
     * Creating the identity matrix
     *
     * @param size size of the matrix
     */
    public static Matrix id(int size) {
        Matrix I = new Matrix(size, size);
        for (int i = 0; i < size; i++) {
            I.set(i, i, 1);
        }
        return I;
    }

    /**
     * Creating a matrix of ones
     *
     * @param rows count of rows
     * @param cols count of columns
     */
    public static Matrix ones(int rows, int cols) {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.set(i, j, 1);
            }
        }
        return res;
    }

    /**
     * Trace of the matrix
     */
    public double trace() {
        int rows = getRows();
        int cols = getCols();

        if (rows != cols) {
            throw new IllegalArgumentException("The matrix must be square. Provided shapes: (" + rows + ", " + cols + ").");
        }
        double[][] data = getData();

        double res = 0;
        for (int i = 0; i < rows; i++) {
            res += data[i][i];
        }
        return res;
    }


    /**
     * Deleting a col of matrix
     *
     * @param col index of col
     */
    public Matrix removeColumn(int col) {
        int rows = getRows();
        int cols = getCols();


        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index is out of matrix bounds");
        }
        double[][] data = getData();
        Matrix newMatrix = new Matrix(rows, cols - 1);

        for (int i = 0; i < rows; i++) {
            int newCol = 0;
            for (int j = 0; j < cols; j++) {
                if (j != col) {
                    newMatrix.set(i, newCol, data[i][j]);
                    newCol++;
                }
            }
        }

        return newMatrix;
    }

    /**
     * Deleting a row of matrix
     *
     * @param row index of row
     */
    public Matrix removeRow(int row) {
        int rows = getRows();
        int cols = getCols();


        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Column index is out of matrix bounds");
        }
        double[][] data = getData();
        Matrix newMatrix = new Matrix(rows - 1, cols);

        int newRow = 0;
        for (int i = 0; i < rows; i++) {
            if (i != row) {
                for (int j = 0; j < cols; j++) {
                    newMatrix.set(newRow, j, data[i][j]);
                }
                newRow++;
            }
        }

        return newMatrix;
    }

    /**
     * Increasing the dimensions by 1 by adding a uniform coordinate
     */
    public Matrix increaseDimensions() {
        int rows = getRows();
        int cols = getCols();
        Matrix res = new Matrix(rows + 1, cols + 1);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.set(i, j, get(i, j));
            }
        }
        res.set(rows, cols, 1);
        return res;
    }
    public Matrix4D toMatrix4D() throws Exception {
        if (getRows() != 4 || getCols() != 4) {
            throw new Exception("Shapes of matrix must be equal to 4x4. Provided: " + getRows() + ", " + getCols() + ".");
        }
        return new Matrix4D(getData());
    }

    public Matrix3D toMatrix3D() throws Exception {
        if (getRows() != 3 || getCols() != 3) {
            throw new Exception("Shapes of matrix must be equal to 3x3. Provided: " + getRows() + ", " + getCols() + ".");
        }
        return new Matrix3D(getData());
    }

    public Matrix2D toMatrix2D() throws Exception {
        if (getRows() != 2 || getCols() != 2) {
            throw new Exception("Shapes of matrix must be equal to 2x2. Provided: " + getRows() + ", " + getCols() + ".");
        }
        return new Matrix2D(getData());
    }

    /**
     * Converting a matrix to a vector
     */
    public Vector toVector() {
        int rows = getRows();

        if (rows != 1) {
            throw new IllegalArgumentException("Matrix must have 1 row. Provided: " + rows + ".");
        }
        return getRow(0);
    }

    /**
     * Determinant of matrix
     */
    public double det() {
        int cols = getCols();
        int rows = getRows();
        if (cols != rows) {
            throw new IllegalArgumentException("The matrix must be square. Provided shapes: (" + rows + ", " + cols + ").");
        }
        double res = 1;
        Matrix triang = this.gaussianElimination();
        for (int i = 0; i < cols; i++) {
            res *= triang.get(i, i);
        }
        return res;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                hash = 7199369 * hash + Double.hashCode(get(i, j));
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Matrix matrix = (Matrix) o;
        int cols = matrix.cols;
        int rows = matrix.rows;
        if (this.rows != rows || this.cols != cols) return false;

        float eps = 1e-6F;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (Math.abs(get(i, j) - matrix.get(i, j)) > eps) return false;
            }
        }
        return true;
    }

}
