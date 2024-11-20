package com.cgvsu.model;

import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;

import java.util.*;

public class Model {

    public ArrayList<Vector3D> vertices = new ArrayList<>();
    public ArrayList<Vector2D> textureVertices = new ArrayList<>();
    public ArrayList<Vector3D> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();
}
