// The Swift Programming Language
// https://docs.swift.org/swift-book
import ArgumentParser
import Foundation
import CRashunal
import CRMatrix
import Model

enum SwiftRMatrixError: Error {
    case runtimeError(String)
}

@main
struct SwiftRMatrix: ParsableCommand {
    @Option(help: "Specify the input file")
    public var inputFile: String

    public func run() throws {
        let url = URL(fileURLWithPath: inputFile)
        var inputText = ""
        do {
            inputText = try String(contentsOf: url, encoding: .utf8)
        } catch {
            throw SwiftRMatrixError.runtimeError("Error reading file [\(inputFile)]")
        }

        let one_half = Model.Rashunal(1, 2)
        print(one_half)

        let two = Model.Rashunal(2)
        print(two)

        let threeQuarters = Model.Rashunal([3, 4])
        print(threeQuarters)

        let one = Model.Rashunal([1])
        print(one)

        let m1 = Model.RMatrix(height: 2, width: 3, data: [
            Model.Rashunal(1, 1),
            Model.Rashunal(2, 1),
            Model.Rashunal(3, 2),
            Model.Rashunal(4, 3),
            Model.Rashunal(5, 1),
            Model.Rashunal(6, 1)
        ])
        print(m1)

        let m2 = Model.RMatrix([
            [[1], [2], [3, 2]],
            [[4,3], [5], [6]]
        ])
        print(m2)

        let lines = inputText
            .split(whereSeparator: \.isNewline)
            .map { $0.trimmingCharacters(in: .whitespaces) }
        for line in lines {
            print(line)
        }
        let data = lines.map { line in
            line.split(whereSeparator: { $0.isWhitespace })
            .map { token in
                token.split(separator: "/").map { Int($0)! }
            }
        }
        let m3 = Model.RMatrix(data)
        print(m3)

        let one_half_cr: UnsafeMutablePointer<CRashunal.Rashunal> = n_Rashunal(1, 2)!
        print("{\(one_half_cr.pointee.numerator),\(one_half_cr.pointee.denominator)}")

        let one_cr: UnsafeMutablePointer<CRashunal.Rashunal> = ni_Rashunal(1)!
        print("{\(one_cr.pointee.numerator),\(one_cr.pointee.denominator)}")

        let crm: OpaquePointer = new_RMatrix(1, 2, [one_half_cr, one_cr])
        print("OpaquePointer: height = \(RMatrix_height(crm)), width = \(RMatrix_width(crm))")

        let cgh: UnsafeMutablePointer<CRMatrix.Gauss_Factorization> = RMatrix_gelim(crm)
    }
}
