package com.cgvsu.model;

import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;

public class VertexObject {
    private int x;
    private int y;
    private double z;
    private Vector2D textureVert;
    private Vector3D normal;

    public VertexObject(int x, int y, double z, Vector2D textureVert, Vector3D normal) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.textureVert = textureVert;
        this.normal = normal;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector2D getTextureVert() {
        return textureVert;
    }

    public void setTextureVert(Vector2D textureVert) {
        this.textureVert = textureVert;
    }

    public Vector3D getNormal() {
        return normal;
    }

    public void setNormal(Vector3D normal) {
        this.normal = normal;
    }
}
