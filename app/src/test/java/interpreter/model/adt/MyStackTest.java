package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyStackTest {
    IStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new MyStack<>();
    }

    @Test
    void newStackEmpty() {
        assertTrue(stack.isEmpty());
    }

    @Test
    void stackNotEmptyAfterPush() {
        stack.push(2);
        assertFalse(stack.isEmpty());
    }

    @Test
    void popReturnsLIFOOrder() throws ADTException {
        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void stackEmptyAfterPoppingAll() throws ADTException {
        stack.push(1);
        stack.push(2);
        stack.pop();
        stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test
    void popEmptyStackThrows() {
        assertThrows(ADTException.class, () -> stack.pop());
    }

    @Test
    void toListReturnsTopFirst() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(List.of(3, 2, 1), stack.toList());
    }

    @Test
    void toListDoesNotChangeTheStack() throws ADTException {
        stack.push(1);
        stack.push(2);
        stack.toList();
        assertFalse(stack.isEmpty());
        assertEquals(2, stack.pop());
    }
}
