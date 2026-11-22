package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;

public record ReadHeapExp(IExp exp) implements IExp {
    @Override
    public IValue eval(IDictionary<String, IValue> symTable, IHeap heap) throws MyException {
        if (!(exp.eval(symTable, heap)  instanceof RefValue ref))
            throw new ExpressionException("RH: " + exp + " is not a reference");
        if (!heap.isDefined(ref.address()))
            throw new ExpressionException("RH: address " + ref.address() + " is not defined");
        return heap.get(ref.address());
    }

    @Override
    public String toString() {
        return "RH(" + exp + ")";
    }
}
