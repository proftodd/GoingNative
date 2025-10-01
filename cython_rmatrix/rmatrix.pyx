from libc.stdlib cimport malloc, free
cimport crashunal, crmatrix

cdef class RMatrix:
    cdef crmatrix.RMatrix *_c_rmatrix

    def __init__(self, m):
        cdef el_count = m.height * m.width
        cdef crashunal.Rashunal **arr = <crashunal.Rashunal**> malloc(el_count * sizeof(crashunal.Rashunal*))
        if arr is NULL:
            raise MemoryError()

        try:
            for i in range(el_count):
                el = m.data[i]
                num = el.numerator
                den = el.denominator
                arr[i] = crashunal.n_Rashunal(num, den)
                if arr[i] is NULL:
                    raise MemoryError()
            self._c_rmatrix = crmatrix.new_RMatrix(m.height, m.width, arr)
            if self._c_rmatrix is NULL:
                raise MemoryError()
        finally:
            for i in range(el_count):
                if arr[i] is not NULL:
                    free(arr[i])
            free(arr)
    
    def __dealloc__(self):
        if self._c_rmatrix is not NULL:
            crmatrix.free_RMatrix(self._c_rmatrix)
            self._c_rmatrix = NULL

    @property
    def height(self):
        return crmatrix.RMatrix_height(self._c_rmatrix)

    @property
    def width(self):
        return crmatrix.RMatrix_width(self._c_rmatrix)
