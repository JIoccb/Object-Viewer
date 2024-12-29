package math;

import com.cgvsu.math.vectors.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VectorTest {

    Vector vector = new Vector(new double[]{1, 2, 3, 4});

    @Test
    void getLength() {
        assertEquals(4, vector.getLength());
    }

    @Test
    void get() {
        assertEquals(3, vector.get(2));
    }

    @Test
    void norm() {
        assertEquals(5.477225575051661, vector.norm());
    }

    @Test
    void normalize() {
        assertEquals(new Vector(new double[]{0.18257418583505536, 0.3651483716701107, 0.5477225575051661, 0.7302967433402214}), vector.normalize());
    }

    @Test
    void scale() {
        assertEquals(new Vector(new double[]{2, 4, 6, 8}), vector.scale(2));
    }
}