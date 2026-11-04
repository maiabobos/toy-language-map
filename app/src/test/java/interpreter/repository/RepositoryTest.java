package interpreter.repository;

import interpreter.exceptions.MyException;
import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryTest {
    @TempDir
    Path dir;

    private PrgState state;
    private Path logFile;
    private IRepository repo;

    @BeforeEach
    void SetUp() {
        state = PrgState.of(new NopStmt());
        logFile = dir.resolve("log.txt");
        repo = new Repository(state, logFile.toString());
    }

    @Test
    void getCrtPrgReturnsStateGivenToConstructor() {
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

    @Test
    void logPrgStateExecWritesTheStateToTheLogFile() throws Exception {
        repo.logPrgStateExec();
        String log = Files.readString(logFile);
        assertTrue(log.contains("ExeStack:"));
        assertTrue(log.contains("FileTable:"));
    }

    @Test
    void logPrgStateExecAppendsInsteadOfOverwriting() throws Exception {
        repo.logPrgStateExec();
        repo.logPrgStateExec();
        String log = Files.readString(logFile);
        assertEquals(2, log.split("ExeStack:", -1).length - 1);
    }

    @Test
    void logToAnUnwritablePathThrowsAnInterpreterException() {
        var badRepo = new Repository(state, dir.resolve("no-such-folder/log.txt").toString());
        assertThrows(MyException.class, badRepo::logPrgStateExec);        // must be a MyException so the menu can catch it
    }

    @Test
    void theExceptionForAnUnwritablePathIsARepositoryException() {
        var badRepo = new Repository(state, dir.resolve("no-such-folder/log.txt").toString());
        assertThrows(RepositoryException.class, badRepo::logPrgStateExec);
    }
}
