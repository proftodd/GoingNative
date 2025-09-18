using CsRMatrix.Model;

namespace CsRMatrix.Engine;

public static class CsRMatrixNativeAdapter
{
    public static CsGaussFactorization Factor(int[][][] data)
    {
        return new CsGaussFactorization
        {
            PInverse = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
            Lower = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
            Diagonal = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
            Upper = new Model.CsRMatrix { Height = 0, Width = 0, Data = [], },
        };
    }
}
