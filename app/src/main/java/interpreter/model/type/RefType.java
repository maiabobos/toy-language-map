package interpreter.model.type;

import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;

public record RefType(IType inner) implements  IType {
    @Override
    public IValue defaultValue() {
        return new RefValue(0, inner);
    }

    @Override
    public String toString() {
        return "Ref(" + inner + ")";
    }
}

