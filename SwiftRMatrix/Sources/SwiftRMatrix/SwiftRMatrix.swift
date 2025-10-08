// The Swift Programming Language
// https://docs.swift.org/swift-book
import ArgumentParser
import Foundation

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
        let lines = inputText.components(separatedBy: .newlines)
        for line in lines {
            print(line)
        }
    }
}
