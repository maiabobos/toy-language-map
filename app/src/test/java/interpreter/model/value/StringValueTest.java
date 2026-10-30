package interpreter.model.value;

import interpreter.model.type.StringType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringValueTest {
    @Test
    void typeIsString() {
        assertEquals(new StringType(), new StringValue("a").getType());
    }

    @Test
    void valuesAreEqual() {
        assertEquals(new StringValue("ab"), new StringValue("ab"));
    }

    @Test
    void valuesNotEqual() {
        assertNotEquals(new StringValue("ac"), new StringValue("ab"));
    }

    @Test
    void toStringShowsActualString() {
        assertEquals("\"abc\"", new StringValue("abc").toString());
    }
}
