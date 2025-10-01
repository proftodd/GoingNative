import RMatrix
import rashunal.rashunal as crash
import rmatrix.rmatrix as crm

r = crash.Rashunal(1, 2)
print(str(r))

data = [
    RMatrix.PRashunal([1, 1]),
    RMatrix.PRashunal([2, 1]),
    RMatrix.PRashunal([3, 2]),
    RMatrix.PRashunal([4, 3]),
    RMatrix.PRashunal([5, 1]),
    RMatrix.PRashunal([6, 1])
]
prm = RMatrix.PRMatrix(2, 3, data)
m = crm.RMatrix(prm)
print(f"m.height = {m.height}")
print(f"m.width = {m.width}")
