package interpreter.model.stmt;

import interpreter.exceptions.ADTException;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.IExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrintStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws ADTException {
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
    void appendsValToOut() throws MyException {
        new PrintStmt(num(5)).execute(state);
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(5), state.getOut().get(0));
    }

    @Test
    void printsInOrder() throws MyException {
        new PrintStmt(num(1)).execute(state);
        new PrintStmt(bool(true)).execute(state);
        assertEquals(new IntValue(1), state.getOut().get(0));
        assertEquals(new BoolValue(true), state.getOut().get(1));
    }

    @Test
    void printsValueOfVar() throws MyException {
        state.getSymTable().put("v", new IntValue(9));
        new PrintStmt(new VarExp("v")).execute(state);
        assertEquals(new IntValue(9), state.getOut().get(0));
    }

    @Test
    void errorInTheExpressionIsPropagatedAndNothingIsPrinted() {
        assertThrows(ExpressionException.class, () -> new PrintStmt(new VarExp("missing")).execute(state));
        assertEquals(0, state.getOut().size());
    }
}
