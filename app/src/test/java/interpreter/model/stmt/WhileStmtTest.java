package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.RelOp;
import interpreter.model.exp.RelationalExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhileStmtTest {
    private PrgState state;

    private static ValueExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private static ValueExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
    }

    private void run(IStmt... program) throws MyException {
        for (int i = program.length - 1; i >= 0; i--)
            state.getExeStack().push(program[i]);
        int steps = 0;
        while (!state.getExeStack().isEmpty()) {
            if (++steps > 10_000)
                throw new AssertionError("the program does not stop: is the while statement pushed back in the wrong order?");
            state.getExeStack().pop().execute(state);
        }
    }

    private int countIterations(int n) throws MyException {
        IStmt loop = new WhileStmt(
                new RelationalExp(RelOp.GT, new VarExp("v"), num(0)),
                new CompStmt(
                        new AssignStmt("c", new ArithExp(ArithOp.ADD, new VarExp("c"), num(1))),
                        new AssignStmt("v", new ArithExp(ArithOp.SUB, new VarExp("v"), num(1)))));
        run(new VarDeclStmt("c", new IntType()),
                new VarDeclStmt("v", new IntType()),
                new AssignStmt("v", num(n)),
                loop);
        return ((IntValue) state.getSymTable().lookup("c")).val();
    }

    @Test
    void runsZeroTimesWhenTheConditionStartsFalse() throws MyException {
        assertEquals(0, countIterations(0));
    }

    @Test
    void runsOnce() throws MyException {
        assertEquals(1, countIterations(1));
    }

    @Test
    void runsSeveralTimes() throws MyException {
        assertEquals(5, countIterations(5));
    }

    @Test
    void theConditionIsEvaluatedAgainAfterEveryIteration() throws MyException {
        countIterations(3);
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));      // stopped exactly when v reached 0
    }

    @Test
    void aTrueConditionPutsTheBodyOnTopOfTheLoop() throws MyException {
        IStmt body = new PrintStmt(num(1));
        var loop = new WhileStmt(bool(true), body);

        loop.execute(state);

        assertSame(body, state.getExeStack().pop());
        assertSame(loop, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void aFalseConditionLeavesTheStackUnchanged() throws MyException {
        new WhileStmt(bool(false), new PrintStmt(num(1))).execute(state);
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void executingTheLoopDoesNotRunTheBodyItself() throws MyException {
        new WhileStmt(bool(true), new PrintStmt(num(1))).execute(state);
        assertEquals(0, state.getOut().size());
    }

    @Test
    void aNonBooleanConditionThrows() {
        var loop = new WhileStmt(num(1), new NopStmt());
        assertThrows(StatementException.class, () -> loop.execute(state));
    }

    @Test
    void aFailedConditionLeavesTheStackUnchanged() {
        try {
            new WhileStmt(num(1), new NopStmt()).execute(state);
        } catch (MyException ignored) {
        }
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void anErrorInTheConditionIsPropagated() {
        var loop = new WhileStmt(new VarExp("undefined"), new NopStmt());
        assertThrows(ExpressionException.class, () -> loop.execute(state));
    }

    @Test
    void anErrorInTheBodyStopsTheLoop() {
        var loop = new WhileStmt(bool(true), new AssignStmt("undeclared", num(1)));
        assertThrows(StatementException.class, () -> run(loop));
    }

    @Test
    void toStringShowsTheConditionAndTheBody() {
        assertEquals("(while (c) nop)", new WhileStmt(new VarExp("c"), new NopStmt()).toString());
    }
}
