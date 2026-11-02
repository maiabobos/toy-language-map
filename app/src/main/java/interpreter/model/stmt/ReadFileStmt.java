package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.IDictionary;
import interpreter.model.exp.IExp;
import interpreter.model.type.IntType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;

import java.io.BufferedReader;
import java.io.IOException;

public record ReadFileStmt(IExp exp, String varName) implements  IStmt {

    @Override
    public PrgState execute(PrgState state) throws MyException {
        IDictionary<String, IValue> symT = state.getSymTable();

        if (!symT.isDefined(varName))
            throw new StatementException("ReadFile: " + varName + " is not defined");
        if (!(symT.lookup(varName) instanceof IntValue))
            throw new StatementException("ReadFile: " + varName + " does not have type int");

        if (!(exp.eval(symT) instanceof StringValue filename))
            throw new StatementException("ReadFile: " + exp + " is not a string");
        var fileTable = state.getFileTable();
        if (!fileTable.isDefined(filename))
            throw new StatementException("ReadFile: " + filename + " is not open");

        BufferedReader reader = fileTable.lookup(filename);
        int n;
        try {
            String line = reader.readLine();
            n = (line == null) ? 0 : Integer.parseInt(line.strip());
        } catch (IOException e) {
            throw new StatementException("ReadFile: cannot read from " + filename + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new StatementException("ReadFile: the line read from " + filename + " is not an integer (" + e.getMessage() + ")");
        }
        symT.update(varName, new IntValue(n));
        return state;
    }

    @Override
    public String toString() {
        return "ReadFile(" + exp + ", " + varName + ")";
    }
}
