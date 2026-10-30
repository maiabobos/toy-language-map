package interpreter.model.exp;

public enum LogicOp {
    AND("&&"), OR("||");
    private final String symbol;
    LogicOp(String symbol) {this.symbol = symbol;}
    @Override
    public String toString() {return symbol;}
}
