package com.cgvsu.rasterization;

import com.cgvsu.math.vectors.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.List;

public class Rasterization {
    public static void fillTriangleWithTexture(
            final GraphicsContext graphicsContext,
            final int[] arrX,
            final int[] arrY,
            final double[] arrZ,
            final List<Vector2D> textureVert,
            // final double[][] uvCoords, // Текстурные координаты вершин { {u1, v1}, {u2, v2}, {u3, v3} }
            final Image texture, // Текстура
            final double[][] zBuffer) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        final int textureWidth = (int) texture.getWidth();
        final int textureHeight = (int) texture.getHeight();
        final PixelReader pixelReader = texture.getPixelReader();

        final double[][] uvCoords = new double[3][2];
        uvCoords[0] = textureVert.get(0).getData();
        uvCoords[1] = textureVert.get(1).getData();
        uvCoords[2] = textureVert.get(2).getData();


        sort(arrX, arrY, arrZ, uvCoords);

        int width = zBuffer.length;
        int height = zBuffer[0].length;

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
                if (z < zBuffer[x][y]) {
                    zBuffer[x][y] = z;

                    // Вычисление текстурных координат
                    double u = baryCoords[0] * uvCoords[0][0] + baryCoords[1] * uvCoords[1][0] + baryCoords[2] * uvCoords[2][0];
                    double v = baryCoords[0] * uvCoords[0][1] + baryCoords[1] * uvCoords[1][1] + baryCoords[2] * uvCoords[2][1];

                    // Нормализация и получение цвета из текстуры
                    int texX = (int) (u * (textureWidth - 1));
                    int texY = (int) (v * (textureHeight - 1));
                    Color textureColor = pixelReader.getColor(texX, texY);

                    pixelWriter.setColor(x, y, textureColor);
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
                if (z < zBuffer[x][y]) {
                    zBuffer[x][y] = z;

                    // Вычисление текстурных координат
                    double u = baryCoords[0] * uvCoords[0][0] + baryCoords[1] * uvCoords[1][0] + baryCoords[2] * uvCoords[2][0];
                    double v = baryCoords[0] * uvCoords[0][1] + baryCoords[1] * uvCoords[1][1] + baryCoords[2] * uvCoords[2][1];

                    // Нормализация и получение цвета из текстуры
                    int texX = (int) (u * (textureWidth - 1));
                    int texY = (int) (v * (textureHeight - 1));
                    Color textureColor = pixelReader.getColor(texX, texY);

                    pixelWriter.setColor(x, y, textureColor);
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


    private static void sort(int[] x, int[] y, double[] z, double[][] uvCoords) {
        if (y[0] > y[1]) swap(x, y, z, uvCoords, 0, 1);
        if (y[1] > y[2]) swap(x, y, z, uvCoords, 1, 2);
        if (y[0] > y[1]) swap(x, y, z, uvCoords, 0, 1);
    }


    private static void swap(int[] x, int[] y, double[] z, double[][] uvCoords, int i, int j) {
        // Swap x, y, and z coordinates
        int tempX = x[i];
        int tempY = y[i];
        double tempZ = z[i];
        x[i] = x[j];
        y[i] = y[j];
        z[i] = z[j];
        x[j] = tempX;
        y[j] = tempY;
        z[j] = tempZ;

        // Swap texture coordinates
        double[] tempUV = uvCoords[i];
        uvCoords[i] = uvCoords[j];
        uvCoords[j] = tempUV;
    }

}

