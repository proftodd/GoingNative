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

    [DllImport("rashunal", EntryPoint = "n_Rashunal")]
    private static extern RashunalHandle n_Rashunal(int numerator, int denominator);

    [DllImport("rmatrix", EntryPoint = "new_RMatrix")]
    private static extern RMatrixHandle new_RMatrix(int height, int width, IntPtr data);

    [DllImport("rmatrix", EntryPoint = "RMatrix_height")]
    private static extern int RMatrix_height(RMatrixHandle m);

    [DllImport("rmatrix", EntryPoint = "RMatrix_width")]
    private static extern int RMatrix_width(RMatrixHandle m);

    [DllImport("rmatrix", EntryPoint = "RMatrix_get")]
    private static extern RashunalHandle RMatrix_get(RMatrixHandle m, int row, int col);

    [DllImport("rmatrix", EntryPoint = "RMatrix_gelim")]
    private static extern GaussFactorizationHandle RMatrix_gelim(RMatrixHandle m);

    private static RashunalHandle AllocRashunal(int num, int den)
    {
        IntPtr ptr = NativeStdLib.Malloc((UIntPtr)Marshal.SizeOf<Rashunal>());
        var value = new Rashunal { numerator = num, denominator = den };
        Marshal.StructureToPtr(value, ptr, false);
        return new RashunalHandle(ptr, ownsHandle: true);
    }

    private static RMatrixHandle AllocateNativeRMatrix(Model.CsRMatrix m)
    {
        int elementCount = m.Height * m.Width;
        IntPtr elementArray = NativeStdLib.Malloc((UIntPtr)(IntPtr.Size * elementCount));
        unsafe
        {
            var pArray = (IntPtr*)elementArray;
            for (int i = 0; i < elementCount; ++i)
            {
                var element = m.Data[i];
                var elementHandle = AllocRashunal(element.Numerator, element.Denominator);
                pArray[i] = elementHandle.DangerousGetHandle();
            }
            var rMatrixHandle = new_RMatrix(m.Height, m.Width, elementArray);

            NativeStdLib.Free(elementArray);

            return rMatrixHandle;
        }
    }

    private static Model.CsRMatrix AllocateManagedRMatrix(RMatrixHandle m)
    {
        int height = RMatrix_height(m);
        int width = RMatrix_width(m);
        var data = new CsRashunal[height * width];
        for (int i = 1; i <= height; ++i)
        {
            for (int j = 1; j <= width; ++j)
            {
                using var rPtr = RMatrix_get(m, i, j);
                var r = Marshal.PtrToStructure<Rashunal>(rPtr.DangerousGetHandle());
                data[(i - 1) * width + (j - 1)] = new CsRashunal { Numerator = r.numerator, Denominator = r.denominator };
            }
        }
        return new Model.CsRMatrix { Height = height, Width = width, Data = data, };
    }

    public static CsGaussFactorization Factor(Model.CsRMatrix m)
    {
        using var nativeM = AllocateNativeRMatrix(m);
        using var fPtr = RMatrix_gelim(nativeM);
        var f = Marshal.PtrToStructure<GaussFactorization>(fPtr.DangerousGetHandle());
        using var pInv = new RMatrixHandle(f.PInverse, ownsHandle: true);
        using var pLower = new RMatrixHandle(f.Lower, ownsHandle: true);
        using var pDiagonal = new RMatrixHandle(f.Diagonal, ownsHandle: true);
        using var pUpper = new RMatrixHandle(f.Upper, ownsHandle: true);
        return new CsGaussFactorization
        {
            PInverse = AllocateManagedRMatrix(pInv),
            Lower = AllocateManagedRMatrix(pLower),
            Diagonal = AllocateManagedRMatrix(pDiagonal),
            Upper = AllocateManagedRMatrix(pUpper),
        };
    }
}
