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

class LogicExpTest {
    private IDictionary<String, IValue> symT;

    @BeforeEach
    void setUp() {
        symT = new MyDictionary<>();
    }

    private static IExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    @Test
    void andOfTrueTrueIsTrue() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.AND, bool(true), bool(true)).eval(symT));
    }

    @Test
    void andOfTrueFalseIsFalse() throws MyException {
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(true), bool(false)).eval(symT));
    }

    @Test
    void andOfFalseFalseIsFalse() throws MyException {
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(false), bool(false)).eval(symT));
    }

    @Test
    void orOfFalseFalseIsFalse() throws MyException {
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.OR, bool(false), bool(false)).eval(symT));
    }

    @Test
    void orOfTrueFalseIsTrue() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(true), bool(false)).eval(symT));
    }

    @Test
    void orOfFalseTrueIsTrue() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(false), bool(true)).eval(symT));
    }

    @Test
    void nonBooleanOperandThrows() {
        IExp e = new LogicExp(LogicOp.AND, num(1), bool(true));
        assertThrows(ExpressionException.class, () -> e.eval(symT));
    }
}
