import PRMatrix
from rashunal import Rashunal
from rmatrix import RMatrix

r = Rashunal(1, 2)
print(str(r))

data = [
    PRMatrix.PRashunal([1, 1]),
    PRMatrix.PRashunal([2, 1]),
    PRMatrix.PRashunal([3, 2]),
    PRMatrix.PRashunal([4, 3]),
    PRMatrix.PRashunal([5, 1]),
    PRMatrix.PRashunal([6, 1])
]
prm = PRMatrix.PRMatrix(2, 3, data)
m = RMatrix(prm)
print(f"m.height = {m.height}")
print(f"m.width = {m.width}")
