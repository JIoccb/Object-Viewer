package math;

import com.cgvsu.math.vectors.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VectorTest {

    Vector vector = new Vector(new double[]{1, 2, 3, 4});

    @Test
    void getLength() {
        assertEquals(vector.getLength(), 4);
    }

    @Test
    void get() {
        assertEquals(vector.get(2), 3);
    }

    @Test
    void norm() {
        assertEquals(vector.norm(), 5.477225575051661);
    }

    @Test
    void normalize() {
        assertEquals(vector.normalize(), new Vector(new double[]{0.18257418583505536, 0.3651483716701107, 0.5477225575051661, 0.7302967433402214}));
    }

    @Test
    void scale() {
        assertEquals(vector.scale(2), new Vector(new double[]{2, 4, 6, 8}));
    }
}