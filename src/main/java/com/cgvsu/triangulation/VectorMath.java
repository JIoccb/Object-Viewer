package com.cgvsu.triangulation;

import com.cgvsu.math.vectors.Vector2D;

import static com.cgvsu.math.Global.EPS;

/**
 * A utility class for working with {@link Vector2D}
 */
final class VectorMath {

    /**
     * Prevents class instantiation.
     * @throws UnsupportedOperationException when called
     */
    private VectorMath() {
        throw new UnsupportedOperationException("Cannot be instantiated.");
    }

    /**
     * Calculates the cross product of vectors (BA) x (BC).
     * @param a A coordinates
     * @param b B coordinates
     * @param c C coordinates
     * @return cross product of vectors (BA) x (BC)
     */
    static double dis(Vector2D a, Vector2D b, Vector2D c) {
        double dx1 = b.get(0) - a.get(0);
        double dy1 = b.get(1) - a.get(1);
        double dx2 = c.get(0) - a.get(0);
        double dy2 = c.get(1) - a.get(1);

        return dx1 * dy2 - dx2 * dy1;
    }

    /**
     * Checks whether point P is inside of triangle ABC.
     * <p>Uses the cross product of three vectors.
     * @param a A coordinates
     * @param b B coordinates
     * @param c C coordinates
     * @param p P coordinates
     * @return true if P is inside ABC
     */
    static boolean isPointInTriangle(Vector2D a, Vector2D b, Vector2D c, Vector2D p) {
        double check1 = dis(a, b, p);
        double check2 = dis(p, b, c);
        double check3 = dis(p, c, a);

        return (check1 >= -EPS && check2 >= -EPS && check3 >= -EPS) ||
         (check1 <= EPS && check2 <= EPS && check3 <= EPS);
    }
}
