package org.jtodd.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
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

    public static SymbolLookup openNativeLib(String library, Arena arena) throws IllegalStateException {
        String osSpecificLibrary;
        String osName = System.getProperty("os.name");

        if (osName.contains("Linux")) {
            osSpecificLibrary = "lib" + library + ".so";
        } else if (osName.contains("Mac OS")) {
            osSpecificLibrary = "lib" + library + ".dylib";
        } else if (osName.contains("Windows")) {
            osSpecificLibrary = library + ".dll";
        } else {
            throw new IllegalStateException("Unsupported OS: " + osName);
        }
        return SymbolLookup.libraryLookup(osSpecificLibrary, arena);
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

        long elementSize = RASHUNAL_LAYOUT.byteSize();
        long elementAlign = RASHUNAL_LAYOUT.byteAlignment();
        long totalBytes = elementSize * (long)elementCount;
        MemorySegment elems = arena.allocate(totalBytes, elementAlign);
        long numOffset = RASHUNAL_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("numerator"));
        long denOffset = RASHUNAL_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("denominator"));
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

    private static JRashunalMatrix allocateJRashunalMatrix(Arena arena, Linker linker, SymbolLookup lookup, MethodHandle freeHandle, MemorySegment mPtr) throws Throwable {
        long numeratorOffset = RASHUNAL_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("numerator"));
        long denominatorOffset = RASHUNAL_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("denominator"));

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
                MemorySegment element = elementZero.reinterpret(RASHUNAL_LAYOUT.byteSize(), arena, null);
                int numerator = element.get(JAVA_INT, numeratorOffset);
                int denominator = element.get(JAVA_INT, denominatorOffset);
                data[Math.toIntExact((i - 1) * width + (j - 1))] = new JRashunal(numerator, denominator);
                freeHandle.invoke(element);
            }
        }

        return new JRashunalMatrix(Math.toIntExact(height), Math.toIntExact(width), data);
    }

    public static JGaussFactorization factor(int[][][] data) throws Throwable {
        Linker linker = Linker.nativeLinker();

        try (Arena arena = Arena.ofConfined()) {
            var lookup = openNativeLib("rmatrix", arena);
            var clib = linker.defaultLookup();

            var RMatrix_gelim_handle = linker.downcallHandle(find.apply(lookup, "RMatrix_gelim"),
                FunctionDescriptor.of(ADDRESS, ADDRESS));

            var free_RMatrix_handle = linker.downcallHandle(find.apply(lookup, "free_RMatrix"),
                FunctionDescriptor.ofVoid(ADDRESS));

            MethodHandle freeHandle = linker.downcallHandle(clib.find("free").orElseThrow(), FunctionDescriptor.ofVoid(ADDRESS));

            MemorySegment rmatrixPtr = allocateNativeRMatrix(arena, linker, lookup, data);

            MemorySegment factorZero = (MemorySegment) RMatrix_gelim_handle.invoke(rmatrixPtr);
            MemorySegment factor = factorZero.reinterpret(GAUSS_FACTORIZATION_LAYOUT.byteSize(), arena, null);

            long piOffset = GAUSS_FACTORIZATION_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("PI"));
            long lOffset = GAUSS_FACTORIZATION_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("L"));
            long dOffset = GAUSS_FACTORIZATION_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("D"));
            long uOffset = GAUSS_FACTORIZATION_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("U"));

            MemorySegment piPtr = factor.get(ADDRESS, piOffset);
            MemorySegment lPtr = factor.get(ADDRESS, lOffset);
            MemorySegment dPtr = factor.get(ADDRESS, dOffset);
            MemorySegment uPtr = factor.get(ADDRESS, uOffset);

            JRashunalMatrix pInverse = allocateJRashunalMatrix(arena, linker, lookup, freeHandle, piPtr);
            JRashunalMatrix lower = allocateJRashunalMatrix(arena, linker, lookup, freeHandle, lPtr);
            JRashunalMatrix diagonal = allocateJRashunalMatrix(arena, linker, lookup, freeHandle, dPtr);
            JRashunalMatrix upper = allocateJRashunalMatrix(arena, linker, lookup, freeHandle, uPtr);
            JGaussFactorization factorization = new JGaussFactorization(pInverse, lower, diagonal, upper);

            free_RMatrix_handle.invoke(rmatrixPtr);
            free_RMatrix_handle.invoke(piPtr);
            free_RMatrix_handle.invoke(lPtr);
            free_RMatrix_handle.invoke(dPtr);
            free_RMatrix_handle.invoke(uPtr);

            freeHandle.invoke(factor);

            return factorization;
        }
    }
}
