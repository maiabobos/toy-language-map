package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationalExpTest {
    private IDictionary<String, IValue> symT;

    @BeforeEach
    void setUp() {
        symT = new MyDictionary<>();
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    @Test
    void ltTrueResultWorks() throws MyException {
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.LT, num(2), num(3)).eval(symT));
    }

    @Test
    void ltFalseResultWorks() throws MyException {
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.LT, num(2), num(1)).eval(symT));
    }

    @Test
    void leTrueResultWorks() throws MyException {
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.LE, num(2), num(4)).eval(symT));
    }

    @Test
    void leFalseResultWorks() throws MyException {
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.LE, num(2), num(1)).eval(symT));
    }

    @Test
    void eqTrueResultWorks() throws MyException {
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.EQ, num(3), num(3)).eval(symT));
    }

    @Test
    void eqFalseResultWorks() throws MyException {
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.EQ, num(2), num(4)).eval(symT));
    }

    @Test
    void neTrueResultWorks() throws MyException {
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.NE, num(2), num(3)).eval(symT));
    }

    @Test
    void neFalseResultWorks() throws MyException {
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.NE, num(2), num(2)).eval(symT));
    }

    @Test
    void gtTrueResultWorks() throws MyException {
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.GT, num(5), num(3)).eval(symT));
    }

    @Test
    void gtFalseResultWorks() throws MyException {
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.GT, num(0), num(1)).eval(symT));
    }

    @Test
    void geTrueResultWorks() throws MyException {
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.GE, num(5), num(3)).eval(symT));
    }

    @Test
    void geFalseResultWorks() throws MyException {
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.GE, num(2), num(7)).eval(symT));
    }

    @Test
    void nonIntegerOperandThrows() {
        assertThrows(ExpressionException.class, () -> new RelationalExp(RelOp.EQ, num(2), new ValueExp(new StringValue("a"))).eval(symT));
    }

}
