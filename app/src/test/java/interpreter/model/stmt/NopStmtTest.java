package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NopStmtTest {
    @Test
    void leavesStateUnchanged() throws MyException {
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();

        state.getSymTable().put("v", new IntValue(1));
        state.getOut().add(new IntValue(2));

        new NopStmt().execute(state);

        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(1), state.getSymTable().lookup("v"));
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }
}
