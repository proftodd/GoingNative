import sys

data = []
demoData = [
    [[1],    [2], [3, 2]],
    [[4, 3], [5], [6]]
]
if len(sys.argv) == 1:
    print('using demo data')
    data = demoData
else:
    with open(sys.argv[1], 'r') as my_file:
        print(f"using data from file {sys.argv[1]}")
        print(my_file.read())
