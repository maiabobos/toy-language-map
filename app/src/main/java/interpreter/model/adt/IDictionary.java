package interpreter.model.adt;

import interpreter.exceptions.ADTException;

import java.util.Map;

public interface IDictionary<K, V> {
    boolean isDefined(K key);
    V lookup(K key) throws ADTException;
    void put(K key, V val);
    void update(K key, V val) throws ADTException;
    void remove(K key) throws ADTException;
    Map<K, V> toMap();
}
