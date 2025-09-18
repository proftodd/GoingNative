using CsRMatrix.Model;
using System.Runtime.InteropServices;

namespace CsRMatrix.Engine;

public static partial class CsRMatrixNativeAdapter
{
    [LibraryImport("rashunal", EntryPoint = "n_Rashunal")]
    private static partial IntPtr n_Rashunal(int numerator, int denominator);

    public static CsGaussFactorization Factor(int[][][] data)
    {
        Rashunal r = Marshal.PtrToStructure<Rashunal>(n_Rashunal(1, 2));
        CsRashunal cr = new() { Numerator = r.numerator, Denominator = r.denominator };
        return new CsGaussFactorization
        {
            PInverse = new Model.CsRMatrix { Height = 1, Width = 1, Data = [cr], },
            Lower = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
            Diagonal = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
            Upper = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
        };
    }

    private struct Rashunal
    {
        public int numerator;
        public int denominator;
    }
}
