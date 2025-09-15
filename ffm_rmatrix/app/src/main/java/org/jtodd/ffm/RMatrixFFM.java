package org.jtodd.ffm;

import java.lang.foreign.*;
import java.nio.file.Path;

import static java.lang.foreign.ValueLayout.*;

public class RMatrixFFM {
    public static final GroupLayout RASHUNAL_LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("numerator"),
        JAVA_INT.withName("denominator")
    );

    public static final AddressLayout RASHUNAL_PTR = ADDRESS.withTargetLayout(RASHUNAL_LAYOUT);

    public static final GroupLayout GAUSS_FACTORIZATION_LAYOUT = MemoryLayout.structLayout(
        ADDRESS.withName("PI"),
        ADDRESS.withName("L"),
        ADDRESS.withName("D"),
        ADDRESS.withName("U")
    );

    public static SymbolLookup openNativeLib(Arena arena) {
        return SymbolLookup.libraryLookup("rmatrix", arena);
    }
}
