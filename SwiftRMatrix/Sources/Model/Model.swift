public struct Rashunal: CustomStringConvertible {
    public var numerator: Int
    public var denominator: Int

    public init(_ numerator: Int, _ denominator: Int = 1) {
        self.numerator = numerator
        self.denominator = denominator
    }

    public init(_ data: [Int]) {
        self.numerator = data[0]
        self.denominator = data.count > 1 ? data[1] : 1
    }

    public var description: String {
        return "{\(numerator),\(denominator)}"
    }
}

public struct RMatrix: CustomStringConvertible {
    public var height: Int
    public var width: Int
    public var data: [Rashunal]

    public init(height: Int, width: Int, data: [Rashunal]) {
        self.height = height
        self.width = width
        self.data = data
    }

    public init(_ data: [[[Int]]]) {
        self.height = data.count
        self.width = data[0].count
        self.data = data.flatMap { $0.map { Model.Rashunal($0) } }
    }

    public var description: String {
        var result = ""
        for i in 0 ... height - 1 {
            result += "[ "
            for j in 0 ... width - 1 {
                result += String(describing: data[i * width + j])
                result += " "
            }
            result += "]\n"
        }
        return result
    }
}

public struct GaussFactorization {
    public var PInverse: RMatrix
    public var Lower: RMatrix
    public var Diagonal: RMatrix
    public var Upper: RMatrix

    public init(PInverse: RMatrix, Lower: RMatrix, Diagonal: RMatrix, Upper: RMatrix) {
        self.PInverse = PInverse
        self.Lower = Lower
        self.Diagonal = Diagonal
        self.Upper = Upper
    }
}