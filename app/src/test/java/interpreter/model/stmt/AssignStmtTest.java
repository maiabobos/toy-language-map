package interpreter.model.stmt;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.*;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssignStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private static IExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    @Test
    void assignsEvaluatedValue() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        new AssignStmt("v", num(2)).execute(state);
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
    }

    @Test
    void assignsResultOfExpressionUsingOtherVariables() throws MyException {
        new VarDeclStmt("a", new IntType()).execute(state);
        new VarDeclStmt("b", new IntType()).execute(state);
        new AssignStmt("a", num(4)).execute(state);
        new AssignStmt("b", new ArithExp(ArithOp.ADD, new VarExp("a"), num(1))).execute(state);
        assertEquals(new IntValue(5), state.getSymTable().lookup("b"));
    }

    @Test
    void assignsBooleanToBoolVariable() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        new AssignStmt("b", bool(true)).execute(state);
        assertEquals(new BoolValue(true), state.getSymTable().lookup("b"));
    }

    @Test
    void assigningToUndeclaredVariableThrows() {
        assertThrows(StatementException.class, () -> new AssignStmt("x", num(1)).execute(state));
    }

    @Test
    void assigningBooleanToIntVariableThrows() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        assertThrows(StatementException.class, () -> new AssignStmt("v", bool(true)).execute(state));
    }

    @Test
    void assigningIntToBoolVariableThrows() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        assertThrows(StatementException.class, () -> new AssignStmt("b", num(1)).execute(state));
    }

    @Test
    void failedAssignmentLeavesOldValue() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        new AssignStmt("v", num(3)).execute(state);
        try {
            new AssignStmt("v", bool(true)).execute(state);
        } catch (StatementException ignored) {
        }
        assertEquals(new IntValue(3), state.getSymTable().lookup("v"));
    }

    @Test
    void errorInExpressionPropagated() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        assertThrows(ExpressionException.class, () -> new AssignStmt("v", new VarExp("undefinedVar")).execute(state));
    }
}
