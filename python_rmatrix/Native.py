import RMatrix

def factor(m):
    p_inverse = RMatrix.PRMatrix(0, 0, [])
    lower = RMatrix.PRMatrix(0, 0, [])
    diagonal = RMatrix.PRMatrix(0, 0, [])
    upper = RMatrix.PRMatrix(0, 0, [])
    return RMatrix.PGaussFactorization(p_inverse, lower, diagonal, upper)
