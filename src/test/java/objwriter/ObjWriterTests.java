package objwriter;

import com.cgvsu.math.vectors.Vector2D;
import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.objwriter.ObjWriter;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ObjWriterTests {

    /*@ParameterizedTest
    @CsvSource({"2.3f, -4.54f, 0", "0, 0, 0", "132.2652624646f, 0.0131f, -5.5437f", "-2.8f, -0.0, 9.3"})
    public void vertexToStringTest(float x, float y, float z) {
        String result = ObjWriter.vertexToString(new Vector3D(new double[]{x, y, z}));
        String[] array = result.split(" ");
        Assertions.assertEquals("v", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
        Assertions.assertEquals(z, Float.parseFloat(array[3]));
    }

    @ParameterizedTest
    @CsvSource({"2.3f, -4.54f", "0, 0", "132.2652624646f, 0.0131f", "-2.8f, -0.0"})
    public void textureVertexToStringTest(float x, float y) {
        String result = ObjWriter.textureVertexToString(new Vector2D(new double[]{x, y}));
        String[] array = result.split(" ");
        Assertions.assertEquals("vt", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
    }

    @ParameterizedTest
    @CsvSource({"2.3f, -4.54f, 0", "0, 0, 0", "132.2652624646f, 0.0131f, -5.5437f", "-2.8f, -0.0, 9.3"})
    public void normalToStringTest(float x, float y, float z) {
        String result = ObjWriter.normalToString(new Vector3D(new double[]{x, y, z}));
        String[] array = result.split(" ");
        Assertions.assertEquals("vn", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
        Assertions.assertEquals(z, Float.parseFloat(array[3]));
    }*/

    @Test
    public void polygonToStringTestWithOnlyVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        String result = ObjWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1 2 3", result);
    }

    @Test
    public void polygonToStringTestWithTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 5)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(3, 5, 4, 2)));
        String result = ObjWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1/4 2/6 3/5 6/3", result);
    }

    @Test
    public void polygonToStringTestWithNormalIndicesAndWithoutTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 5)));
        polygon.setNormalIndices(new ArrayList<>(List.of(3, 5, 4, 2)));
        String result = ObjWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1//4 2//6 3//5 6//3", result);
    }

    @Test
    public void polygonToStringTestWithNormalIndicesAndWithTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 5)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(7, 4, 3, 6)));
        polygon.setNormalIndices(new ArrayList<>(List.of(3, 5, 4, 2)));
        String result = ObjWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1/8/4 2/5/6 3/4/5 6/7/3", result);
    }

    /*@ParameterizedTest
    @ValueSource(strings = {"test.obj", "test", "тест.obj", "анонимная модель.obj", "../папка/ещё папка/моделька.obj", "../../meow.obj"})
    public void writeTest(String testFilename) throws Exception {
        Model model = new Model();
        model.vertices = new ArrayList<>(List.of(
                new Vector3D(new double[]{0, 0, 0}),
                new Vector3D(new double[]{1, 1.2f, 3.6f}),
                new Vector3D(new double[]{-2, -4.45f, 7f}),
                new Vector3D(new double[]{-1.5f, -4.45f, 6.5f}),
                new Vector3D(new double[]{10f, 11f, 0})
        ));
        Polygon polygon1 = new Polygon();
        Polygon polygon2 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 3)));
        polygon2.setVertexIndices(new ArrayList<>(List.of(2, 4, 3)));
        model.polygons = new ArrayList<>(List.of(
                polygon1, polygon2
        ));
        ObjWriter.write(model, testFilename);
        Path path = Path.of(testFilename);
        String separator = System.lineSeparator();
        File file = path.toFile();
        String content = Files.readString(path, StandardCharsets.UTF_8);
        Assertions.assertEquals(
                "v 0.0 0.0 0.0" + separator +
                        "v 1.0 1.2 3.6" + separator +
                        "v -2.0 -4.45 7.0" + separator +
                        "v -1.5 -4.45 6.5" + separator +
                        "v 10.0 11.0 0.0" + separator +
                        "f 1 2 4" + separator +
                        "f 3 5 4" + separator,
                content
        );
        int depth = testFilename.split(File.pathSeparator).length - 1;
        if (depth == 0) depth = 1;
        while (depth > 0 && file.delete()) {
            file = file.getParentFile();
            depth--;
        }
    }*/
}
