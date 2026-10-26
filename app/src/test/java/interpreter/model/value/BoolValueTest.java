package interpreter.model.value;

import interpreter.model.type.BoolType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BoolValueTest {
    @Test
    void typeIsBool() {
        assertEquals(new BoolType(), new BoolValue(true).getType());
    }

    @Test
    void valuesAreEqual() {
        assertEquals(new BoolValue(true), new BoolValue(true));
    }

    @Test
    void valuesNotEqual() {
        assertNotEquals(new BoolValue(true), new BoolValue(false));
    }

    @Test
    void toStringShowsCorrect() {
        assertEquals("true", new BoolValue(true).toString());
    }
}
