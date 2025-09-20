// See https://aka.ms/new-console-template for more information
using CsRMatrix.Engine;
using CsRMatrix.Model;

int[][][] data;
if (args.Length > 0)
{
    Console.WriteLine($"Reading matrix from {args[0]}");
    data = File.ReadLines(args[0])
        .Select(l => l.Split(' ', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries)
                      .Select(e => e.Split('/', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries)
                                    .Select(n => int.Parse(n))
                                    .ToArray())
                      .ToArray())
        .ToArray();
}
else
{
    Console.WriteLine("Reading matrix from demo data");

    data =
    [
        [ [1],    [2], [3, 2], ],
        [ [4, 3], [5], [6],    ],
    ];
}

var csRashunals = data.SelectMany(a => a)
    .Select(cell =>
    {
        int num = cell[0];
        int den = cell.Length == 1 ? 1 : cell[1];
        return new CsRashunal { Numerator = num, Denominator = den };
    })
    .ToArray();
CsRMatrix.Model.CsRMatrix m = new() { Height = data.Length, Width = data[0].Length, Data = csRashunals, };

Console.WriteLine("Starting Matrix:");
Console.WriteLine(m.ToString());

var f = CsRMatrixNativeAdapter.Factor(m);
Console.WriteLine();
Console.WriteLine("PInverse:");
Console.WriteLine(f.PInverse.ToString());

Console.WriteLine();
Console.WriteLine("Lower:");
Console.WriteLine(f.Lower.ToString());

Console.WriteLine();
Console.WriteLine("Diagonal:");
Console.WriteLine(f.Diagonal.ToString());

Console.WriteLine();
Console.WriteLine("Upper:");
Console.WriteLine(f.Upper.ToString());
