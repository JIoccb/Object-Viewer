package com.cgvsu.rasterization;


import com.cgvsu.math.vectors.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.List;
public class FullRasterization {
    private static final double EPS = 1e-6;
    public static void fillTriangle(
            final GraphicsContext graphicsContext,
            final int[] arrX,
            final int[] arrY,
            final double[] arrZ,
            final Color color, // Используется для заливки или линий, если текстура не задана
            final Image texture, // Текстура (может быть null, если текстура не используется)
            final List<Vector2D> textureVert, // Текстурные координаты (используются, если текстура задана)
            final Z_Buffer zBuffer,
            final boolean drawWireframe // Флаг: true для отрисовки полигональной сетки
    ) {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        final int width = zBuffer.getWidth();
        final int height = zBuffer.getHeight();

        PixelReader pixelReader = null;
        int textureWidth = 0;
        int textureHeight = 0;
        double[][] uvCoords = null;

        if (texture != null && textureVert != null) {
            pixelReader = texture.getPixelReader();
            textureWidth = (int) texture.getWidth();
            textureHeight = (int) texture.getHeight();
            uvCoords = new double[3][2];
            uvCoords[0] = textureVert.get(0).getData();
            uvCoords[1] = textureVert.get(1).getData();
            uvCoords[2] = textureVert.get(2).getData();
            sort(arrX, arrY, arrZ, uvCoords);
        }else {
            sort(arrX, arrY, arrZ);
        }

        if (drawWireframe) {
            // Отрисовка полигональной сетки
            drawWireframeLine(graphicsContext, arrX[0], arrY[0], arrZ[0], arrX[1], arrY[1], arrZ[1], color, zBuffer);
            drawWireframeLine(graphicsContext, arrX[1], arrY[1], arrZ[1], arrX[2], arrY[2], arrZ[2], color, zBuffer);
            drawWireframeLine(graphicsContext, arrX[2], arrY[2], arrZ[2], arrX[0], arrY[0], arrZ[0], color, zBuffer);
        } else {
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

                        if (texture != null && uvCoords != null) {
                            // Если текстура задана, вычисляем текстурные координаты
                            double u = baryCoords[0] * uvCoords[0][0] + baryCoords[1] * uvCoords[1][0] + baryCoords[2] * uvCoords[2][0];
                            double v = baryCoords[0] * uvCoords[0][1] + baryCoords[1] * uvCoords[1][1] + baryCoords[2] * uvCoords[2][1];

                            // Нормализация текстурных координат
                            int texX = (int) (u * (textureWidth - 1));
                            int texY = (int) (v * (textureHeight - 1));

                            // Получаем цвет пикселя из текстуры
                            Color textureColor = pixelReader.getColor(texX, texY);
                            pixelWriter.setColor(x, y, textureColor);
                        } else {
                            // Если текстура не задана, используем сплошной цвет
                            pixelWriter.setColor(x, y, color);
                        }
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

                        if (texture != null && uvCoords != null) {
                            // Если текстура задана, вычисляем текстурные координаты
                            double u = baryCoords[0] * uvCoords[0][0] + baryCoords[1] * uvCoords[1][0] + baryCoords[2] * uvCoords[2][0];
                            double v = baryCoords[0] * uvCoords[0][1] + baryCoords[1] * uvCoords[1][1] + baryCoords[2] * uvCoords[2][1];

                            // Нормализация текстурных координат
                            int texX = (int) (u * (textureWidth - 1));
                            int texY = (int) (v * (textureHeight - 1));

                            // Получаем цвет пикселя из текстуры
                            Color textureColor = pixelReader.getColor(texX, texY);
                            pixelWriter.setColor(x, y, textureColor);
                        } else {
                            // Если текстура не задана, используем сплошной цвет
                            pixelWriter.setColor(x, y, color);
                        }
                    }
                }
            }
        }
    }

    /**
     * Метод для отрисовки линии с использованием Z-буфера.
     */
    private static void drawWireframeLine(
            final GraphicsContext graphicsContext,
            final int x1, final int y1, final double z1,
            final int x2, final int y2, final double z2,
            final Color color,
            final Z_Buffer zBuffer
    ) {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        final int width = zBuffer.getWidth();
        final int height = zBuffer.getHeight();

        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        double z = z1;
        double zStep = (z2 - z1) / Math.max(dx, dy);

        int x = x1, y = y1;
        while (true) {
            if (x >= 0 && x < width && y >= 0 && y < height && z < zBuffer.get(x, y)) {
                zBuffer.set(x, y, z);
                pixelWriter.setColor(x, y, color);
            }

            if (x == x2 && y == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }

            z += zStep;
        }
    }


    private static void sort(int[] x, int[] y, double[] z, double[][] uv) {
        if (y[0] > y[1]) swap(x, y, z, uv, 0, 1);
        if (y[1] > y[2]) swap(x, y, z, uv, 1, 2);
        if (y[0] > y[1]) swap(x, y, z, uv, 0, 1);
    }
    private static void sort(int[] x, int[] y, double[] z) {
        if (y[0] > y[1]) swap(x, y, z, null, 0, 1);
        if (y[1] > y[2]) swap(x, y, z, null, 1, 2);
        if (y[0] > y[1]) swap(x, y, z, null, 0, 1);
    }

    private static void swap(int[] x, int[] y, double[] z, double[][] uv, int i, int j) {
        int tempX = x[i];
        int tempY = y[i];
        double tempZ = z[i];
        x[i] = x[j];
        y[i] = y[j];
        z[i] = z[j];
        x[j] = tempX;
        y[j] = tempY;
        z[j] = tempZ;

        if (uv != null) {
            double[] tempUV = uv[i];
            uv[i] = uv[j];
            uv[j] = tempUV;
        }
    }

    private static double[] calculateBarycentricCoordinates(int x, int y, int[] xPoints, int[] yPoints) {
        double denominator = (yPoints[1] - yPoints[2]) * (xPoints[0] - xPoints[2]) +
                (xPoints[2] - xPoints[1]) * (yPoints[0] - yPoints[2]);
        if (denominator == 0) return null;

        double alpha = ((yPoints[1] - yPoints[2]) * (x - xPoints[2]) +
                (xPoints[2] - xPoints[1]) * (y - yPoints[2])) / denominator;
        double beta = ((yPoints[2] - yPoints[0]) * (x - xPoints[2]) +
                (xPoints[0] - xPoints[2]) * (y - yPoints[2])) / denominator;
        double gamma = 1 - alpha - beta;

        return (alpha >= 0 && beta >= 0 && gamma >= 0) ? new double[]{alpha, beta, gamma} : null;
    }



}
