namespace CsRMatrix.Model;

public record CsGaussFactorization
{
    public required CsRMatrix PInverse;
    public required CsRMatrix Lower;
    public required CsRMatrix Diagonal;
    public required CsRMatrix Upper;
}
