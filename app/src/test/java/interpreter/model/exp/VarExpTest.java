package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VarExpTest {
    private IDictionary<String, IValue> symT;

    @BeforeEach
    void setUp() {
        symT = new MyDictionary<>();
    }

    @Test
    void returnValueOfDefinedVar() throws MyException {
        symT.put("a", new IntValue(2));
        assertEquals(new IntValue(2), new VarExp("a").eval(symT));
    }

    @Test
    void undefinedVarThrows() {
        assertThrows(ExpressionException.class, () -> new VarExp("x").eval(symT));
    }
}
