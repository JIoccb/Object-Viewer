package com.cgvsu.rasterization;


import com.cgvsu.math.operations.BinaryOperations;
import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.model.VertexObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.*;

public class FullRasterization {
    private static final double EPS = 1e-6;

    public static void fillTriangle(
            final GraphicsContext graphicsContext,
            final int[] arrX,
            final int[] arrY,
            final double[] arrZ,
            final Color baseColor,
            final Image texture,
            final List<Vector2D> textureVert,
            final Z_Buffer zBuffer,
            final boolean drawWireframe,
            final boolean useLighting, // Использование освещения
            final List<Vector3D> normals, // Список нормалей
            final Vector3D lightDirection // Направление источника света
    ) {
        final double k = 0.4; // Коэффициент освещения

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        final int width = zBuffer.getWidth();
        final int height = zBuffer.getHeight();

        PixelReader pixelReader = null;
        int textureWidth = 0;
        int textureHeight = 0;
        double[][] uvCoords = null;

        // Создание и сортировка вершин треугольника по Y-координате
        VertexObject vo1 = new VertexObject(arrX[0], arrY[0], arrZ[0], textureVert.get(0), normals.get(0));
        VertexObject vo2 = new VertexObject(arrX[1], arrY[1], arrZ[1], textureVert.get(1), normals.get(1));
        VertexObject vo3 = new VertexObject(arrX[2], arrY[2], arrZ[2], textureVert.get(2), normals.get(2));
        List<VertexObject> vertexList = Arrays.asList(vo1, vo2, vo3);

        // vertexList.sort(Comparator.comparingInt(VertexObject::getY));
        vertexList.sort(new Comparator<VertexObject>() {
            @Override
            public int compare(VertexObject o1, VertexObject o2) {
                /*if (o1.getY() == o2.getY()){
                    return o1.getX() <= o2.getX() ? -1 : 1;
                }*/


                return o1.getY() <= o2.getY() ? -1 : 1;
            }
        });

        // Подготовка текстурных координат (если есть текстура)
        if (texture != null && textureVert != null) {
            pixelReader = texture.getPixelReader();
            textureWidth = (int) texture.getWidth();
            textureHeight = (int) texture.getHeight();
            uvCoords = new double[3][2];
            uvCoords[0] = vertexList.get(0).getTextureVert().getData();
            uvCoords[1] = vertexList.get(1).getTextureVert().getData();
            uvCoords[2] = vertexList.get(2).getTextureVert().getData();
        }

        // Отрисовка каркаса (если включено)
        if (drawWireframe) {
            drawLine(graphicsContext, vertexList.get(0).getX(), vertexList.get(0).getY(), vertexList.get(0).getZ(), vertexList.get(1).getX(), vertexList.get(1).getY(), vertexList.get(1).getZ(), zBuffer);
            drawLine(graphicsContext, vertexList.get(1).getX(), vertexList.get(1).getY(), vertexList.get(1).getZ(), vertexList.get(2).getX(), vertexList.get(2).getY(), vertexList.get(2).getZ(), zBuffer);
            drawLine(graphicsContext, vertexList.get(2).getX(), vertexList.get(2).getY(), vertexList.get(2).getZ(), vertexList.get(0).getX(), vertexList.get(0).getY(), vertexList.get(0).getZ(), zBuffer);
        }

        // Растеризация треугольника
        for (int y = vertexList.get(0).getY(); y <= vertexList.get(2).getY(); y++) {
            int x1 = (y <= vertexList.get(1).getY())
                    ? calculateEdge(y, vertexList.get(0).getX(), vertexList.get(0).getY(), vertexList.get(1).getX(), vertexList.get(1).getY())
                    : calculateEdge(y, vertexList.get(1).getX(), vertexList.get(1).getY(), vertexList.get(2).getX(), vertexList.get(2).getY());
            int x2 = calculateEdge(y, vertexList.get(0).getX(), vertexList.get(0).getY(), vertexList.get(2).getX(), vertexList.get(2).getY());

            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                if (x < 0 || x >= width || y < 0 || y >= height) continue;

                double[] baryCoords = calculateBarycentricCoordinates(x, y, vertexList);
                if (baryCoords == null) continue;

                double z = -(baryCoords[0] * vertexList.get(0).getZ() + baryCoords[1] * vertexList.get(1).getZ() + baryCoords[2] * vertexList.get(2).getZ());

                // Проверка и обновление Z-буфера
                if (z < zBuffer.get(x, y)) {
                    zBuffer.set(x, y, z);

                    // Интерполяция нормалей
                    Vector3D interpolatedNormal = interpolateNormal(baryCoords, vertexList);

                    // Вычисление освещения
                    double lightingFactor = 1.0;
                    if (useLighting) {
                        double l = -(Math.max(0, -BinaryOperations.dot(interpolatedNormal, lightDirection.normalize())));
                        lightingFactor = (1 - k) + k * l;
                    }

                    // Применение цвета с учетом освещения
                    Color finalColor = baseColor.deriveColor(0, 1, lightingFactor, 1);

                    // Работа с текстурой
                    if (texture != null && uvCoords != null) {
                        double u = baryCoords[0] * uvCoords[0][0] + baryCoords[1] * uvCoords[1][0] + baryCoords[2] * uvCoords[2][0];
                        double v = baryCoords[0] * uvCoords[0][1] + baryCoords[1] * uvCoords[1][1] + baryCoords[2] * uvCoords[2][1];
                        u = Math.max(0, Math.min(1, u));
                        v = Math.max(0, Math.min(1, v));

                        int texX = (int) (u * (textureWidth - 1));
                        int texY = (int) ((1 - v) * (textureHeight - 1));
                        Color textureColor = pixelReader.getColor(texX, texY);

                        if (useLighting) {
                            textureColor = textureColor.deriveColor(0, 1, lightingFactor, 1);
                        }
                        pixelWriter.setColor(x, y, textureColor);
                    } else {
                        pixelWriter.setColor(x, y, finalColor);
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
            final Z_Buffer zBuffer
    ) {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        final int width = zBuffer.getWidth();
        final int height = zBuffer.getHeight();
        final Color color = Color.BLACK;

        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        double z = z1;
        double zStep = (z2 - z1) / Math.max(dx, dy);

        int x = x1, y = y1;
        while (true) {
            if (x >= 0 && x < width && y >= 0 && y < height && -z - 0.5 < zBuffer.get(x, y)) {
                zBuffer.set(x, y, z - 0.5);
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
    public static void drawLine(GraphicsContext graphicsContext,
                                int x0, int y0, double z0,
                                int x1, int y1, double z1,
                                final Z_Buffer zBuffer) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        int width = zBuffer.getWidth();
        int height = zBuffer.getHeight();
        int x = x0;
        int y = y0;
        int deltax = Math.abs(x1 - x0);
        int deltay = Math.abs(y1 - y0);
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;
        int error = deltax - deltay;
        while (true) {
            if (x0 >= 0 && x0 < width && y0 >= 0 && y0 < height) {
                float alpha = (float) Math.pow((Math.pow(x - x0, 2) + Math.pow(y - y0, 2)), 0.5);
                float beta = (float) Math.pow((Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2)), 0.5);
                float denominator = 1 / (alpha + beta);
                alpha *= denominator;
                beta *= denominator;
                double z = -(beta * z0 + alpha * z1);
                if (zBuffer.get(x0,y0) + 0.03 > z) {
                    pixelWriter.setColor(x0, y0, Color.BLACK);
                    zBuffer.set(x0,y0, z - 0.03);
                }
            }

            if (x0 == x1 && y0 == y1) break;

            int error2 = error * 2;

            if (error2 > -deltay) {
                error -= deltay;
                x0 += sx;
            }
            if (error2 < deltax) {
                error += deltax;
                y0 += sy;
            }
        }
    }


    /**
     * Вычисляет значение координаты X на ребре треугольника для заданной координаты Y.
     *
     * @param y  Координата Y, для которой вычисляется X.
     * @param x1 Координата X первой вершины ребра.
     * @param y1 Координата Y первой вершины ребра.
     * @param x2 Координата X второй вершины ребра.
     * @param y2 Координата Y второй вершины ребра.
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

    private static double[] calculateBarycentricCoordinates(int x, int y, List<VertexObject> vertexList) {
        double denominator = (vertexList.get(1).getY() - vertexList.get(2).getY()) * (vertexList.get(0).getX() - vertexList.get(2).getX()) +
                (vertexList.get(2).getX() - vertexList.get(1).getX()) * (vertexList.get(0).getY() - vertexList.get(2).getY());
        double alpha;
        double beta;
        double gamma;
        if (denominator  == 0){
             alpha = ((vertexList.get(1).getY() - vertexList.get(2).getY()) * (x - vertexList.get(2).getX()) +
                    (vertexList.get(2).getX() - vertexList.get(1).getX()) * (y - vertexList.get(2).getY()));
             beta = ((vertexList.get(2).getY() - vertexList.get(0).getY()) * (x - vertexList.get(2).getX()) +
                    (vertexList.get(0).getX() - vertexList.get(2).getX()) * (y - vertexList.get(2).getY()));

        }else {
             alpha = ((vertexList.get(1).getY() - vertexList.get(2).getY()) * (x - vertexList.get(2).getX()) +
                    (vertexList.get(2).getX() - vertexList.get(1).getX()) * (y - vertexList.get(2).getY())) / denominator;
             beta = ((vertexList.get(2).getY() - vertexList.get(0).getY()) * (x - vertexList.get(2).getX()) +
                    (vertexList.get(0).getX() - vertexList.get(2).getX()) * (y - vertexList.get(2).getY())) / denominator;
        }


         gamma = 1 - alpha - beta;

        return (alpha >= 0 && beta >= 0 && gamma >= 0) ? new double[]{alpha, beta, gamma} : null;
    }

    // Интерполяция нормалей
    private static Vector3D interpolateNormal(double[] baryCoords, List<VertexObject> vertexList) {
        double x = baryCoords[0] * vertexList.get(0).getNormal().get(0) + baryCoords[1] * vertexList.get(1).getNormal().get(0) + baryCoords[2] * vertexList.get(2).getNormal().get(0);
        double y = baryCoords[0] * vertexList.get(0).getNormal().get(1) + baryCoords[1] * vertexList.get(1).getNormal().get(1) + baryCoords[2] * vertexList.get(2).getNormal().get(1);
        double z = baryCoords[0] * vertexList.get(0).getNormal().get(2) + baryCoords[1] * vertexList.get(1).getNormal().get(2) + baryCoords[2] * vertexList.get(2).getNormal().get(2);
        return new Vector3D(new double[]{x, y, z}).normalize().toVector3D();
    }


    private static double[] calculateBarycentricCoordinates1(int x, int y, int[] xPoints, int[] yPoints) {
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

    private static void sort(int[] x, int[] y, double[] z, double[][] uv) {
        if (y[0] > y[1]) swap(x, y, z, uv, 0, 1);
        if (y[0] > y[2]) swap(x, y, z, uv, 0, 2);
        if (y[1] > y[2]) swap(x, y, z, uv, 1, 2);
    }

    private static void sort(int[] x, int[] y, double[] z) {
        if (y[0] > y[1]) swap(x, y, z, null, 0, 1);
        if (y[0] > y[2]) swap(x, y, z, null, 0, 2);
        if (y[1] > y[2]) swap(x, y, z, null, 1, 2);
    }

    // Вспомогательный метод для сортировки нормалей
    private static void sortNormals(List<Vector3D> normals) {
        /*
        if (normals.get(0).getY() > normals.get(1).getY()) Collections.swap(normals, 0, 1);
        if (normals.get(1).getY() > normals.get(2).getY()) Collections.swap(normals, 1, 2);
        if (normals.get(0).getY() > normals.get(1).getY()) Collections.swap(normals, 0, 1);
         */
        if (normals.get(0).get(1) > normals.get(1).get(1)) Collections.swap(normals, 0, 1);
        if (normals.get(0).get(1) > normals.get(2).get(1)) Collections.swap(normals, 0, 2);
        if (normals.get(1).get(1) > normals.get(2).get(1)) Collections.swap(normals, 1, 2);
    }

    private static void swap(int[] x, int[] y, double[] z, double[][] uv, int i, int j) {
        if (i < 0 || j < 0 || i >= x.length || j >= x.length ||
                i >= y.length || j >= y.length ||
                i >= z.length || j >= z.length ||
                (uv != null && (i >= uv.length || j >= uv.length))) {
            throw new IllegalArgumentException("Invalid indices or array lengths");
        }

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


}
