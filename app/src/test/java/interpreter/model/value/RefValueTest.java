package interpreter.model.value;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import org.junit.jupiter.api.Test;

class RefValueTest {
    @Test
    void typeIsRefOfLocationType() {
        assertEquals(new RefType(new IntType()), new RefValue(1, new IntType()).getType());
    }

    @Test
    void typeOfAReferenceToAReference() {
        var value = new RefValue(10, new RefType(new IntType()));
        assertEquals(new RefType(new RefType(new IntType())), value.getType());
    }

    @Test
    void valuesWithTheSameAddressAndLocationTypeAreEqual() {
        assertEquals(new RefValue(1, new IntType()), new RefValue(1, new IntType()));
    }

    @Test
    void differentAddressesAreNotEqual() {
        assertNotEquals(new RefValue(1, new IntType()), new RefValue(2, new IntType()));
    }

    @Test
    void differentLocationTypesAreNotEqual() {
        assertNotEquals(new RefValue(1, new IntType()), new RefValue(1, new BoolType()));
    }

    @Test
    void toStringShowsTheAddressAndTheLocationType() {
        assertEquals("(1, int)", new RefValue(1, new IntType()).toString());
        assertEquals("(2, Ref(int))", new RefValue(2, new RefType(new IntType())).toString());
    }
}

