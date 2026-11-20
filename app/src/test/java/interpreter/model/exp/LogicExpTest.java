package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicExpTest {
    private IDictionary<String, IValue> symT;
    private IHeap heap;

    @BeforeEach
    void setUp() {
        symT = new MyDictionary<>();
        heap = new MyHeap();
    }

    private static IExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    @Test
    void andOfTrueTrueIsTrue() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.AND, bool(true), bool(true)).eval(symT, heap));
    }

    @Test
    void andOfTrueFalseIsFalse() throws MyException {
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(true), bool(false)).eval(symT, heap));
    }

    @Test
    void andOfFalseFalseIsFalse() throws MyException {
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(false), bool(false)).eval(symT, heap));
    }

    @Test
    void orOfFalseFalseIsFalse() throws MyException {
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.OR, bool(false), bool(false)).eval(symT, heap));
    }

    @Test
    void orOfTrueFalseIsTrue() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(true), bool(false)).eval(symT, heap));
    }

    @Test
    void orOfFalseTrueIsTrue() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(false), bool(true)).eval(symT, heap));
    }

    @Test
    void nonBooleanOperandThrows() {
        IExp e = new LogicExp(LogicOp.AND, num(1), bool(true));
        assertThrows(ExpressionException.class, () -> e.eval(symT, heap));
    }
}
