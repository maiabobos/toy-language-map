package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import interpreter.model.value.IValue;

import java.util.HashMap;
import java.util.Map;

public class MyHeap implements IHeap {
    private Map<Integer, IValue> heap = new HashMap<>();
    private int nextFree = 1;

    @Override
    public int allocate(IValue value) {
        int address = nextFree++;
        heap.put(address, value);
        return address;
    }

    @Override
    public IValue get(int address) throws ADTException {
        if(!heap.containsKey(address))
            throw new ADTException("Address not found in heap");
        return heap.get(address);
    }

    @Override
    public void update(int address, IValue value) throws ADTException {
        if(!heap.containsKey(address))
            throw new ADTException("Address not found in heap");
        heap.put(address, value);
    }

    @Override
    public boolean isDefined(int address) {
        return heap.containsKey(address);
    }

    @Override
    public Map<Integer, IValue> getContent() {
        return Map.copyOf(heap);
    }

    @Override
    public void setContent(Map<Integer, IValue> content) {
        heap = new HashMap<>(content);
    }

    @Override
    public String toString() {
        return heap.toString();
    }
}
