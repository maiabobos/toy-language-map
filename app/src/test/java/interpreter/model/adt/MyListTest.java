package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyListTest {
    private IList<String> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void newListSizeZero() {
        assertEquals(0, list.size());
    }

    @Test
    void addIncreasesSize() {
        list.add("a");
        assertEquals(1, list.size());
    }

    @Test
    void getReturnsInInsertionOrder() throws ADTException {
        list.add("1");
        list.add("2");
        assertEquals("1", list.get(0));
        assertEquals("2", list.get(1));
    }

    @Test
    void getWithIndexEqualToSizeThrows() {
        assertThrows(ADTException.class, () -> list.get(1));
    }

    @Test
    void getWithNegativeThrows() {
        list.add("a");
        assertThrows(ADTException.class, () -> list.get(-2));
    }

    @Test
    void getEmptyListThrows() {
        assertThrows(ADTException.class, () -> list.get(0));
    }
}
