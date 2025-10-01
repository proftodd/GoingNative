cimport crashunal

cdef extern from "rmatrix.h":
    ctypedef struct RMatrix:
        pass
    
    RMatrix *new_RMatrix(size_t height, size_t width, crashunal.Rashunal **data)
    void free_RMatrix(RMatrix *m)
    size_t RMatrix_height(RMatrix *m)
    size_t RMatrix_width(RMatrix *m)
    crashunal.Rashunal *RMatrix_get(RMatrix *m, size_t row, size_t col)

cdef extern from "stdlib.h":
    void free(void *ptr)
    void *malloc(size_t)
