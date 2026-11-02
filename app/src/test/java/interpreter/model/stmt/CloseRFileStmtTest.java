package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.IntType;
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

public class CloseRFileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private StringValue fileName;
    private ValueExp file;

    @BeforeEach
    void setUp() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "15\n50\n");
        fileName = new StringValue(f.toString());
        file = new ValueExp(fileName);
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @AfterEach
    void closeLeftOverFiles() throws IOException {
        for (BufferedReader reader : state.getFileTable().toMap().values())
            reader.close();
    }

    @Test
    void removesFileFromFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertFalse(state.getFileTable().isDefined(fileName));
    }

    @Test
    void reallyClosesReader() throws MyException {
        new OpenRFileStmt(file).execute(state);
        BufferedReader reader = state.getFileTable().lookup(fileName);

        new CloseRFileStmt(file).execute(state);
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void fileNotOpenThrows() {
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));
    }

    @Test
    void nonStringExpressionThrows() {
        var notAString = new ValueExp(new IntValue(1));
        assertThrows(StatementException.class, () -> new CloseRFileStmt(notAString).execute(state));
    }

    @Test
    void closingTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));
    }

    @Test
    void readAfterCloseThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));
    }

    @Test
    void closedFileCanBeOpenedAgainAndReadsFromStart() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        new CloseRFileStmt(file).execute(state);

        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(15), state.getSymTable().lookup("x"));
    }
}
