package interpreter.model.type;

import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BoolTypeTest {
    @Test
    void defaultValueIsFalse() {
        assertEquals(new BoolValue(false), new BoolType().defaultValue());
    }

    @Test
    void twoBoolTypesEqual() {
        assertEquals(new BoolType(), new BoolType());
    }

    @Test
    void toStringIsInt() {
        assertEquals("bool", new BoolType().toString());
    }
}
