package interpreter.model.type;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

class RefTypeTest {
    private final IType intRef = new RefType(new IntType());
    private final IType intRefRef = new RefType(new RefType(new IntType()));

    @Test
    void defaultValueIsTheNullReferenceToTheInnerType() {
        assertEquals(new RefValue(0, new IntType()), intRef.defaultValue());
    }

    @Test
    void defaultValueOfANestedRefPointsToTheInnerRefType() {
        assertEquals(new RefValue(0, new RefType(new IntType())), intRefRef.defaultValue());
    }

    @Test
    void aDeclaredRefVariableGetsTheNullReferenceOfItsLocationType() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", intRef).execute(state);
        new VarDeclStmt("w", intRefRef).execute(state);

        assertEquals(new RefValue(0, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new RefValue(0, new RefType(new IntType())), state.getSymTable().lookup("w"));
    }

    @Test
    void nestedTypesWithTheSameStructureAreEqual() {
        assertEquals(new RefType(new RefType(new IntType())), intRefRef);
    }

    @Test
    void refRefIntIsDifferentFromRefInt() {
        assertNotEquals(intRefRef, intRef);
    }

    @Test
    void refIntIsDifferentFromRefBool() {
        assertNotEquals(intRef, new RefType(new BoolType()));
    }

    @Test
    void refIntIsDifferentFromInt() {
        assertNotEquals(intRef, new IntType());
    }

    @Test
    void equalTypesHaveTheSameHashCode() {
        assertEquals(new RefType(new RefType(new IntType())).hashCode(), intRefRef.hashCode());
    }

    @Test
    void toStringShowsTheNesting() {
        assertEquals("Ref(int)", intRef.toString());
        assertEquals("Ref(Ref(int))", intRefRef.toString());
    }
}
