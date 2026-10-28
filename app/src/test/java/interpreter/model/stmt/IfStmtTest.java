package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.IExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IfStmtTest {
    private PrgState state;
    private final IStmt thenS = new PrintStmt(num(1));
    private final IStmt elseS = new PrintStmt(num(2));

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
    void pushesThenWhenCondTrue() throws MyException {
        new IfStmt(bool(true), thenS, elseS).execute(state);
        assertSame(thenS, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void pushesElseWhenCondFalse() throws MyException {
        new IfStmt(bool(false), thenS, elseS).execute(state);
        assertSame(elseS, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void doesntExecuteBranchItself() throws MyException {
        new IfStmt(bool(true), thenS, elseS).execute(state);
        assertEquals(0, state.getOut().size());
    }

    @Test
    void nonBooleanConditionThrows() {
        assertThrows(StatementException.class, () -> new IfStmt(num(1), thenS, elseS).execute(state));
    }

    @Test
    void stackUnchangedWhenConditionNotBoolean() {
        try {
            new IfStmt(num(1), thenS, elseS).execute(state);
        }
        catch (MyException ignored) {
        }
        assertTrue(state.getExeStack().isEmpty());
    }
}
