from libc.stdlib cimport malloc, free
cimport crashunal
cimport crmatrix

cdef class RMatrix:
    cdef crmatrix.RMatrix *_c_rmatrix

    def __init__(self, data):
        cdef height = len(data)
        cdef width = len(data[0])
        cdef el_count = height * width
        cdef crashunal.Rashunal **arr = <crashunal.Rashunal**> malloc(el_count * sizeof(crashunal.Rashunal*))
        if arr is NULL:
            raise MemoryError()

        try:
            for i in range(el_count):
                el = data[i // width][i % width]
                num = el[0]
                den = el[1] if len(el) == 2 else 1
                arr[i] = crashunal.n_Rashunal(num, den)
                if arr[i] is NULL:
                    raise MemoryError()
            self._c_rmatrix = crmatrix.new_RMatrix(height, width, arr)
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
