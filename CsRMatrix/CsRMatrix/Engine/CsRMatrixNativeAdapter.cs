using CsRMatrix.Model;
using System.Runtime.InteropServices;

namespace CsRMatrix.Engine;

public static partial class CsRMatrixNativeAdapter
{
    [StructLayout(LayoutKind.Sequential)]
    private struct Rashunal
    {
        public int numerator;
        public int denominator;
    }

    [StructLayout(LayoutKind.Sequential)]
    private struct GaussFactorization
    {
        public IntPtr PInverse;
        public IntPtr Lower;
        public IntPtr Diagonal;
        public IntPtr Upper;
    }

    [LibraryImport("rashunal", EntryPoint = "n_Rashunal")]
    private static partial IntPtr n_Rashunal(int numerator, int denominator);

    [LibraryImport("rmatrix", EntryPoint = "new_RMatrix")]
    private static partial IntPtr new_RMatrix(int height, int width, IntPtr data);

    [LibraryImport("rmatrix", EntryPoint = "RMatrix_height")]
    private static partial int RMatrix_height(IntPtr m);

    [LibraryImport("rmatrix", EntryPoint = "RMatrix_width")]
    private static partial int RMatrix_width(IntPtr m);

    [LibraryImport("rmatrix", EntryPoint = "RMatrix_get")]
    private static partial IntPtr RMatrix_get(IntPtr m, int row, int col);

    [LibraryImport("rmatrix", EntryPoint = "RMatrix_gelim")]
    private static partial IntPtr RMatrix_gelim(IntPtr m);

    private static IntPtr AllocRashunal(int num, int den)
    {
        IntPtr ptr = NativeStdLib.Malloc((UIntPtr)Marshal.SizeOf<Rashunal>());
        var value = new Rashunal { numerator = num, denominator = den };
        Marshal.StructureToPtr(value, ptr, false);
        return ptr;
    }

    private static IntPtr AllocateNativeRMatrix(Model.CsRMatrix m)
    {
        int elementCount = m.Height * m.Width;
        IntPtr elementArray = NativeStdLib.Malloc((UIntPtr)(IntPtr.Size * elementCount));
        unsafe
        {
            var pArray = (IntPtr*)elementArray;
            for (int i = 0; i < elementCount; ++i)
            {
                var element = m.Data[i];
                var elementPtr = AllocRashunal(element.Numerator, element.Denominator);
                pArray[i] = elementPtr;
            }
            var rMatrixPtr = new_RMatrix(m.Height, m.Width, elementArray);

            for (int i = 0; i < elementCount; ++i)
            {
                NativeStdLib.Free(pArray[i]);
            }
            NativeStdLib.Free(elementArray);

            return rMatrixPtr;
        }
    }

    private static Model.CsRMatrix AllocateManagedRMatrix(IntPtr m)
    {
        int height = RMatrix_height(m);
        int width = RMatrix_width(m);
        var data = new CsRashunal[height * width];
        for (int i = 1; i <= height; ++i)
        {
            for (int j = 1; j <= width; ++j)
            {
                var rPtr = RMatrix_get(m, i, j);
                var r = Marshal.PtrToStructure<Rashunal>(rPtr);
                data[(i - 1) * width + (j - 1)] = new CsRashunal { Numerator = r.numerator, Denominator = r.denominator };
                NativeStdLib.Free(rPtr);
            }
        }
        return new Model.CsRMatrix { Height = height, Width = width, Data = data, };
    }

    public static CsGaussFactorization Factor(Model.CsRMatrix m)
    {
        var nativeMPtr = AllocateNativeRMatrix(m);
        var fPtr = RMatrix_gelim(nativeMPtr);
        var f = Marshal.PtrToStructure<GaussFactorization>(fPtr);
        var csF = new CsGaussFactorization
        {
            PInverse = AllocateManagedRMatrix(f.PInverse),
            Lower = AllocateManagedRMatrix(f.Lower),
            Diagonal = AllocateManagedRMatrix(f.Diagonal),
            Upper = AllocateManagedRMatrix(f.Upper),
        };
        NativeStdLib.Free(nativeMPtr);
        NativeStdLib.Free(fPtr);
        return csF;
    }
}
