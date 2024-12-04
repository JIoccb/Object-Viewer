package objreader;

import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class ObjReaderTest {

    @Test
    void testParseVertex01() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        Vector3D result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3D expectedResult = new Vector3D(new double[]{1.01d, 1.02d, 1.03d});
        Assertions.assertEquals(result, expectedResult);
    }

    @Test
    public void testParseVertex02() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        Vector3D result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3D expectedResult = new Vector3D(new double[]{1.01d, 1.02d, 1.10d});
        Assertions.assertNotEquals(result, expectedResult);
    }

    @Test
    public void testParseVertex03() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("ab", "o", "ba"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Failed to parse float value.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex04() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Too few vertex arguments.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex05() {
        // АГААА! Вот тест, который говорит, что у метода нет проверки на более, чем 3 числа
        // А такой случай лучше не игнорировать, а сообщать пользователю, что у него что-то не так
        // ассерт, чтобы не забыть про тест:
        Assertions.fail();


        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0", "3.0", "4.0"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }
}