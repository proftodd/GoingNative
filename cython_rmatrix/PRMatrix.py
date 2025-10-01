class PRashunal:
    def __init__(self, data):
        self.numerator = data[0]
        self.denominator = 1 if len(data) == 1 else data[1]

    def __str__(self):
        return f"{{{self.numerator},{self.denominator}}}"

class PRMatrix:
    def __init__(self, height, width, data):
        self.height = height
        self.width = width
        self.data = data

    def __str__(self):
        return "\n".join(
            [
                "[ " + " ".join(str(self.data[i * self.width + j]) for j in range(self.width)) + " ]"
                for i in range(self.height)
            ]
        )

class PGaussFactorization:
    def __init__(self, p_inverse, lower, diagonal, upper):
        self.p_inverse = p_inverse
        self.lower = lower
        self.diagonal = diagonal
        self.upper = upper
