import sys
from rashunal import Rashunal
from rmatrix import RMatrix

data = []
if len(sys.argv) == 1:
    print('using demo data')
    data = [
        [[1],    [2], [3, 2]],
        [[4, 3], [5], [6]]
    ]
else:
    print(f"using data from file {sys.argv[1]}")
    with open(sys.argv[1], 'r') as my_file:
        data = [
            [list(map(int, el.split('/'))) for el in line.split()]
            for line in my_file
        ]

m = RMatrix(data)
print("Original matrix:")
print(data)

(p_inverse, lower, diagonal, upper) = m.factor()

print()
print("Lower:")
print(p_inverse)

print()
print("Lower:")
print(lower)

print()
print("Diagonal:")
print(diagonal)

print()
print("Upper:")
print(upper)
