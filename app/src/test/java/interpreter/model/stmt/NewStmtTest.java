package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class NewStmtTest {
    private PrgState state;

    private static ValueExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
    }

    @Test
    void allocatesAtAddressOneAndUpdatesTheVariable() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void aSecondNewGetsTheNextAddressAndLeavesTheOldLocationInTheHeap() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new NewStmt("v", num(30)).execute(state);

        assertEquals(new RefValue(2, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(Map.of(1, new IntValue(20), 2, new IntValue(30)), state.getHeap().getContent());
    }

    @Test
    void newOfANestedReferenceStoresTheInnerReference() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        new NewStmt("a", new VarExp("v")).execute(state);

        assertEquals(new RefValue(2, new RefType(new IntType())), state.getSymTable().lookup("a"));
        assertEquals(new RefValue(1, new IntType()), state.getHeap().get(2));
    }

    @Test
    void variableThatIsNotDeclaredThrows() {
        assertThrows(StatementException.class, () -> new NewStmt("nope", num(1)).execute(state));
    }

    @Test
    void variableThatIsNotAReferenceThrows() throws MyException {
        new VarDeclStmt("n", new IntType()).execute(state);
        assertThrows(StatementException.class, () -> new NewStmt("n", num(1)).execute(state));
    }

    @Test
    void valueOfTheWrongTypeThrows() {
        var exp = new ValueExp(new BoolValue(true));
        assertThrows(StatementException.class, () -> new NewStmt("v", exp).execute(state));
    }

    @Test
    void valueOfTheWrongTypeForANestedReferenceThrows() throws MyException {
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        assertThrows(StatementException.class, () -> new NewStmt("a", num(5)).execute(state));
    }

    @Test
    void aFailedNewAllocatesNothingAndLeavesTheVariableUnchanged() throws MyException {
        try {
            new NewStmt("v", new ValueExp(new BoolValue(true))).execute(state);
        } catch (StatementException ignored) {
        }
        assertTrue(state.getHeap().getContent().isEmpty());
        assertEquals(new RefValue(0, new IntType()), state.getSymTable().lookup("v"));
    }

    @Test
    void anErrorInTheExpressionIsPropagated() {
        assertThrows(ExpressionException.class,
                () -> new NewStmt("v", new VarExp("undefined")).execute(state));
    }

    @Test
    void toStringUsesTheSpecForm() {
        assertEquals("New(v, 20)", new NewStmt("v", num(20)).toString());
    }
}
