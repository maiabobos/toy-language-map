package interpreter.model.adt;

import interpreter.exceptions.ADTException;

public interface IDictionary<K, V> {
    boolean isDefined(K key);
    V lookup(K key) throws ADTException;
    void put(K key, V val);
    void update(K key, V val) throws ADTException;
}
