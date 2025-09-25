import sys
import Native
import RMatrix

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
            [el.split('/') for el in line.split()]
            for line in my_file
        ]

p_rashunals = [
    RMatrix.PRashunal(el)
    for row in data
    for el in row
]

prm = RMatrix.PRMatrix(len(data), len(data[0]), p_rashunals)
print("Input matrix:")
print(prm)

f = Native.factor(prm)

print()
print("PInverse:")
print(f.p_inverse)

print()
print("Lower:")
print(f.lower)

print()
print("Diagonal:")
print(f.diagonal)

print()
print("Upper:")
print(f.upper)
