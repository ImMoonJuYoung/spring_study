package hello.servlet.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LombokTest {

    @Test
    void testLombokAnnotations() {
        TestPerson testPerson = new TestPerson("문", 30);

        testPerson.setName("Moon");
        assertEquals("Moon", testPerson.getName());

        String toString = testPerson.toString();
        System.out.println("toString = " + toString);
        assertTrue(toString.contains("Moon"));
        assertTrue(toString.contains("30"));

    }
}
