package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyDictionaryTest {
    private IDictionary<String, Integer> dict;

    @BeforeEach
    void setUp() {
        dict = new MyDictionary<>();
    }

    @Test
    void keyNotDefined() {
        assertFalse(dict.isDefined("a"));
    }

    @Test
    void putDefinesKey() {
        dict.put("a", 1);
        assertTrue(dict.isDefined("a"));
    }

    @Test
    void lookupReturnsValue() throws ADTException {
        dict.put("a", 1);
        assertEquals(1, dict.lookup("a"));
    }

    @Test
    void lookupMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.lookup("missing"));
    }

    @Test
    void updateChangesValue() throws ADTException {
        dict.put("a", 1);
        dict.update("a", 2);
        assertEquals(2, dict.lookup("a"));
    }

    @Test
    void updateMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.update("missing", 2));
    }

    @Test
    void updateDoesntAddNewKey() {
        try {
            dict.update("missing", 1);
        }
        catch (ADTException e) {}
        assertFalse(dict.isDefined("missing"));
    }

    @Test
    void toStringReturnsKeysValues() {
        dict.put("a", 1);
        String s = dict.toString();
        assertTrue(s.contains("a"));
        assertTrue(s.contains("1"));
    }

    @Test
    void toMapDoesNotChangeDict() throws ADTException {
        dict.put("a", 1);
        dict.put("b", 2);
        dict.toMap();
        assertEquals(2, dict.lookup("b"));
    }

    @Test
    void removeDeletesKey() throws ADTException {
        dict.put("a", 1);
        dict.remove("a");
        assertFalse(dict.isDefined("a"));
    }

    @Test
    void removeMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.remove("missing"));
    }

    @Test
    void removeKeepsOtherKeys() throws ADTException {
        dict.put("a", 1);
        dict.put("b", 2);
        dict.remove("a");
        assertEquals(2, dict.lookup("b"));
    }

    @Test
    void toMapIsReadOnlyCopy() {
        dict.put("a", 1);
        var map = dict.toMap();
        assertEquals(1, map.get("a"));
        assertThrows(UnsupportedOperationException.class, () -> map.put("b", 1));
    }
}
