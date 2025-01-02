package com.cgvsu.rasterization;

import java.util.Arrays;

public class Z_Buffer {
    private final double[][] zBuffer;

    public Z_Buffer(int width, int height) {
        zBuffer = new double[width][height];

        for (int i = 0; i < width; i++) {
            // заполнения буфера одинаковыми большими значениями
            Arrays.fill(zBuffer[i], Double.MAX_VALUE);
        }

    }

    public void set(int x, int y, double z) {
        zBuffer[x][y] = z;
    }

    public double get(int x, int y) {
        return zBuffer[x][y];
    }

    public int getWidth() {
        return zBuffer.length;
    }

    public int getHeight() {
        return zBuffer[0].length;
    }
}

