package interpreter.repository;

import interpreter.model.PrgState;

import java.util.ArrayList;
import java.util.List;


public class Repository implements IRepository{
    private final List<PrgState> programs = new ArrayList<>();

    public Repository(PrgState initialState) {
        programs.add(initialState);
    }

    @Override
    public PrgState getCrtPrg() {
        return programs.get(0);
    }
}
