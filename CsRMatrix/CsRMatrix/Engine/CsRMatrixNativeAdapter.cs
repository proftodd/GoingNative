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

    [LibraryImport("rashunal", EntryPoint = "n_Rashunal")]
    private static partial IntPtr n_Rashunal(int numerator, int denominator);

    public static CsGaussFactorization Factor(int[][][] data)
    {
        IntPtr rPtr = n_Rashunal(1, 2);
        Rashunal r = Marshal.PtrToStructure<Rashunal>(rPtr);
        CsRashunal cr = new() { Numerator = r.numerator, Denominator = r.denominator };
        NativeStdLib.Free(rPtr);
        return new CsGaussFactorization
        {
            PInverse = new Model.CsRMatrix { Height = 1, Width = 1, Data = [cr], },
            Lower = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
            Diagonal = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
            Upper = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
        };
    }
}
