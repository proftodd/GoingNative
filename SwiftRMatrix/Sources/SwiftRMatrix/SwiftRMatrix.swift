// The Swift Programming Language
// https://docs.swift.org/swift-book
import ArgumentParser
import Foundation
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
        let data = inputText
            .split(whereSeparator: \.isNewline)
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .map { line in line.split(whereSeparator: { $0.isWhitespace })
            .map { token in token.split(separator: "/").map { Int($0)! } }
        }
        let m = Model.RMatrix(data)
        print("Input matrix:")
        print(m)

        let factor = m.factor()
        print("Factors into:")
        print("PInverse:")
        print(factor.PInverse)

        print("Lower:")
        print(factor.Lower)

        print("Diagonal:")
        print(factor.Diagonal)

        print("Upper:")
        print(factor.Upper)
    }
}
