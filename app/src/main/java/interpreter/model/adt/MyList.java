package interpreter.model.adt;

import interpreter.exceptions.ADTException;

import java.util.ArrayList;
import java.util.List;

public class MyList<T> implements IList<T> {
    private final List<T> list = new ArrayList<>();

    @Override
    public void add(T v) {
        list.add(v);
    }

    @Override
    public T get(int pos) throws ADTException {
        if (list.size() <= pos || pos < 0)
            throw new ADTException("No element found at index " + pos);
        return list.get(pos);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
