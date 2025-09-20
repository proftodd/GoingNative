namespace CsRMatrix.Model;

public record CsRashunal
{
    public int Numerator;
    public int Denominator;

    public override string ToString() => "{" + Numerator + "/" + Denominator + "}";
}
