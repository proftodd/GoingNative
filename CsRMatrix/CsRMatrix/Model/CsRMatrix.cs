using System.Text;

namespace CsRMatrix.Model;

public record CsRMatrix
{
    public int Height;
    public int Width;
    public CsRashunal[] Data = [];

    public override string ToString()
    {
        var sb = new StringBuilder();
        for (int i = 0; i < Height; ++i)
        {
            sb.Append("[ ");
            for (int j = 0; j < Width; ++j)
            {
                var el = Data[i * Width + j];
                sb.Append(el.ToString());
                sb.Append(' ');
            }
            sb.Append("]\n");
        }
        return sb.ToString();
    }
}
