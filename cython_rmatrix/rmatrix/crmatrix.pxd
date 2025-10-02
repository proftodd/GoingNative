cimport crashunal

cdef extern from "rmatrix.h":
    ctypedef struct RMatrix:
        pass
    
    RMatrix *new_RMatrix(size_t height, size_t width, crashunal.Rashunal **data)
    void free_RMatrix(RMatrix *m)
    size_t RMatrix_height(const RMatrix *m)
    size_t RMatrix_width(const RMatrix *m)
    Gauss_Factorization *RMatrix_gelim(const RMatrix *m)
    crashunal.Rashunal *RMatrix_get(const RMatrix *m, size_t row, size_t col)

    ctypedef struct Gauss_Factorization:
        const RMatrix *pi
        const RMatrix *l
        const RMatrix *d
        const RMatrix *u

cdef extern from "stdlib.h":
    void free(void *ptr)
    void *malloc(size_t)
