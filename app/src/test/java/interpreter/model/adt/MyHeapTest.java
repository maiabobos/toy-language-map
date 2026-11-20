package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class MyHeapTest {
    private IHeap heap;

    @BeforeEach
    void setUp() {
        heap = new MyHeap();
    }

    @Test
    void allocateReturnsAddressesOneTwoThree() {
        assertEquals(1, heap.allocate(new IntValue(10)));
        assertEquals(2, heap.allocate(new IntValue(20)));
        assertEquals(3, heap.allocate(new IntValue(30)));
    }

    @Test
    void isDefinedIsTrueOnlyForAllocatedAddresses() {
        heap.allocate(new IntValue(10));
        assertTrue(heap.isDefined(1));
        assertFalse(heap.isDefined(2));
    }

    // ---------- get ----------

    @Test
    void getReturnsTheStoredValue() throws ADTException {
        heap.allocate(new IntValue(10));
        heap.allocate(new BoolValue(true));
        assertEquals(new IntValue(10), heap.get(1));
        assertEquals(new BoolValue(true), heap.get(2));
    }

    @Test
    void getOfAMissingAddressThrows() {
        assertThrows(ADTException.class, () -> heap.get(1));
    }

    @Test
    void updateChangesTheValue() throws ADTException {
        heap.allocate(new IntValue(10));
        heap.update(1, new IntValue(99));
        assertEquals(new IntValue(99), heap.get(1));
    }

    @Test
    void updateKeepsTheOtherLocations() throws ADTException {
        heap.allocate(new IntValue(10));
        heap.allocate(new IntValue(20));
        heap.update(1, new IntValue(99));
        assertEquals(new IntValue(20), heap.get(2));
    }

    @Test
    void updateOfAMissingAddressThrows() {
        assertThrows(ADTException.class, () -> heap.update(1, new IntValue(5)));
    }

    @Test
    void getContentReturnsAllTheLocations() {
        heap.allocate(new IntValue(10));
        heap.allocate(new BoolValue(true));
        assertEquals(Map.of(1, new IntValue(10), 2, new BoolValue(true)), heap.getContent());
    }

    @Test
    void getContentIsAReadOnlyCopy() {
        heap.allocate(new IntValue(10));
        assertThrows(UnsupportedOperationException.class,
                () -> heap.getContent().put(9, new IntValue(1)));
    }

    @Test
    void setContentReplacesTheWholeContent() throws ADTException {
        heap.allocate(new IntValue(10));
        heap.allocate(new IntValue(20));
        heap.setContent(Map.of(2, new IntValue(20)));

        assertFalse(heap.isDefined(1));
        assertEquals(new IntValue(20), heap.get(2));
    }

    @Test
    void toStringShowsTheContent() {
        heap.allocate(new IntValue(10));
        assertTrue(heap.toString().contains("10"));
    }
}
