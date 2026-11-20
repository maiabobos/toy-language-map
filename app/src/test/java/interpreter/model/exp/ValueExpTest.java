package interpreter.model.exp;

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

class ValueExpTest {
    private IDictionary<String, IValue> symT;
    private IHeap heap;

    @BeforeEach
    void setUp() {
        symT = new MyDictionary<>();
        heap = new MyHeap();
    }

    @Test
    void returnsInteger() throws MyException {
        assertEquals(new IntValue(7), new ValueExp(new IntValue(7)).eval(symT, heap));
    }

    @Test
    void returnsTheBool() throws MyException {
        assertEquals(new BoolValue(true), new ValueExp(new BoolValue(true)).eval(symT, heap));
    }
}
