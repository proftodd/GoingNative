from rashunal import Rashunal
from rmatrix import RMatrix

r = Rashunal(1, 2)
print(str(r))

data = [
    [[1], [2], [3, 2]],
    [[4, 3], [5], [6]]
]
m = RMatrix(data)
print(f"m.height = {m.height}")
print(f"m.width = {m.width}")
(p_inverse, lower, diagonal, upper) = m.factor()
print(p_inverse)
print(lower)
print(diagonal)
print(upper)
