package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class GarbageCollectorTest {
    private final List<IValue> symTable = List.of(
            new RefValue(3, new IntType()), new RefValue(2, new RefType(new IntType())));
    private final Map<Integer, IValue> heap = Map.of(
            1, new IntValue(20), 2, new RefValue(1, new IntType()),
            3, new IntValue(30), 4, new IntValue(7));

    private static RefValue ref(int address) {
        return new RefValue(address, new IntType());
    }

    @Test
    void theAddressesOfTheSymTableAreTheAddressesOfItsReferences() {
        assertEquals(Set.of(2, 3), GarbageCollector.getAddrFromSymTable(symTable));
    }

    @Test
    void valuesThatAreNotReferencesAreIgnored() {
        List<IValue> values = List.of(new IntValue(5), new BoolValue(true), ref(2));
        assertEquals(Set.of(2), GarbageCollector.getAddrFromSymTable(values));
    }

    @Test
    void twoReferencesToTheSameLocationGiveOneAddress() {
        assertEquals(Set.of(2), GarbageCollector.getAddrFromSymTable(List.of(ref(2), ref(2))));
    }

    @Test
    void anEmptySymTableHasNoAddresses() {
        assertTrue(GarbageCollector.getAddrFromSymTable(List.of()).isEmpty());
    }

    @Test
    void aRootThatPointsToNothingReachesOnlyItself() {
        assertEquals(Set.of(3), GarbageCollector.reachableAddresses(Set.of(3), heap));
    }

    @Test
    void aRootReachesTheLocationItsContentRefersTo() {
        assertEquals(Set.of(2, 1), GarbageCollector.reachableAddresses(Set.of(2), heap));
    }

    @Test
    void noRootsReachNothing() {
        assertTrue(GarbageCollector.reachableAddresses(Set.of(), heap).isEmpty());
    }

    @Test
    void followsAChainOfReferences() {
        Map<Integer, IValue> chain = Map.of(1, ref(2), 2, ref(3), 3, new IntValue(5), 4, new IntValue(6));
        assertEquals(Set.of(1, 2, 3), GarbageCollector.reachableAddresses(Set.of(1), chain));
    }

    @Test
    void terminatesOnACycle() {
        Map<Integer, IValue> cycle = Map.of(1, ref(2), 2, ref(1));
        assertEquals(Set.of(1, 2), GarbageCollector.reachableAddresses(Set.of(1), cycle));
    }

    // ---------- safeGarbageCollector ----------

    @Test
    void keepsTheLocationsReachableThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);
        assertEquals(Set.of(1, 2, 3), kept.keySet());          // 4 is garbage, 1 is not
    }

    @Test
    void removesTheLocationsNobodyCanReach() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        assertFalse(GarbageCollector.safeGarbageCollector(roots, heap).containsKey(4));
    }

    @Test
    void keptLocationsKeepTheirValues() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);
        assertEquals(new IntValue(20), kept.get(1));
        assertEquals(new RefValue(1, new IntType()), kept.get(2));
    }

    @Test
    void keepsEveryLocationOfAChain() {
        Map<Integer, IValue> chain = Map.of(1, ref(2), 2, ref(3), 3, new IntValue(5), 4, new IntValue(6));
        assertEquals(Set.of(1, 2, 3), GarbageCollector.safeGarbageCollector(Set.of(1), chain).keySet());
    }

    @Test
    void terminatesAndKeepsBothLocationsOfACycle() {
        Map<Integer, IValue> cycle = Map.of(1, ref(2), 2, ref(1), 3, new IntValue(9));
        assertEquals(Set.of(1, 2), GarbageCollector.safeGarbageCollector(Set.of(1), cycle).keySet());
    }

    @Test
    void withoutRootsEverythingIsGarbage() {
        assertTrue(GarbageCollector.safeGarbageCollector(Set.of(), heap).isEmpty());
    }

    @Test
    void theNullAddressAndAddressesNotInTheHeapAreIgnored() {
        Map<Integer, IValue> small = Map.of(1, new IntValue(20));
        assertTrue(GarbageCollector.safeGarbageCollector(Set.of(0, 9), small).isEmpty());
    }

    @Test
    void aReferenceInsideTheHeapToAMissingLocationDoesNotBreakTheCollector() {
        Map<Integer, IValue> dangling = Map.of(1, ref(9));
        assertEquals(Set.of(1), GarbageCollector.safeGarbageCollector(Set.of(1), dangling).keySet());
    }

    @Test
    void theInputHeapIsNotModified() {
        Map<Integer, IValue> mutable = new HashMap<>(heap);
        GarbageCollector.safeGarbageCollector(Set.of(3), mutable);
        assertEquals(heap, mutable);
    }

    @Test
    void theUnsafeCollectorLosesALocationThatIsReachableOnlyThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.unsafeGarbageCollector(roots, heap);
        assertEquals(Set.of(2, 3), kept.keySet());
    }
}
