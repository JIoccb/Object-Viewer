package com.cgvsu.rasterization;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Comparator;


// для того чтобы методы корректно работали с векторами, нужно применить к ним .getData()
public class TriangleFiller extends Canvas {
    private static int[] x_Points;
    private static int[] y_Points;
    private static Color col;

    public static void fillTriangle(GraphicsContext gc,
                                    final int[] xPoints,
                                    final int[] yPoints,
                                    final Color color) {

        if (xPoints.length != 3 || yPoints.length != 3) {
            throw new IllegalArgumentException("Triangle requires exactly 3 vertices and 3 colors.");
        }
        x_Points = xPoints;
        y_Points = yPoints;
        col = color;
        sortVerticesByY(xPoints, yPoints);

        int x0 = xPoints[0], y0 = yPoints[0];
        int x1 = xPoints[1], y1 = yPoints[1];
        int x2 = xPoints[2], y2 = yPoints[2];

        if (y1 == y2) {
            fillFlatBottomTriangle(gc, x0, y0, x1, y1, x2, y2);
        } else if (y0 == y1) {
            fillFlatTopTriangle(gc, x0, y0, x1, y1, x2, y2);
        } else {
            int xM = x0 + (int) Math.round((double) (y1 - y0) / (y2 - y0) * (x2 - x0));

            fillFlatBottomTriangle(gc, x0, y0, x1, y1, xM, y1);
            fillFlatTopTriangle(gc, x1, y1, xM, y1, x2, y2);
        }
    }

    private static void fillFlatBottomTriangle(GraphicsContext gc, int x0, int y0, int x1, int y1, int x2, int y2) {
        double slopeLeft = (double) (x1 - x0) / (y1 - y0);
        double slopeRight = (double) (x2 - x0) / (y2 - y0);


        double xStart = x0;
        double xEnd = x0;

        for (int y = y0; y <= y1; y++) {
            int xStartInt = (int) Math.round(Math.max(0, Math.min(xStart, gc.getCanvas().getWidth() - 1)));
            int xEndInt = (int) Math.round(Math.max(0, Math.min(xEnd, gc.getCanvas().getWidth() - 1)));

            drawScanline(gc.getPixelWriter(), Math.min(xStartInt, xEndInt), Math.max(xStartInt, xEndInt), y);
            xStart += slopeLeft;
            xEnd += slopeRight;
        }
    }

    private static void fillFlatTopTriangle(GraphicsContext gc, int x0, int y0, int x1, int y1, int x2, int y2) {
        double slopeLeft = (double) (x2 - x0) / (y2 - y0);
        double slopeRight = (double) (x2 - x1) / (y2 - y1);


        double xStart = x2;
        double xEnd = x2;

        for (int y = y2; y >= y0; y--) {
            int xStartInt = (int) Math.round(Math.max(0, Math.min(xStart, gc.getCanvas().getWidth() - 1)));
            int xEndInt = (int) Math.round(Math.max(0, Math.min(xEnd, gc.getCanvas().getWidth() - 1)));

            drawScanline(gc.getPixelWriter(), Math.min(xStartInt, xEndInt), Math.max(xStartInt, xEndInt), y);
            xStart -= slopeLeft;
            xEnd -= slopeRight;
        }
    }

    private static void drawScanline(PixelWriter pw, int xStart, int xEnd, int y) {
        for (int x = xStart; x <= xEnd; x++) {
            double coverage = calculateCoverage(x, y, x_Points, y_Points);
            if (coverage > 0) {

                // Устанавливаем смешанный цвет
                pw.setColor(x, y, col);
            }
        }
    }

    private static void sortVerticesByY(int[] xPoints, int[] yPoints) {
        int[][] vertices = {{xPoints[0], yPoints[0]}, {xPoints[1], yPoints[1]}, {xPoints[2], yPoints[2]}};
        Arrays.sort(vertices, Comparator.comparingInt(v -> v[1]));

        for (int i = 0; i < 3; i++) {
            xPoints[i] = vertices[i][0];
            yPoints[i] = vertices[i][1];
        }
    }

    private static double[] calculateBarycentricCoordinates(int x, int y, int[] xPoints, int[] yPoints) {
        double denominator = (yPoints[1] - yPoints[2]) * (xPoints[0] - xPoints[2]) + (xPoints[2] - xPoints[1]) * (yPoints[0] - yPoints[2]);
        double alpha = ((yPoints[1] - yPoints[2]) * (x - xPoints[2]) + (xPoints[2] - xPoints[1]) * (y - yPoints[2])) / denominator;
        double beta = ((yPoints[2] - yPoints[0]) * (x - xPoints[2]) + (xPoints[0] - xPoints[2]) * (y - yPoints[2])) / denominator;
        double gamma = 1 - alpha - beta;

        return (alpha >= 0 && beta >= 0 && gamma >= 0) ? new double[]{alpha, beta, gamma} : null;
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static double calculateCoverage(int x, int y, int[] xPoints, int[] yPoints) {
        if (!isBoundingBoxIntersect(x, y, xPoints, yPoints)) {
            return 0.0;
        }

        int subPixelCount = 64;
        int insideCount = 0;

        double step = 1.0 / 8;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                double subPixelX = x + (i + 0.5) * step;
                double subPixelY = y + (j + 0.5) * step;
                if (isPointInTriangle(subPixelX, subPixelY, xPoints, yPoints)) {
                    insideCount++;
                }
            }
        }

        return (double) insideCount / subPixelCount;
    }

    private static boolean isBoundingBoxIntersect(int x, int y, int[] xPoints, int[] yPoints) {
        int minX = Math.min(Math.min(xPoints[0], xPoints[1]), xPoints[2]);
        int maxX = Math.max(Math.max(xPoints[0], xPoints[1]), xPoints[2]);
        int minY = Math.min(Math.min(yPoints[0], yPoints[1]), yPoints[2]);
        int maxY = Math.max(Math.max(yPoints[0], yPoints[1]), yPoints[2]);

        return (x >= minX && x <= maxX && y >= minY && y <= maxY);
    }

    private static boolean isPointInTriangle(double px, double py, int[] xPoints, int[] yPoints) {
        double denominator = (yPoints[1] - yPoints[2]) * (xPoints[0] - xPoints[2]) +
                (xPoints[2] - xPoints[1]) * (yPoints[0] - yPoints[2]);
        double alpha = ((yPoints[1] - yPoints[2]) * (px - xPoints[2]) +
                (xPoints[2] - xPoints[1]) * (py - yPoints[2])) / denominator;
        double beta = ((yPoints[2] - yPoints[0]) * (px - xPoints[2]) +
                (xPoints[0] - xPoints[2]) * (py - yPoints[2])) / denominator;
        double gamma = 1 - alpha - beta;

        return alpha >= 0 && beta >= 0 && gamma >= 0;
    }
}