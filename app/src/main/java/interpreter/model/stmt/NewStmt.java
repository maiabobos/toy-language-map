package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;

public record NewStmt(String varName, IExp exp)  implements  IStmt{
    @Override
    public PrgState execute(PrgState state) throws MyException {
        var symT = state.getSymTable();
        if (!symT.isDefined(varName))
            throw new StatementException("New: " + varName + " is not declared");
        if (!(symT.lookup(varName) instanceof RefValue ref))
            throw new StatementException("New: " + varName + " is not a reference");
        IValue value = exp.eval(symT, state.getHeap());
        if (!value.getType().equals(ref.locationType()))
            throw new StatementException("New: " + exp + " has type " + value.getType() + ", expected " + ref.locationType());
        int address = state.getHeap().allocate(value);
        symT.update(varName, new RefValue(address, ref.locationType()));
        return state;
    }

    @Override
    public String toString() {
        return "New(" + varName + ", " + exp + ")";
    }
}
