package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
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

public class ReadFileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private ValueExp file;

    @BeforeEach
    void setUp() throws Exception {
        file = writeFile("numbers.in", "15\n50\n");
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @AfterEach
    void closeLeftoverFiles() throws IOException {
        for (BufferedReader reader : state.getFileTable().toMap().values())
            reader.close();
    }

    private static ValueExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private ValueExp writeFile(String name, String content) throws IOException {
        Path f = dir.resolve("name");
        Files.writeString(f, content);
        return new ValueExp(new StringValue(f.toString()));
    }

    private int x() throws MyException {
        return ((IntValue) state.getSymTable().lookup("x")).val();
    }

    @Test
    void readsNumbersInOrderThenZero() throws MyException {
        new OpenRFileStmt(file).execute(state);

        new ReadFileStmt(file, "x").execute(state);
        assertEquals(15, x());

        new ReadFileStmt(file, "x").execute(state);
        assertEquals(50, x());

        new ReadFileStmt(file, "x").execute(state);
        assertEquals(0, x());
    }

    @Test
    void keepsGivingZeroAfterEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        for (int i = 0; i < 3; i ++) {
            new ReadFileStmt(file, "x").execute(state);
        }
        new AssignStmt("x", num(9)).execute(state);

        new ReadFileStmt(file, "x").execute(state);
        assertEquals(0, x());
    }

    @Test
    void emptyFileGivesZero() throws MyException, IOException {
        var empty = writeFile("empty.in", "");
        new OpenRFileStmt(empty).execute(state);
        new AssignStmt("x", num(5)).execute(state);

        new ReadFileStmt(empty, "x").execute(state);
        assertEquals(0, x());
    }

    @Test
    void undeclaredVariableThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "nope").execute(state));
    }

    @Test
    void nonIntVariableThrows() throws MyException {
        new VarDeclStmt("s", new StringType()).execute(state);
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "s").execute(state));
    }

    @Test
    void fileThatIsNotOpenThrows() {
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));
    }

    @Test
    void nonStringExpressionThrows() {
        assertThrows(StatementException.class, () -> new ReadFileStmt(num(1), "x").execute(state));
    }

    @Test
    void nonIntegerLineThrows() throws MyException, IOException {
        var text = writeFile("text.in", "abc\n");
        new OpenRFileStmt(text).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(text, "x").execute(state));
    }

    @Test
    void failedReadLeavesTheVariableUnchanged() throws MyException, IOException {
        var text = writeFile("text.in", "abc\n");
        new OpenRFileStmt(text).execute(state);
        new AssignStmt("x", num(3)).execute(state);
        try {
            new ReadFileStmt(text, "x").execute(state);
        } catch (StatementException ignored) {
        }
        assertEquals(3, x());
    }

}
