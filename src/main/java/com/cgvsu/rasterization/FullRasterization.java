package com.cgvsu.rasterization;


import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class FullRasterization {
    private static final double EPS = 1e-6;
    public static void fillTriangle(
            final GraphicsContext graphicsContext,
            final int[] arrX,
            final int[] arrY,
            final double[] arrZ,
            final Color color,
            final Image texture,
            final List<Vector2D> textureVert,
            final Z_Buffer zBuffer,
            final boolean drawWireframe,
            final boolean useLighting, // Новый флаг
            final List<Vector3D> normals, // Список нормалей
            final Vector3D lightDirection // Направление источника света
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
        } else {
            sort(arrX, arrY, arrZ);
        }

        // Сортируем нормали так же, как вершины
        List<Vector3D> sortedNormals = new ArrayList<>(normals);
        sortNormals(sortedNormals);

        if (drawWireframe) {
            drawWireframeLine(graphicsContext, arrX[0], arrY[0], arrZ[0], arrX[1], arrY[1], arrZ[1], color, zBuffer);
            drawWireframeLine(graphicsContext, arrX[1], arrY[1], arrZ[1], arrX[2], arrY[2], arrZ[2], color, zBuffer);
            drawWireframeLine(graphicsContext, arrX[2], arrY[2], arrZ[2], arrX[0], arrY[0], arrZ[0], color, zBuffer);
        } else {
            //верхняя часть треугольника
            for (int y = arrY[1]; y <= arrY[2]; y++) {
                final int x1 = calculateEdge(y, arrX[1], arrY[1], arrX[2], arrY[2]);
                final int x2 = calculateEdge(y, arrX[0], arrY[0], arrX[2], arrY[2]);

                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    if (x < 0 || x >= width || y < 0 || y >= height) continue;

                    double[] baryCoords = calculateBarycentricCoordinates(x, y, arrX, arrY);
                    if (baryCoords == null) continue;

                    double z = baryCoords[0] * arrZ[0] + baryCoords[1] * arrZ[1] + baryCoords[2] * arrZ[2];
                    if (z < zBuffer.get(x, y) || Math.abs(z - zBuffer.get(x, y)) < EPS) {
                        zBuffer.set(x, y, z);

                        Color finalColor = color;
                        double intensity = 1.0; // Значение по умолчанию для освещения

                        if (useLighting) {
                            Vector3D interpolatedNormal = interpolateNormal(baryCoords, sortedNormals);
                            intensity = Math.max(0, BinaryOperations.dot(interpolatedNormal, lightDirection.normalize()));
                            //intensity = Math.max(0, interpolatedNormal.dot(lightDirection.normalize()));
                            finalColor = color.deriveColor(0, 1, intensity, 1);
                        }

                        if (texture != null && uvCoords != null) {
                            double u = baryCoords[0] * uvCoords[0][0] + baryCoords[1] * uvCoords[1][0] + baryCoords[2] * uvCoords[2][0];
                            double v = baryCoords[0] * uvCoords[0][1] + baryCoords[1] * uvCoords[1][1] + baryCoords[2] * uvCoords[2][1];
                            u = Math.max(0, Math.min(1, u));
                            v = Math.max(0, Math.min(1, v));

                            int texX = (int) (u * (textureWidth - 1));
                            int texY = (int) ((1 - v) * (textureHeight - 1));
                            Color textureColor = pixelReader.getColor(texX, texY);

                            if (useLighting) {
                                textureColor = textureColor.deriveColor(0, 1, intensity, 1);
                            }
                            pixelWriter.setColor(x, y, textureColor);
                        } else {
                            pixelWriter.setColor(x, y, finalColor);
                        }
                    }
                }
            }
            // Нижняя часть треугольника
            for (int y = arrY[0]; y < arrY[1]; y++) {
                final int x1 = calculateEdge(y, arrX[0], arrY[0], arrX[1], arrY[1]);
                final int x2 = calculateEdge(y, arrX[0], arrY[0], arrX[2], arrY[2]);

                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    if (x < 0 || x >= width || y < 0 || y >= height) continue;

                    double[] baryCoords = calculateBarycentricCoordinates(x, y, arrX, arrY);
                    if (baryCoords == null) continue;

                    double z = baryCoords[0] * arrZ[0] + baryCoords[1] * arrZ[1] + baryCoords[2] * arrZ[2];
                    if (z < zBuffer.get(x, y) || Math.abs(z - zBuffer.get(x, y)) < EPS) {
                        zBuffer.set(x, y, z);

                        Color finalColor = color;
                        double intensity = 1.0; // Значение по умолчанию для освещения

                        if (useLighting) {
                            Vector3D interpolatedNormal = interpolateNormal(baryCoords, sortedNormals);
                            intensity = Math.max(0, BinaryOperations.dot(interpolatedNormal, lightDirection.normalize()));
                            finalColor = color.deriveColor(0, 1, intensity, 1);
                        }

                        if (texture != null && uvCoords != null) {
                            double u = baryCoords[0] * uvCoords[0][0] + baryCoords[1] * uvCoords[1][0] + baryCoords[2] * uvCoords[2][0];
                            double v = baryCoords[0] * uvCoords[0][1] + baryCoords[1] * uvCoords[1][1] + baryCoords[2] * uvCoords[2][1];
                            u = Math.max(0, Math.min(1, u));
                            v = Math.max(0, Math.min(1, v));

                            int texX = (int) (u * (textureWidth - 1));
                            int texY = (int) ((1 - v) * (textureHeight - 1));
                            Color textureColor = pixelReader.getColor(texX, texY);

                            if (useLighting) {
                                textureColor = textureColor.deriveColor(0, 1, intensity, 1);
                            }
                            pixelWriter.setColor(x, y, textureColor);
                        } else {
                            pixelWriter.setColor(x, y, finalColor);
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


    /**
     * Вычисляет значение координаты X на ребре треугольника для заданной координаты Y.
     *
     * @param y     Координата Y, для которой вычисляется X.
     * @param x1    Координата X первой вершины ребра.
     * @param y1    Координата Y первой вершины ребра.
     * @param x2    Координата X второй вершины ребра.
     * @param y2    Координата Y второй вершины ребра.
     * @return Значение X для заданного Y на ребре между точками (x1, y1) и (x2, y2).
     */
    private static int calculateEdge(int y, int x1, int y1, int x2, int y2) {
        // Проверка на горизонтальное ребро
        if (y1 == y2) {
            return x1; // Или x2 — значение будет одинаковым
        }

        // Линейная интерполяция X для заданного Y
        return x1 + (y - y1) * (x2 - x1) / (y2 - y1);
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
    // Вспомогательный метод для сортировки нормалей
    private static void sortNormals(List<Vector3D> normals) {
        /*
        if (normals.get(0).getY() > normals.get(1).getY()) Collections.swap(normals, 0, 1);
        if (normals.get(1).getY() > normals.get(2).getY()) Collections.swap(normals, 1, 2);
        if (normals.get(0).getY() > normals.get(1).getY()) Collections.swap(normals, 0, 1);
         */
        if (normals.get(0).get(1) > normals.get(1).get(1)) Collections.swap(normals, 0, 1);
        if (normals.get(1).get(1) > normals.get(2).get(1)) Collections.swap(normals, 1, 2);
        if (normals.get(0).get(1) > normals.get(1).get(1)) Collections.swap(normals, 0, 1);
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

    private static Vector3D interpolateNormal(double[] baryCoords, List<Vector3D> normals) {
        double x = baryCoords[0] * normals.get(0).get(0) + baryCoords[1] * normals.get(1).get(0) + baryCoords[2] * normals.get(2).get(0);
        double y = baryCoords[0] * normals.get(0).get(1) + baryCoords[1] * normals.get(1).get(1) + baryCoords[2] * normals.get(2).get(1);
        double z = baryCoords[0] * normals.get(0).get(2) + baryCoords[1] * normals.get(1).get(2) + baryCoords[2] * normals.get(2).get(2);
        return new Vector(new double[]{x,y,z}).normalize().toVector3D();

    }







}
