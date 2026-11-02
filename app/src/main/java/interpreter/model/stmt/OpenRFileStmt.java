package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.StringValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public record OpenRFileStmt(IExp exp) implements IStmt {

    @Override
    public PrgState execute(PrgState state) throws MyException {
        if(!(exp.eval(state.getSymTable()) instanceof StringValue fileName))
            throw new StatementException("OpenRFile: " + exp + " is not a string");
        var fileTable = state.getFileTable();
        if (fileTable.isDefined(fileName))
            throw new StatementException("OpenRFile: " + fileName + " is alreasy open");
        try {
            var reader = new BufferedReader(new FileReader(fileName.s()));
            fileTable.put(fileName, reader);
        } catch (IOException e) {
            throw new StatementException("OpenRFile: cannot open " + fileName + ": " + e.getMessage());
        }
        return state;
    }
    
    @Override
    public String toString() {
        return "OpenRFile(" + exp + ")";
    }
}
