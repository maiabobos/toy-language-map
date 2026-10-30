package interpreter.view;

import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.*;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;

import java.util.List;

public final class Examples {
    private Examples() {}

    public record Example(String description, IStmt program) {}

    // Ex1: int v; v = 2; print(v)
    public static final IStmt EX1 =
            new CompStmt(new VarDeclStmt("v", new IntType()),
                    new CompStmt(new AssignStmt("v", new ValueExp(new IntValue(2))),
                            new PrintStmt(new VarExp("v"))));

    // Ex2: int a; int b; a = 2 + 3 * 5; b = a + 1; print(b)
    public static final IStmt EX2 =
            new CompStmt(new VarDeclStmt("a", new IntType()),
                    new CompStmt(new VarDeclStmt("b", new IntType()),
                            new CompStmt(new AssignStmt("a",
                                    new ArithExp(ArithOp.ADD, new ValueExp(new IntValue(2)),
                                            new ArithExp(ArithOp.MUL, new ValueExp(new IntValue(3)),
                                                    new ValueExp(new IntValue(5))))),
                                    new CompStmt(new AssignStmt("b",
                                            new ArithExp(ArithOp.ADD, new VarExp("a"),
                                                    new ValueExp(new IntValue(1)))),
                                            new PrintStmt(new VarExp("b"))))));

    // Ex3: bool a; int v; a = true; (if a then v = 2 else v = 3); print(v)
    public static final IStmt EX3 =
            new CompStmt(new VarDeclStmt("a", new BoolType()),
                    new CompStmt(new VarDeclStmt("v", new IntType()),
                            new CompStmt(new AssignStmt("a", new ValueExp(new BoolValue(true))),
                                    new CompStmt(new IfStmt(new VarExp("a"),
                                            new AssignStmt("v", new ValueExp(new IntValue(2))),
                                            new AssignStmt("v", new ValueExp(new IntValue(3)))),
                                            new PrintStmt(new VarExp("v"))))));

    public static final IStmt EXERROR =
            new CompStmt(new VarDeclStmt("a", new BoolType()),
                    new CompStmt(new AssignStmt("a", new ValueExp(new IntValue(2))),
                        new PrintStmt(new VarExp("a"))));

    public static List<Example> all() {
        return List.of(
                new Example("int v; v = 2; print(v)", EX1),
                new Example("int a; int b; a = 2 + 3 * 5; b = a + 1; print(b)", EX2),
                new Example("bool a; int v; a = true; (if a then v = 2 else v = 3); print(v)", EX3),
                new Example("bool a; a = 2; print(a)", EXERROR));
    }
}