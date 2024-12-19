package com.cgvsu.rasterization;

import java.util.Arrays;

public class Z_Buffer {
    private int width;
    private int height;
    private double z;
    private double[][] zBuffer = new double[width][height];

    public Z_Buffer(int width, int height) {
        this.width = width;
        this.height = height;
        double value = Double.MAX_VALUE;

        for (int i = 0; i < width; i++){ // заполнения буфера одинаковыми большими значениями
            Arrays.fill(zBuffer[i], value);
        }
    }
    public void set(int x, int y, double z){
        zBuffer[x][y] = z;
    }
    public double get(int x, int y){
        return zBuffer[x][y];
    }
}

