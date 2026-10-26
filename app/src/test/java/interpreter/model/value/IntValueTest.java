package interpreter.model.value;

import interpreter.model.type.IntType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntValueTest {
    @Test
    void typeIsInt() {
        assertEquals(new IntType(), new IntValue(5).getType());
    }

    @Test
    void valuesAreEqual() {
        assertEquals(new IntValue(5), new IntValue(5));
    }

    @Test
    void valuesNotEqual() {
        assertNotEquals(new IntValue(1), new IntValue(3));
    }

    @Test
    void intValueNotEqualBoolValue() {
        assertNotEquals(new IntValue(3), new BoolValue(true));
    }

    @Test
    void toStringShowsNumber() {
        assertEquals("5", new IntValue(5).toString());
    }
}
