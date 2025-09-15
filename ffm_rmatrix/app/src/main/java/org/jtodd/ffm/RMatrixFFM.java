package org.jtodd.ffm;

import java.lang.foreign.*;
import java.nio.file.Files;
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
        String libString = System.getenv("RMATRIX_LIB");
        if (libString == null || libString.isBlank()) {
            throw new IllegalStateException("Environment variable RMATRIX_LIB needed to load native libraries");
        }
        Path libPath = Path.of(libString);
        if (!Files.isRegularFile(libPath)) {
            throw new IllegalArgumentException("Library file not found: " + libPath);
        }
        System.out.println("Loading native library: " + libPath.toAbsolutePath());
        return SymbolLookup.libraryLookup(libPath, arena);
    }
}
