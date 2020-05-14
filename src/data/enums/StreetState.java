package data.enums;

public enum StreetState {
    LOW (1),
    MEDIUM (0.75),
    HIGH (0.5);

    private final double factor;

    StreetState(double factor) { this.factor = factor; }

    public double getModifier() { return factor; }
}
