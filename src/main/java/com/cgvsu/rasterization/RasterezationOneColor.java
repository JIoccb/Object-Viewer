package com.cgvsu.rasterization;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class RasterezationOneColor {
    private static final double EPS = 1e-6;
    public static void fillTriangle(
            final GraphicsContext graphicsContext,
            final int[] arrX,
            final int[] arrY,
            final double[] arrZ,
            final Color color,
           // final double[][] zBuffer
            final Z_Buffer zBuffer) {



        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        sort(arrX, arrY, arrZ);
        int width = zBuffer.getWidth();
        int height = zBuffer.getHeight();
        // Верхняя часть треугольника
        for (int y = arrY[1]; y <= arrY[2]; y++) {
            final int x1 = (arrY[2] - arrY[1] == 0) ? arrX[1] :
                    (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
            final int x2 = (arrY[0] - arrY[2] == 0) ? arrX[2] :
                    (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                if (x < 0 || x >= width || y < 0 || y >= height) continue; // Проверка границ
                double[] baryCoords = calculateBarycentricCoordinates(x, y, arrX, arrY);
                if (baryCoords == null) continue;
                double z = baryCoords[0] * arrZ[0] + baryCoords[1] * arrZ[1] + baryCoords[2] * arrZ[2];
                if (z < zBuffer.get(x, y) || Math.abs(z - zBuffer.get(x, y)) < EPS) {
                    zBuffer.set(x, y, z);
                    pixelWriter.setColor(x, y, color);
                }

            }
        }
        // Нижняя часть треугольника
        for (int y = arrY[1]; y >= arrY[0]; y--) {
            final int x1 = (arrY[1] - arrY[0] == 0) ? arrX[0] :
                    (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
            final int x2 = (arrY[0] - arrY[2] == 0) ? arrX[2] :
                    (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                if (x < 0 || x >= width || y < 0 || y >= height) continue; // Проверка границ
                double[] baryCoords = calculateBarycentricCoordinates(x, y, arrX, arrY);
                if (baryCoords == null) continue;
                double z = baryCoords[0] * arrZ[0] + baryCoords[1] * arrZ[1] + baryCoords[2] * arrZ[2];
                if (z < zBuffer.get(x, y) || Math.abs(z - zBuffer.get(x, y)) < EPS) {
                    zBuffer.set(x, y, z);
                    pixelWriter.setColor(x, y, color);
                }

            }
        }
    }
    private static double[] calculateBarycentricCoordinates(int x, int y, int[] xPoints, int[] yPoints) {
        double denominator = (yPoints[1] - yPoints[2]) * (xPoints[0] - xPoints[2]) + (xPoints[2] - xPoints[1]) * (yPoints[0] - yPoints[2]);
        if (denominator == 0) return null; // Предотвращение деления на ноль
        double alpha = ((yPoints[1] - yPoints[2]) * (x - xPoints[2]) + (xPoints[2] - xPoints[1]) * (y - yPoints[2])) / denominator;
        double beta = ((yPoints[2] - yPoints[0]) * (x - xPoints[2]) + (xPoints[0] - xPoints[2]) * (y - yPoints[2])) / denominator;
        double gamma = 1 - alpha - beta;
        return (alpha >= 0 && beta >= 0 && gamma >= 0) ? new double[]{alpha, beta, gamma} : null;
    }
    private static void sort(int[] x, int[] y, double[] z) {
        if (y[0] > y[1]) swap(x, y, z, 0, 1);
        if (y[1] > y[2]) swap(x, y, z, 1, 2);
        if (y[0] > y[1]) swap(x, y, z, 0, 1);
    }
    private static void swap(int[] x, int[] y, double[] z, int i, int j) {
        int tempX = x[i];
        int tempY = y[i];
        double tempZ = z[i];
        x[i] = x[j];
        y[i] = y[j];
        z[i] = z[j];
        x[j] = tempX;
        y[j] = tempY;
        z[j] = tempZ;
    }
}
