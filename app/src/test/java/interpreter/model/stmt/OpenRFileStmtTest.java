package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class OpenRFileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private StringValue fileName;
    private ValueExp file;

    @BeforeEach
    void setUp() throws IOException {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "15\n50\n");
        fileName = new StringValue(f.toString());
        file = new ValueExp(fileName);
        state = PrgState.of(new NopStmt());
    }

    @AfterEach
    void closeLeftoverFiles() throws IOException {
        for (BufferedReader reader : state.getFileTable().toMap().values())
            reader.close();
    }

    @Test
    void addsFileToFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }

    @Test
    void nonStringExpressionThrows() {
        var notString = new ValueExp(new IntValue(1));
        assertThrows(StatementException.class, () -> new OpenRFileStmt(notString).execute(state));
    }

    @Test
    void fileAlreadyOpenThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new OpenRFileStmt(file).execute(state));
    }

    @Test
    void missingFileThrows() {
        var missing = new ValueExp(new StringValue(dir.resolve("missing.in").toString()));
        assertThrows(StatementException.class, () -> new OpenRFileStmt(missing).execute(state));
    }

    @Test
    void failedOpenAddsNothingToTheFileTable() {
        var missingName = new StringValue(dir.resolve("missing.in").toString());
        try {
            new OpenRFileStmt(new ValueExp(missingName)).execute(state);
        } catch (MyException ignored) {
        }
        assertFalse(state.getFileTable().isDefined(missingName));
    }
}
