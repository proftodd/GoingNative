package org.jtodd.ffm;

public class JGaussFactorization {
    public final JRashunalMatrix pInverse;
    public final JRashunalMatrix lower;
    public final JRashunalMatrix diagonal;
    public final JRashunalMatrix upper;

    public JGaussFactorization(JRashunalMatrix pInverse, JRashunalMatrix lower, JRashunalMatrix diagonal, JRashunalMatrix upper) {
        this.pInverse = pInverse;
        this.lower = lower;
        this.diagonal = diagonal;
        this.upper = upper;
    }
}
