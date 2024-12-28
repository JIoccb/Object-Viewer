package com.cgvsu.rasterization;


import com.cgvsu.math.Global;
import com.cgvsu.math.vector.Vector3f;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import javax.vecmath.Point2f;
import java.awt.image.BufferedImage;
import java.util.*;
public class RatmirRasterization {
    public static void drawTriangle(final PixelWriter pixelWriter,
                                    final int x0, final int y0, final float z0,
                                    final int x1, final int y1, final float z1,
                                    final int x2, final int y2, final float z2,
                                    final int tx0, final int ty0,
                                    final int tx1, final int ty1,
                                    final int tx2, final int ty2,
                                    Color color,
                                    Float[][] zBuffer,
                                    BufferedImage texture,
                                    boolean useTexture,
                                    boolean useLight,
                                    Vector3f n0,
                                    Vector3f n1,
                                    Vector3f n2,
                                    Vector3f ray,
                                    int width,
                                    int height) {
        long start = System.currentTimeMillis();
        float maxZ = -9999;
        Point2f d0 = new Point2f(x0, y0);
        Point2f d1 = new Point2f(x1, y1);
        Point2f d2 = new Point2f(x2, y2);
        List<Point2f> dots = Arrays.asList(d0, d1, d2);
        Point2fYThenXComparator compYX = new Point2fYThenXComparator();
        dots.sort(compYX);
        float function1;
        float function2;
        float function3 = 0;
        float function4 = 0;
        Point2f vertex1 = dots.get(1);
        Point2f vertex2 = dots.get(2);

        //Рассчёт функций верхней половины
        if (dots.get(0).y - vertex2.y != 0) {
            function1 = (dots.get(0).x - vertex2.x) / (dots.get(0).y - vertex2.y);
        } else {
            function1 = 0;
        }
        if (dots.get(0).y - vertex1.y != 0) {
            function2 = (dots.get(0).x - vertex1.x) / (dots.get(0).y - vertex1.y);
        } else {
            function2 = 0;
        }

        //Рассчёт функций нижней половины
        if (vertex2.y >= vertex1.y) {
            if (dots.get(2).y - vertex1.y != 0) {
                function3 = (dots.get(2).x - vertex1.x) / (dots.get(2).y - vertex1.y);
            } else function3 = 0;
        } else {
            if (dots.get(2).y - vertex2.y != 0) {
                function4 = (dots.get(2).x - vertex2.x) / (dots.get(2).y - vertex2.y);
            } else function4 = 0;
        }
        //Переменная, чтобы понять, нужно ли отрисовывать треугольник
        float temp = 0;

        for (int row = Math.max((int) dots.get(0).y, 0); row < Math.min(dots.get(1).y, height); row++) {
            int fullFunctionLeft = Math.max((int) (function1 * row + vertex2.x - function1 * vertex2.y), 0);
            int fullFunctionRight = Math.max((int) (function2 * row + vertex1.x - function2 * vertex1.y), 0);
            temp = 0;
            for (int col = Math.min(fullFunctionRight, fullFunctionLeft); col <= Math.max(fullFunctionRight, fullFunctionLeft); col++) {
                if (col < width && row < height && col > 0 && row > 0) {
                    if (zBuffer[col][row] > maxZ) {
                        maxZ = zBuffer[col][row];
                        if (zBuffer[col][row] == 9999) {
                            temp = 9999;
                            break;
                        }
                    }
                }
            }
            if (temp == 9999) {
                break;
            }
        }
        if (temp!=9999) {
            for (int row = (int) dots.get(1).y; row < dots.get(2).y; row++) {
                int fullFunctionLeft = (int) (function3 * row + vertex2.x - function3 * vertex2.y);
                int fullFunctionRight = (int) (function4 * row + vertex1.x -function4 * vertex1.y);
                for (int col = Math.min(fullFunctionRight, fullFunctionLeft); col <= Math.max(fullFunctionRight, fullFunctionLeft); col++) {
                    if (col < width && row < height && col > 0 && row > 0) {
                        if (zBuffer[col][row] > maxZ) {
                            maxZ = zBuffer[col][row];
                            if (zBuffer[col][row] == 9999) {
                                temp = 9999;
                                break;
                            }
                        }
                    }
                }
                if (temp == 9999) {
                    break;
                }
            }
        }
        if (Math.min(Math.min(z0, z1), z2) > maxZ && maxZ != -9999) {
            return;
        }
        long mid = System.currentTimeMillis();

        int startArray = Math.max((int) dots.get(0).y, 0);
        int endArray = (int) Math.min(dots.get(1).y, height);
        int[] fullFunctionRightArray = new int[Math.abs(endArray-startArray)];
        int[] fullFunctionLeftArray = new int[Math.abs(endArray - startArray)];

        if (function1 < Global.eps){
            for (int row = startArray; row < endArray; row++) {
                fullFunctionLeftArray[row - startArray] = (int) vertex2.x;
            }
        } else {
            for (int row = startArray; row < endArray; row++) {
                fullFunctionLeftArray[row - startArray] = Math.max((int) (function1 * row + vertex2.x - function1 * vertex2.y), 0);
            }
        }
        if (function2 < Global.eps){
            for (int row = startArray; row < endArray; row++) {
                fullFunctionRightArray[row - startArray] = (int) vertex1.x;
            }
        } else {
            for (int row = startArray; row < endArray; row++) {
                fullFunctionRightArray[row - startArray] = Math.max((int) (function2 * row + vertex1.x - function2 * vertex1.y), 0);
            }
        }
        //Верхняя половина
        for (int row = Math.max((int) dots.get(0).y, 0); row < Math.min(dots.get(1).y, height); row++) {
            for (int col = Math.min(fullFunctionRightArray[row - startArray], fullFunctionLeftArray[row - startArray]); col <= Math.max(fullFunctionRightArray[row - startArray], fullFunctionLeftArray[row - startArray]); col++) {
                //float[] coordinates = calculateBarycentricCoordinates(col, row, d0, d1, d2);
                //float z = coordinates[0] * z0 + coordinates[1] * z1 + coordinates[2] * z2;
                //if (zBuffer[col][row] > z) {
                Color finalColor = color;
                    /@if (useTexture) {
                    int x = coordinatesFix((int) (coordinates[0] * tx0 + coordinates[1] * tx1 + coordinates[2] * tx2), texture.getWidth());
                    int y = coordinatesFix((int) (coordinates[0] * ty0 + coordinates[1] * ty1 + coordinates[2] * ty2), texture.getHeight());
                    int clr = texture.getRGB(x, y);
                    float red = (float) ((clr & 0x00ff0000) >> 16) / 255;
                    float green = (float) ((clr & 0x0000ff00) >> 8) / 255;
                    float blue = (float) (clr & 0x000000ff) / 255;
                    finalColor = new Color(red, green, blue, 1);
                } else {
                    finalColor = color;
                }
                if (useLight) {
                    Vector3f currentN = new Vector3f(coordinates[0] * n0.getX() + coordinates[1] * n1.getX() + coordinates[2] * n2.getX(),
                            coordinates[0] * n0.getY() + coordinates[1] * n1.getY() + coordinates[2] * n2.getY(),
                            coordinates[0] * n0.getZ() + coordinates[1] * n1.getZ() + coordinates[2] * n2.getZ());
                    float l = (float) -(currentN.getX() * ray.getX() + currentN.getY() * ray.getY() + currentN.getZ() * ray.getZ());// currentN.vectorMultiply(ray);
                    float k = 0.7F;
                    if (l > 0) {if (l > 1) {
                        l = 1;
                    }
                        finalColor = new Color(finalColor.getRed() * (1 - k) + finalColor.getRed() * k * l, finalColor.getGreen() * (1 - k) + finalColor.getGreen() * k * l, finalColor.getBlue() * (1 - k) + finalColor.getBlue() * k * l, 1);
                    } else {
                        finalColor = new Color(finalColor.getRed() * (1 - k), finalColor.getGreen() * (1 - k), finalColor.getBlue() * (1 - k), 1);
                    }
                }*/
                pixelWriter.setColor(col, row, finalColor);
                //zBuffer[col][row] = z;
                //}
            }
            //drawHalfOfTriangle(function3, function4, vertex1, vertex2, d0, d1, d2, texture, zBuffer, pixelWriter, row, z0, z1, z2, tx0, ty0, tx1, ty1, tx2, ty2, useTexture, useLight, color, n0, n1, n2, ray);
        }

        if (vertex2.y >= vertex1.y) {
            if (dots.get(2).y - vertex1.y != 0) {
                function2 = (dots.get(2).x - vertex1.x) / (dots.get(2).y - vertex1.y);
            } else function2 = 0;
        } else {
            if (dots.get(2).y - vertex2.y != 0) {
                function1 = (dots.get(2).x - vertex2.x) / (dots.get(2).y - vertex2.y);
            } else function1 = 0;
        }
        startArray = (int) dots.get(1).y;
        endArray = (int) dots.get(2).y;
        fullFunctionRightArray = new int[Math.abs(endArray-startArray)];
        fullFunctionLeftArray = new int[Math.abs(endArray - startArray)];

        if (function1 < Global.eps){
            for (int row = startArray; row < endArray; row++) {
                fullFunctionLeftArray[row - startArray] = (int) vertex2.x;
            }
        } else {
            for (int row = startArray; row < endArray; row++) {
                fullFunctionLeftArray[row - startArray] = Math.max((int) (function1 * row + vertex2.x - function1 * vertex2.y), 0);
            }
        }
        if (function2 < Global.eps){
            for (int row = startArray; row < endArray; row++) {
                fullFunctionRightArray[row - startArray] = (int) vertex1.x;
            }
        } else {
            for (int row = startArray; row < endArray; row++) {
                fullFunctionRightArray[row - startArray] = Math.max((int) (function2 * row + vertex1.x - function2 * vertex1.y), 0);
            }
        }
        //Нижняя половина
        for (int row = (int) dots.get(1).y; row < dots.get(2).y; row++) {
            for (int col = Math.min(fullFunctionRightArray[row - startArray], fullFunctionLeftArray[row - startArray]); col <= Math.max(fullFunctionRightArray[row - startArray], fullFunctionLeftArray[row - startArray]); col++) {
                //float[] coordinates = calculateBarycentricCoordinates(col, row, d0, d1, d2);
                //float z = coordinates[0] * z0 + coordinates[1] * z1 + coordinates[2] * z2;
                if (col > 0 && row > 0 && col < width && row < height) {
                    //if (zBuffer[col][row] > z) {
                    Color finalColor = color;
                        /@if (useTexture) {
                        int x = coordinatesFix((int) (coordinates[0] * tx0 + coordinates[1] * tx1 + coordinates[2] * tx2), texture.getWidth());
                        int y = coordinatesFix((int) (coordinates[0] * ty0 + coordinates[1] * ty1 + coordinates[2] * ty2), texture.getHeight());
                        int clr = texture.getRGB(x, y);
                        float red = (float) ((clr & 0x00ff0000) >> 16) / 255;
                        float green = (float) ((clr & 0x0000ff00) >> 8) / 255;
                        float blue = (float) (clr & 0x000000ff) / 255;
                        finalColor = new Color(red, green, blue, 1);
                    } else {
                        finalColor = color;
                    }
                    if (useLight) {
                        Vector3f currentN = new Vector3f(coordinates[0] * n0.getX() + coordinates[1] * n1.getX() + coordinates[2] *
                                18:01

                        n2.getX(),
                                coordinates[0] * n0.getY() + coordinates[1] * n1.getY() + coordinates[2] * n2.getY(),
                                coordinates[0] * n0.getZ() + coordinates[1] * n1.getZ() + coordinates[2] * n2.getZ());
                        float l = (float) -(currentN.getX() * ray.getX() + currentN.getY() * ray.getY() + currentN.getZ() * ray.getZ());// currentN.vectorMultiply(ray);
                        float k = 0.7F;
                        if (l > 0) {
                            if (l > 1) {
                                l = 1;
                            }
                            finalColor = new Color(finalColor.getRed() * (1 - k) + finalColor.getRed() * k * l, finalColor.getGreen() * (1 - k) + finalColor.getGreen() * k * l, finalColor.getBlue() * (1 - k) + finalColor.getBlue() * k * l, 1);
                        } else {
                            finalColor = new Color(finalColor.getRed() * (1 - k), finalColor.getGreen() * (1 - k), finalColor.getBlue() * (1 - k), 1);
                        }
                    }*/
                    pixelWriter.setColor(col, row, finalColor);
                    //zBuffer[col][row] = z;
                    //}
                }
            }
            //drawHalfOfTriangle(function1, function2, vertex1, vertex2, d0, d1, d2, texture, zBuffer, pixelWriter, row, z0, z1, z2, tx0, ty0, tx1, ty1, tx2, ty2, useTexture, useLight, color, n0, n1, n2, ray);
        }
        long end = System.currentTimeMillis();
        long result = end - start;
        long half = mid - start;
    }

    /*private static void drawHalfOfTriangle(float function1, float function2,
                                           Point2f vertex1, Point2f vertex2,
                                           Point2f d0, Point2f d1, Point2f d2,
                                           BufferedImage texture,
                                           Float[][] ZBuffer,
                                           PixelWriter pixelWriter,
                                           int row,
                                           float z0,
                                           float z1,
                                           float z2,
                                           int tx0, int ty0,
                                           int tx1, int ty1,
                                           int tx2, int ty2,
                                           boolean useTexture,
                                           boolean useLight,
                                           Color color,
                                           Vector3f n0,
                                           Vector3f n1,
                                           Vector3f n2,
                                           Vector3f ray) {
        int fullFunctionLeft = (int) (function1 * row + vertex2.x - function1 * vertex2.y);
        int fullFunctionRight = (int) (function2 * row + vertex1.x - function2 * vertex1.y);
        for (int col = Math.min(fullFunctionRight, fullFunctionLeft); col <= Math.max(fullFunctionRight, fullFunctionLeft); col++) {
            float[] coordinates = calculateBarycentricCoordinates(col, row, d0, d1, d2);
            float z = coordinates[0] * z0 + coordinates[1] * z1 + coordinates[2] * z2;
            if (ZBuffer[col][row] > z) {
                Color finalColor;
                if (useTexture) {
                    int x = coordinatesFix((int) (coordinates[0] * tx0 + coordinates[1] * tx1 + coordinates[2] * tx2), texture.getWidth());
                    int y = coordinatesFix((int) (coordinates[0] * ty0 + coordinates[1] * ty1 + coordinates[2] * ty2), texture.getHeight());
                    int clr = texture.getRGB(x, y);
                    float red = (float) ((clr & 0x00ff0000) >> 16) / 255;
                    float green = (float) ((clr & 0x0000ff00) >> 8) / 255;
                    float blue = (float) (clr & 0x000000ff) / 255;
                    finalColor = new Color(red, green, blue, 1);
                    } else {
                    finalColor = color;
                }
                if (useLight) {
                    Vector3f currentN = new Vector3f(coordinates[0] * n0.getX() + coordinates[1] * n1.getX() + coordinates[2] * n2.getX(),
                            coordinates[0] * n0.getY() + coordinates[1] * n1.getY() + coordinates[2] * n2.getY(),
                            coordinates[0] * n0.getZ() + coordinates[1] * n1.getZ() + coordinates[2] * n2.getZ());
                    float l = (float) -(currentN.getX() * ray.getX() + currentN.getY() * ray.getY() + currentN.getZ() * ray.getZ());// currentN.vectorMultiply(ray);
                    float k = 0.7F;
                    if (l > 0) {
                        if (l > 1) {
                            l = 1;
                        }
                        finalColor = new Color(finalColor.getRed() * (1 - k) + finalColor.getRed() * k * l, finalColor.getGreen() * (1 - k) + finalColor.getGreen() * k * l, finalColor.getBlue() * (1 - k) + finalColor.getBlue() * k * l, 1);
                    } else {
                        finalColor = new Color(finalColor.getRed() * (1 - k), finalColor.getGreen() * (1 - k), finalColor.getBlue() * (1 - k), 1);
                    }
                }
                pixelWriter.setColor(col, row, finalColor);
                ZBuffer[col][row] = z;
            }
        }
    }

     */

    private static float[] calculateBarycentricCoordinates(int x, int y, Point2f d0, Point2f d1, Point2f d2) {
        float denominator = (d1.y - d2.y) * (d0.x - d2.x) + (d2.x - d1.x) * (d0.y - d2.y);
        float alpha;
        float beta;
        float gamma;
        if (Float.isNaN(denominator)) {
            alpha = ((d1.y - d2.y) * (x - d2.x) + (d2.x - d1.x) * (y - d2.y));
            beta = ((d2.y - d0.y) * (x - d2.x) + (d0.x - d2.x) * (y - d2.y));
        } else {
            alpha = ((d1.y - d2.y) * (x - d2.x) + (d2.x - d1.x) * (y - d2.y)) / denominator;
            beta = ((d2.y - d0.y) * (x - d2.x) + (d0.x - d2.x) * (y - d2.y)) / denominator;
        }
        gamma = 1 - alpha - beta;
        return new float[]{clamp(alpha), clamp(beta), clamp(gamma)};
    }

    private static float clamp(float value) {
        if (!Float.isNaN(value)) {
            return (float) Math.max(0.0, Math.min(1.0, value));
        }
        return 0;
    }

    private static int coordinatesFix(int value, int max) {
        if (value < 0) {
            value = 0;
        }
        if (value >= max) {
            value = max - 1;
        }
        return value;
    }

    public static void drawLine(PixelWriter pixelWriter, int x0, int y0, float z0, int x1, int y1, float z1, Float[][] ZBuffer, int width, int height) {
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
                float z = beta * z0 + alpha * z1;
                if (ZBuffer[x0][y0] + 0.03 > z) {
                    pixelWriter.setColor(x0, y0, Color.BLACK);
                    ZBuffer[x0][y0] = (float) (z - 0.03);
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
}



