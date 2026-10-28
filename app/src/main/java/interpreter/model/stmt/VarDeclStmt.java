package interpreter.model.stmt;

import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.type.IType;

public record VarDeclStmt(String name, IType type) implements IStmt{
    @Override
    public PrgState execute(PrgState state) throws StatementException {
        var symT = state.getSymTable();
        if (symT.isDefined(name))
            throw new StatementException("Variable " + name + " is already declared");
        symT.put(name, type.defaultValue());
        return state;
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
