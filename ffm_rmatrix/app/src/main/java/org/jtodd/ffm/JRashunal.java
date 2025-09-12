package org.jtodd.ffm;

public class JRashunal {
    private int numerator;
    private int denominator;
    
    public JRashunal(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public String toString() {
        return denominator == 1
            ? String.format("{%d}", numerator)
            : String.format("{%d/%d}", numerator, denominator);
    }
}
