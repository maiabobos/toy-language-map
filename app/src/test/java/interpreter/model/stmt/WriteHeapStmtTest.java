package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class WriteHeapStmtTest {
    private PrgState state;

    private static ValueExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);       // Ref int v
        new NewStmt("v", num(20)).execute(state);                              // v -> (1, int), heap {1 -> 20}
    }

    @Test
    void changesTheHeapLocation() throws MyException {
        new WriteHeapStmt("v", num(30)).execute(state);
        assertEquals(new IntValue(30), state.getHeap().get(1));
    }

    @Test
    void doesNotAllocateAndDoesNotChangeTheVariable() throws MyException {
        new WriteHeapStmt("v", num(30)).execute(state);
        assertEquals(1, state.getHeap().getContent().size());
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
    }

    @Test
    void everyVariableThatPointsToTheLocationSeesTheNewValue() throws MyException {
        state.getSymTable().put("w", new RefValue(1, new IntType()));
        new WriteHeapStmt("w", num(77)).execute(state);
        assertEquals(new IntValue(77), state.getHeap().get(1));
    }

    @Test
    void canStoreAReferenceInANestedLocation() throws MyException {
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        new NewStmt("a", new VarExp("v")).execute(state);
        new NewStmt("v", num(40)).execute(state);

        new WriteHeapStmt("a", new VarExp("v")).execute(state);
        assertEquals(new RefValue(3, new IntType()), state.getHeap().get(2));
    }

    @Test
    void variableThatIsNotDeclaredThrows() {
        assertThrows(StatementException.class, () -> new WriteHeapStmt("nope", num(1)).execute(state));
    }

    @Test
    void variableThatIsNotAReferenceThrows() throws MyException {
        new VarDeclStmt("n", new IntType()).execute(state);
        assertThrows(StatementException.class, () -> new WriteHeapStmt("n", num(1)).execute(state));
    }

    @Test
    void anAddressThatIsNotInTheHeapThrows() {
        state.getSymTable().put("dangling", new RefValue(9, new IntType()));
        assertThrows(StatementException.class, () -> new WriteHeapStmt("dangling", num(1)).execute(state));
    }

    @Test
    void theNullReferenceThrows() throws MyException {
        new VarDeclStmt("n", new RefType(new IntType())).execute(state);
        assertThrows(StatementException.class, () -> new WriteHeapStmt("n", num(1)).execute(state));
    }

    @Test
    void valueOfTheWrongTypeThrows() {
        var exp = new ValueExp(new BoolValue(true));
        assertThrows(StatementException.class, () -> new WriteHeapStmt("v", exp).execute(state));
    }

    @Test
    void valueOfTheWrongTypeForANestedLocationThrows() throws MyException {
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        new NewStmt("a", new VarExp("v")).execute(state);
        assertThrows(StatementException.class, () -> new WriteHeapStmt("a", num(5)).execute(state));
    }

    @Test
    void aFailedWriteLeavesTheHeapUnchanged() {
        try {
            new WriteHeapStmt("v", new ValueExp(new BoolValue(true))).execute(state);
        } catch (MyException ignored) {
        }
        assertEquals(Map.of(1, new IntValue(20)), state.getHeap().getContent());
    }

    @Test
    void anErrorInTheExpressionIsPropagated() {
        assertThrows(ExpressionException.class,
                () -> new WriteHeapStmt("v", new VarExp("undefined")).execute(state));
    }

    @Test
    void toStringUsesTheSpecForm() {
        assertEquals("WH(v, 30)", new WriteHeapStmt("v", num(30)).toString());
    }
}
