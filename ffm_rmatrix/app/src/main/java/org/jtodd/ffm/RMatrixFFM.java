package org.jtodd.ffm;

import java.lang.foreign.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.lang.foreign.ValueLayout.*;

public class RMatrixFFM {
    public static final GroupLayout RASHUNAL_LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("numerator"),
        JAVA_INT.withName("denominator")
    );

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

    private static final BiFunction<SymbolLookup, String, MemorySegment> find = (lookup, name) -> {
        Optional<MemorySegment> sym = lookup.find(name);
        if (sym.isEmpty()) throw new IllegalStateException("Native symbol not found: " + name);
        return sym.get();
    };

    private static MemorySegment allocateNativeRMatrix(Arena arena, Linker linker, SymbolLookup lookup, int[][][] data) throws Throwable {
        var new_RMatrix_handle = linker.downcallHandle(find.apply(lookup, "new_RMatrix"),
            FunctionDescriptor.of(ADDRESS, JAVA_LONG, JAVA_LONG, ADDRESS));

        int height = data.length;
        int width = data[0].length;
        int elementCount = height * width;

        MemoryLayout rashunalLayout = RMatrixFFM.RASHUNAL_LAYOUT;
        long elementSize = rashunalLayout.byteSize();
        long elementAlign = rashunalLayout.byteAlignment();
        long totalBytes = elementSize * (long)elementCount;
        MemorySegment elems = arena.allocate(totalBytes, elementAlign);
        long numOffset = rashunalLayout.byteOffset(MemoryLayout.PathElement.groupElement("numerator"));
        long denOffset = rashunalLayout.byteOffset(MemoryLayout.PathElement.groupElement("denominator"));
        for (int i = 0; i < elementCount; ++i) {
            int row = i / width;
            int col = i % width;
            int[] element = data[row][col];
            int numerator = element[0];
            int denominator = element.length == 1 ? 1 : element[1];

            MemorySegment elementSlice = elems.asSlice(i * elementSize, elementSize);

            elementSlice.set(JAVA_INT, numOffset, numerator);
            elementSlice.set(JAVA_INT, denOffset, denominator);
        }

        MemorySegment ptrArray = arena.allocate(ADDRESS.byteSize() * elementCount, ADDRESS.byteAlignment());
        for (int i = 0; i < elementCount; ++i) {
            MemorySegment elementAddr = elems.asSlice(i * elementSize, elementSize);
            ptrArray.setAtIndex(ADDRESS, i, elementAddr);
        }

        return (MemorySegment) new_RMatrix_handle.invoke((long)height, (long)width, ptrArray);
    }

    private static JRashunalMatrix allocateJRashunalMatrix(Arena arena, Linker linker, SymbolLookup lookup, MemorySegment mPtr) throws Throwable {
        long numeratorOffset = RMatrixFFM.RASHUNAL_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("numerator"));
        long denominatorOffset = RMatrixFFM.RASHUNAL_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("denominator"));

        var RMatrix_height_handle = linker.downcallHandle(find.apply(lookup, "RMatrix_height"),
            FunctionDescriptor.of(JAVA_LONG, ADDRESS));

        var RMatrix_width_handle = linker.downcallHandle(find.apply(lookup, "RMatrix_width"),
            FunctionDescriptor.of(JAVA_LONG, ADDRESS));

        var RMatrix_get_handle = linker.downcallHandle(find.apply(lookup, "RMatrix_get"),
            FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_LONG, JAVA_LONG));

        long height = (long) RMatrix_height_handle.invoke(mPtr);
        long width = (long) RMatrix_width_handle.invoke(mPtr);
        int elementCount = Math.toIntExact(height * width);

        JRashunal[] data = new JRashunal[elementCount];
        for (long i = 1; i <= height; ++i) {
            for (long j = 1; j <= width; ++j) {
                MemorySegment elementZero = (MemorySegment) RMatrix_get_handle.invoke(mPtr, i, j);
                MemorySegment element = elementZero.reinterpret(RMatrixFFM.RASHUNAL_LAYOUT.byteSize(), arena, null);
                int numerator = element.get(JAVA_INT, numeratorOffset);
                int denominator = element.get(JAVA_INT, denominatorOffset);
                data[Math.toIntExact((i - 1) * width + (j - 1))] = new JRashunal(numerator, denominator);
            }
        }

        return new JRashunalMatrix(Math.toIntExact(height), Math.toIntExact(width), data);
    }

    public static JGaussFactorization factor(int[][][] data) throws Throwable {
        Linker linker = Linker.nativeLinker();

        try (Arena arena = Arena.ofConfined()) {
            var lookup = RMatrixFFM.openNativeLib(arena);

            var RMatrix_gelim_handle = linker.downcallHandle(find.apply(lookup, "RMatrix_gelim"),
                FunctionDescriptor.of(ADDRESS, ADDRESS));

            MemorySegment rmatrixPtr = allocateNativeRMatrix(arena, linker, lookup, data);

            MemorySegment factorZero = (MemorySegment) RMatrix_gelim_handle.invoke(rmatrixPtr);
            MemorySegment factor = factorZero.reinterpret(RMatrixFFM.GAUSS_FACTORIZATION_LAYOUT.byteSize(), arena,null);

            long piOffset = RMatrixFFM.GAUSS_FACTORIZATION_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("PI"));
            long lOffset = RMatrixFFM.GAUSS_FACTORIZATION_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("L"));
            long dOffset = RMatrixFFM.GAUSS_FACTORIZATION_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("D"));
            long uOffset = RMatrixFFM.GAUSS_FACTORIZATION_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("U"));

            MemorySegment piPtr = factor.get(ADDRESS, piOffset);
            MemorySegment lPtr = factor.get(ADDRESS, lOffset);
            MemorySegment dPtr = factor.get(ADDRESS, dOffset);
            MemorySegment uPtr = factor.get(ADDRESS, uOffset);

            return new JGaussFactorization(
                allocateJRashunalMatrix(arena, linker, lookup, piPtr),
                allocateJRashunalMatrix(arena, linker, lookup, lPtr),
                allocateJRashunalMatrix(arena, linker, lookup, dPtr),
                allocateJRashunalMatrix(arena, linker, lookup, uPtr)
            );
        }
    }
}
