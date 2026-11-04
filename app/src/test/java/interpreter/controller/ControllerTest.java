package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.*;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    @TempDir
    Path dir;

    private static PrgState stateFor(IStmt program) {
        return PrgState.of(program);
    }

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        var repo = new Repository(state, dir.resolve("log.txt").toString());
        new Controller(repo, false).allStep();
        return state;
    }

    private Controller controllerFor(PrgState state, boolean display) {
        return new Controller(new Repository(state, dir.resolve("log.txt").toString()), display);
    }

    private static ValueExp num (int n) {
        return new ValueExp(new IntValue(n));
    }

    // Ex1: int v; v = 2; print(v)
    private static IStmt ex1() {
        return new CompStmt(new VarDeclStmt("v", new IntType()), new CompStmt(new AssignStmt("v", num(2)), new PrintStmt(new VarExp("v"))));
    }

    //Ex2: int a; int b; a = 2 + 3 * 5; b = a + 1; print(b)
    private static IStmt ex2() {
        return new CompStmt(new VarDeclStmt("a", new IntType()),
                new CompStmt(new VarDeclStmt("b", new IntType()),
                        new CompStmt(new AssignStmt("a",
                                new ArithExp(ArithOp.ADD, num(2),
                                        new ArithExp(ArithOp.MUL, num(3), num(5)))),
                                new CompStmt(new AssignStmt("b",
                                        new ArithExp(ArithOp.ADD, new VarExp("a"), num(1))),
                                        new PrintStmt(new VarExp("b"))))));
    }

    // Ex3: bool a; int v; a = true; (if a then v = 2 else v = 3); print(v)
    private static IStmt ex3() {
        return new CompStmt(new VarDeclStmt("a", new BoolType()),
                new CompStmt(new VarDeclStmt("v", new IntType()),
                        new CompStmt(new AssignStmt("a", new ValueExp(new BoolValue(true))),
                                new CompStmt(new IfStmt(new VarExp("a"),
                                        new AssignStmt("v", num(2)),
                                        new AssignStmt("v", num(3))),
                                        new PrintStmt(new VarExp("v"))))));
    }

    @Test
    void oneStepExecutesStatementAndConsumesIt() throws MyException {
        PrgState state = stateFor(new VarDeclStmt("v", new IntType()));
        controllerFor(state, false).oneStep(state);

        assertTrue(state.getSymTable().isDefined("v"));
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void oneStepOnCompoundStatementSplitsItAndRunsNothing() throws MyException {
        PrgState state = stateFor(new CompStmt(new PrintStmt(num(1)), new PrintStmt(num(2))));
        controllerFor(state, false).oneStep(state);

        assertEquals(0, state.getOut().size());
        assertFalse(state.getExeStack().isEmpty());
    }

    @Test
    void oneStepRunsOnlyTopStatement() throws MyException {
        PrgState state = stateFor(new CompStmt(new PrintStmt(num(1)), new PrintStmt(num(2))));
        Controller controller = controllerFor(state, false);

        controller.oneStep(state);
        controller.oneStep(state);

        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(1), state.getOut().get(0));
    }

    @Test
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = stateFor(new NopStmt());
        state.getExeStack().pop();
        Controller controller = controllerFor(state, false);

        var ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertEquals("Program state stack is empty", ex.getMessage());
    }

    @Test
    void allStepOfExample1PrintsTwo() throws MyException {
        PrgState state = stateFor(ex1());
        controllerFor(state, false).allStep();

        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void allStepOfExample2Prints18() throws MyException {
        PrgState state = stateFor(ex2());
        controllerFor(state, false).allStep();

        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(18), state.getOut().get(0));
    }

    @Test
    void allStepOfExample3TakesThenBranchAndPrintsTwo() throws MyException {
        PrgState state = stateFor(ex3());
        controllerFor(state, false).allStep();

        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void allStepLeavesEmptyStackAndFinalVariables() throws MyException {
        PrgState state = stateFor(ex1());
        controllerFor(state, false).allStep();

        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
    }

    @Test
    void allStepPropagatesErrorFromStatement() {
        PrgState state = stateFor(new AssignStmt("undeclared", num(1)));
        Controller controller = controllerFor(state, false);

        assertThrows(StatementException.class, controller::allStep);
    }

    @Test
    void oneStepAfterAllStepThrowsBecauseStackIsEmpty() throws MyException {
        PrgState state = stateFor(ex1());
        Controller controller = controllerFor(state, false);
        controller.allStep();

        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }

    @Test
    void displayFlagOnPrintsTheStateAfterEveryStep() throws MyException {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            PrgState state = stateFor(ex1());
            controllerFor(state, true).allStep();
        } finally {
            System.setOut(original);
        }
        assertTrue(captured.toString().contains("ExeStack"));
        assertTrue(captured.toString().contains("SymTable"));
        assertTrue(captured.toString().contains("Out"));
    }

    @Test
    void displayFlagOffPrintsNothing() throws MyException {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            PrgState state = stateFor(ex1());
            controllerFor(state, false).allStep();
        } finally {
            System.setOut(original);
        }
        assertEquals("", captured.toString());
    }

    @Test void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(15), s.getOut().get(0));
        assertEquals(new IntValue(50), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test void logFileContainsTheFourSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:");
        assertTrue(0 <= a && a < b && b < c && c < d);
    }

    @Test
    void fileExampleWithAnEmptyFilePrintsZeroZero() throws Exception {
        Path in = dir.resolve("empty.in");
        Files.writeString(in, "");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(2, s.getOut().size());
        assertEquals(new IntValue(0), s.getOut().get(0));
        assertEquals(new IntValue(0), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void example5PrintsFive() throws MyException {
        PrgState s = run(Examples.EX5);
        assertEquals(1, s.getOut().size());
        assertEquals(new IntValue(5), s.getOut().get(0));
    }

    @Test
    void logFileHasTheInitialStateAndOneStateAfterEveryStep() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        assertEquals(6, log.split("ExeStack:", -1).length - 1);
    }

    @Test
    void logFileListsTheOpenFileInTheFileTable() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        run(Examples.fileExample(in.toString()));
        String log = Files.readString(dir.resolve("log.txt"));
        assertTrue(log.contains("FileTable:\n" + in));
    }
}
