import Foundation
#if os(Linux)
import Glibc
#else
import Darwin
#endif
import CRashunal
import CRMatrix

public class Rashunal: CustomStringConvertible {
    var _rashunal: UnsafePointer<CRashunal.Rashunal>

    fileprivate init(_ rashunal: UnsafePointer<CRashunal.Rashunal>) {
        self._rashunal = rashunal
    }

    public init(_ numerator: Int, _ denominator: Int = 1) {
        _rashunal = UnsafePointer(n_Rashunal(Int32(numerator), Int32(denominator)))
    }

    public init(_ data: [Int]) {
        _rashunal = UnsafePointer(n_Rashunal(Int32(data[0]), data.count > 1 ? Int32(data[1]) : 1))
    }

    public var numerator: Int { Int(_rashunal.pointee.numerator) }

    public var denominator: Int { Int(_rashunal.pointee.denominator) }

    public var description: String {
        return "{\(numerator),\(denominator)}"
    }

    deinit {
        free(UnsafeMutablePointer(mutating: _rashunal))
    }
}

public class RMatrix: CustomStringConvertible {
    var _rmatrix: OpaquePointer

    private init(_ rmatrix: OpaquePointer) {
        _rmatrix = rmatrix
    }

    public init(_ data: [[[Int]]]) {
        let height = data.count
        let width = data.first!.count

        let rashunals: [Rashunal] = data.flatMap { $0.map { Rashunal($0) } }

        let ptrArray = UnsafeMutablePointer<UnsafeMutablePointer<CRashunal.Rashunal>?>.allocate(capacity: rashunals.count)
        defer { ptrArray.deallocate() }
        for i in 0..<rashunals.count {
            ptrArray[i] = UnsafeMutablePointer(mutating: rashunals[i]._rashunal)
        }

        let m: OpaquePointer = withExtendedLifetime(rashunals) {
            new_RMatrix(numericCast(height), numericCast(width), ptrArray)
        }
        _rmatrix = m
    }

    public var height: Int { Int(RMatrix_height(_rmatrix)) }

    public var width: Int { Int(RMatrix_width(_rmatrix)) }

    public func factor() -> GaussFactorization {
        let gf = RMatrix_gelim(_rmatrix)!
        return GaussFactorization(
            PInverse: RMatrix(gf.pointee.pi),
            Lower: RMatrix(gf.pointee.l),
            Diagonal: RMatrix(gf.pointee.d),
            Upper: RMatrix(gf.pointee.u)
        )
    }

    public var description: String {
        var result = ""
        for i in 1...height {
            result += "[ "
            for j in 1...width {
                let cellPtr: UnsafePointer<CRashunal.Rashunal> = RMatrix_get(_rmatrix, i, j)
                result += "{\(cellPtr.pointee.numerator),\(cellPtr.pointee.denominator)}"
                result += " "
                free(UnsafeMutablePointer<CRashunal.Rashunal>(mutating: cellPtr))
            }
            result += "]\n"
        }
        return result
    }

    deinit {
        free_RMatrix(_rmatrix)
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