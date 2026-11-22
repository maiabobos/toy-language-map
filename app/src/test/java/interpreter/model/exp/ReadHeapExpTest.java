package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NewStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadHeapExpTest {
    private PrgState state;

    private static ValueExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private IValue eval(IExp exp) throws MyException {
        return exp.eval(state.getSymTable(), state.getHeap());
    }

    // state of Ex6:  v -> (1, int), a -> (2, Ref(int)),  heap {1 -> 20, 2 -> (1, int)}
    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
        new NewStmt("v", num(20)).execute(state);
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        new NewStmt("a", new VarExp("v")).execute(state);
    }

    @Test
    void readsTheValueAtTheAddress() throws MyException {
        assertEquals(new IntValue(20), eval(new ReadHeapExp(new VarExp("v"))));
    }

    @Test
    void readingOneLevelOfANestedReferenceGivesTheInnerReference() throws MyException {
        assertEquals(new RefValue(1, new IntType()), eval(new ReadHeapExp(new VarExp("a"))));
    }

    @Test
    void readsThroughTwoLevels() throws MyException {
        var twice = new ReadHeapExp(new ReadHeapExp(new VarExp("a")));
        assertEquals(new IntValue(20), eval(twice));
    }

    @Test
    void worksInsideAnArithmeticExpression() throws MyException {
        var sum = new ArithExp(ArithOp.ADD, new ReadHeapExp(new ReadHeapExp(new VarExp("a"))), num(5));
        assertEquals(new IntValue(25), eval(sum));
    }

    @Test
    void seesTheCurrentContentOfTheHeap() throws MyException {
        state.getHeap().update(1, new IntValue(30));
        assertEquals(new IntValue(30), eval(new ReadHeapExp(new VarExp("v"))));
    }

    @Test
    void anExpressionThatIsNotAReferenceThrows() {
        assertThrows(ExpressionException.class, () -> eval(new ReadHeapExp(num(1))));
    }

    @Test
    void theNullReferenceThrows() throws MyException {
        new VarDeclStmt("n", new RefType(new IntType())).execute(state);
        assertThrows(ExpressionException.class, () -> eval(new ReadHeapExp(new VarExp("n"))));
    }

    @Test
    void anAddressThatIsNotInTheHeapThrows() {
        state.getSymTable().put("dangling", new RefValue(9, new IntType()));
        assertThrows(ExpressionException.class, () -> eval(new ReadHeapExp(new VarExp("dangling"))));
    }

    @Test
    void anUndefinedVariableInsideThrows() {
        assertThrows(ExpressionException.class, () -> eval(new ReadHeapExp(new VarExp("undefined"))));
    }

    @Test
    void toStringUsesTheSpecForm() {
        assertEquals("RH(v)", new ReadHeapExp(new VarExp("v")).toString());
    }
}
