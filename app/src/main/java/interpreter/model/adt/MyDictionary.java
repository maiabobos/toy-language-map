package interpreter.model.adt;

import interpreter.exceptions.ADTException;

import java.util.HashMap;
import java.util.Map;

public class MyDictionary<K, V> implements IDictionary<K, V> {
    private final Map<K, V> dictionary = new HashMap<>();

    @Override
    public boolean isDefined(K key) {
            if(dictionary.containsKey(key))
                return true;
            return false;
    }

    @Override
    public V lookup(K key) throws ADTException {
        if(dictionary.containsKey(key))
            return dictionary.get(key);
        throw new ADTException("Key " + key + " is not defined");
    }

    @Override
    public void put(K key, V val) {
        dictionary.put(key, val);
    }

    @Override
    public void update(K key, V val) throws ADTException {
        if (!dictionary.containsKey(key))
            throw new ADTException("Cannot update undefined key " + key);
        dictionary.put(key, val);
    }

    @Override
    public String toString() {
        return dictionary.toString();
    }
}
