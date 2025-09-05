package org.jtodd.jni;

public class RMatrixJNI {
    static {
        System.loadLibrary("jnirmatrix");
    }

    public JRashunalMatrix factorMatrix(int data[][][]) {
        return factor(data);
    }
    
    private native JRashunalMatrix factor(int data[][][]);
}
