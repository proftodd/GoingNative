from libc.stdlib cimport malloc, free
cimport crashunal
cimport crmatrix

cdef class RMatrix:
    cdef crmatrix.RMatrix *_c_rmatrix

    def __cinit__(self, data):
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
    
    def factor(self):
        cdef crmatrix.Gauss_Factorization *f
        f = crmatrix.RMatrix_gelim(self._c_rmatrix)
        try:
            result = (
                _crmatrix_to_2d_array(f.pi),
                _crmatrix_to_2d_array(f.l),
                _crmatrix_to_2d_array(f.d),
                _crmatrix_to_2d_array(f.u)
            )
        finally:
            if f.pi != NULL: crmatrix.free_RMatrix(<crmatrix.RMatrix *>f.pi)
            if f.l  != NULL: crmatrix.free_RMatrix(<crmatrix.RMatrix *>f.l)
            if f.d  != NULL: crmatrix.free_RMatrix(<crmatrix.RMatrix *>f.d)
            if f.u  != NULL: crmatrix.free_RMatrix(<crmatrix.RMatrix *>f.u)
            free(f)
        return result

cdef _crmatrix_to_2d_array(const crmatrix.RMatrix *crm):
    cdef height = crmatrix.RMatrix_height(crm)
    cdef width = crmatrix.RMatrix_width(crm)
    cdef result = []
    cdef const crashunal.Rashunal *el
    for i in range(height):
        row = []
        for j in range(width):
            el = crmatrix.RMatrix_get(crm, i + 1, j + 1)
            row.append((el.numerator, el.denominator))
            crashunal.free(<void *>el)
        result.append(row)
    return result
