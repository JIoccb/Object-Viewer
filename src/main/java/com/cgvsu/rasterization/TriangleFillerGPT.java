package com.cgvsu.rasterization;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.model.Polygon;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class TriangleFillerGPT extends Canvas {

    private final Polygon polygon;
    private final List<Vector3D> vertices;
    private final float[][] zBuffer;

    public TriangleFillerGPT(Polygon polygon, List<Vector3D> vertices, int canvasWidth, int canvasHeight) {
        this.polygon = polygon;
        this.vertices = vertices;
        this.zBuffer = new float[canvasHeight][canvasWidth];

        // Инициализация Z-буфера максимальной глубиной
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                zBuffer[y][x] = Float.MAX_VALUE;
            }
        }
    }

    public void fillTriangle(GraphicsContext gc, Color color) {
        // Получаем индексы вершин полигона
        int index1 = polygon.getVertexIndices().get(0);
        int index2 = polygon.getVertexIndices().get(1);
        int index3 = polygon.getVertexIndices().get(2);

        // Извлекаем координаты вершин из общего списка
        Vector3D v1 = vertices.get(index1);
        Vector3D v2 = vertices.get(index2);
        Vector3D v3 = vertices.get(index3);

        // Преобразуем вершины в экранные координаты
        //todo: реализовать переход в экранные координаты
        // P.S. Я не разобрался где искомый метод растеризации это делает, поэтому придется заниматься этим вместе
        int[] xPoints = {(int) v1.getScreenX(), (int) v2.getScreenX(), (int) v3.getScreenX()};
        int[] yPoints = {(int) v1.getScreenY(), (int) v2.getScreenY(), (int) v3.getScreenY()};
        float[] zPoints = {v1.getScreenZ(), v2.getScreenZ(), v3.getScreenZ()};

        sortVerticesByY(xPoints, yPoints, zPoints);

        int x0 = xPoints[0], y0 = yPoints[0];
        int x1 = xPoints[1], y1 = yPoints[1];
        int x2 = xPoints[2], y2 = yPoints[2];
        float z0 = zPoints[0], z1 = zPoints[1], z2 = zPoints[2];

        if (y1 == y2) {
            fillFlatBottomTriangle(gc, x0, y0, z0, x1, y1, z1, x2, y2, z2, color);
        } else if (y0 == y1) {
            fillFlatTopTriangle(gc, x0, y0, z0, x1, y1, z1, x2, y2, z2, color);
        } else {
            int xM = x0 + (int) Math.round((double) (y1 - y0) / (y2 - y0) * (x2 - x0));
            float zM = z0 + (float) ((double) (y1 - y0) / (y2 - y0) * (z2 - z0));

            fillFlatBottomTriangle(gc, x0, y0, z0, x1, y1, z1, xM, y1, zM, color);
            fillFlatTopTriangle(gc, x1, y1, z1, xM, y1, zM, x2, y2, z2, color);
        }
    }

    private void fillFlatBottomTriangle(GraphicsContext gc, int x0, int y0, float z0, int x1, int y1, float z1, int x2, int y2, float z2, Color color) {
        double slopeLeft = (double) (x1 - x0) / (y1 - y0);
        double slopeRight = (double) (x2 - x0) / (y2 - y0);
        double zSlopeLeft = (z1 - z0) / (y1 - y0);
        double zSlopeRight = (z2 - z0) / (y2 - y0);

        double xStart = x0;
        double xEnd = x0;
        double zStart = z0;
        double zEnd = z0;

        for (int y = y0; y <= y1; y++) {
            drawScanline(gc.getPixelWriter(), (int) xStart, zStart, (int) xEnd, zEnd, y, color);
            xStart += slopeLeft;
            xEnd += slopeRight;
            zStart += zSlopeLeft;
            zEnd += zSlopeRight;
        }
    }

    private void fillFlatTopTriangle(GraphicsContext gc, int x0, int y0, float z0, int x1, int y1, float z1, int x2, int y2, float z2, Color color) {
        double slopeLeft = (double) (x2 - x0) / (y2 - y0);
        double slopeRight = (double) (x2 - x1) / (y2 - y1);
        double zSlopeLeft = (z2 - z0) / (y2 - y0);
        double zSlopeRight = (z2 - z1) / (y2 - y1);

        double xStart = x2;
        double xEnd = x2;
        double zStart = z2;
        double zEnd = z2;

        for (int y = y2; y >= y0; y--) {
            drawScanline(gc.getPixelWriter(), (int) xStart, zStart, (int) xEnd, zEnd, y, color);
            xStart -= slopeLeft;
            xEnd -= slopeRight;
            zStart -= zSlopeLeft;
            zEnd -= zSlopeRight;
        }
    }

    private void drawScanline(PixelWriter pw, int xStart, double zStart, int xEnd, double zEnd, int y, Color color) {
        if (xStart > xEnd) {
            int tempX = xStart;
            xStart = xEnd;
            xEnd = tempX;

            double tempZ = zStart;
            zStart = zEnd;
            zEnd = tempZ;
        }

        double z = zStart;
        double zSlope = (zEnd - zStart) / (xEnd - xStart);

        for (int x = xStart; x <= xEnd; x++) {
            if (z < zBuffer[y][x]) {
                zBuffer[y][x] = (float) z;
                pw.setColor(x, y, color);
            }
            z += zSlope;
        }
    }

    private void sortVerticesByY(int[] xPoints, int[] yPoints, float[] zPoints) {
        int[][] vertices = {{xPoints[0], yPoints[0], 0}, {xPoints[1], yPoints[1], 1}, {xPoints[2], yPoints[2], 2}};
        Arrays.sort(vertices, Comparator.comparingInt(v -> v[1]));

        int[] sortedX = new int[3];
        int[] sortedY = new int[3];
        float[] sortedZ = new float[3];

        for (int i = 0; i < 3; i++) {
            sortedX[i] = vertices[i][0];
            sortedY[i] = vertices[i][1];
            sortedZ[i] = zPoints[vertices[i][2]];
        }

        System.arraycopy(sortedX, 0, xPoints, 0, 3);
        System.arraycopy(sortedY, 0, yPoints, 0, 3);
        System.arraycopy(sortedZ, 0, zPoints, 0, 3);
    }
}




