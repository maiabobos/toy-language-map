package interpreter.repository;

import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryTest {
    private PrgState state;
    private IRepository repo;

    @BeforeEach
    void SetUp() {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        repo = new Repository(state);
    }

    @Test
    void getCrtPrgReturnsStateGievnToConstructor() {
        assertSame(state, repo.getCrtPrg());
    }

    @Test
    void getCrtPrgReturnsSameStateOnEveryCall() {
        assertSame(repo.getCrtPrg(), repo.getCrtPrg());
    }

    @Test
    void changesMadeToReturnedStateVisibleThroughRepository() {
        repo.getCrtPrg().getSymTable().put("v", new IntValue(1));
        assertTrue(repo.getCrtPrg().getSymTable().isDefined("v"));
    }
}
