cimport crashunal

cdef class Rashunal:
    cdef crashunal.Rashunal *_c_rashunal

    def __cinit__(self, numerator, denominator):
        self._c_rashunal = crashunal.n_Rashunal(numerator, denominator)
        if self._c_rashunal is NULL:
            raise MemoryError()
    
    def __dealloc__(self):
        if self._c_rashunal is not NULL:
            crashunal.free(self._c_rashunal)
            self._c_rashunal = NULL
    
    def __str__(self):
        return f"{{{self._c_rashunal.numerator},{self._c_rashunal.denominator}}}"
    
    @property
    def numerator(self):
        return self._c_rashunal.numerator
    
    @property
    def denominator(self):
        return self._c_rashunal.denominator
