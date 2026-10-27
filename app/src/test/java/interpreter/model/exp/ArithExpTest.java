package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArithExpTest {
    private IDictionary<String, IValue> symT;

    @BeforeEach
    void setUp() {
        symT = new MyDictionary<>();
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private static IExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    @Test
    void addsTwoNumbers() throws MyException {
        assertEquals(new IntValue(5), new ArithExp(ArithOp.ADD, num(2), num(3)).eval(symT));
    }

    @Test
    void subtractsTwoNumbers() throws MyException {
        assertEquals(new IntValue(5), new ArithExp(ArithOp.SUB, num(8), num(3)).eval(symT));
    }

    @Test
    void multipliesTwoNumbers() throws MyException {
        assertEquals(new IntValue(15), new ArithExp(ArithOp.MUL, num(5), num(3)).eval(symT));
    }

    @Test
    void dividesTwoNumbers() throws MyException {
        assertEquals(new IntValue(5), new ArithExp(ArithOp.DIV, num(21), num(4)).eval(symT));
    }

    @Test
    void respectOperatorPrecedence() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD, num(2),
                new ArithExp(ArithOp.MUL, num(3), num(5)));
        assertEquals(new IntValue(17), e.eval(symT));
    }

    @Test
    void divisionByZeroThrows() {
        IExp e = new ArithExp(ArithOp.DIV, num(1), num(0));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symT));
        assertEquals("Division by zero", ex.getMessage());
    }

    @Test
    void booleanOperandThrows() {
        IExp e = new ArithExp(ArithOp.ADD, bool(true), num(1));
        assertThrows(ExpressionException.class, () -> e.eval(symT));
    }

    @Test
    void usesVariablesFromSymTable() throws MyException {
        symT.put("a", new IntValue(2));
        IExp e = new ArithExp(ArithOp.ADD, new VarExp("a"), num(1));
        assertEquals(new IntValue(3), e.eval(symT));
    }

    @Test
    void evaluationDoesntChangeSymTable() throws MyException {
        symT.put("a", new IntValue(4));
        new ArithExp(ArithOp.MUL, new VarExp("a"), num(2)).eval(symT);
        assertEquals(new IntValue(4), symT.lookup("a"));
    }
}
