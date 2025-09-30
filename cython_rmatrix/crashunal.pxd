cdef extern from "rashunal.h":
    ctypedef struct Rashunal:
        int numerator
        int denominator
    
    Rashunal *n_Rashunal(int numerator, int denominator)

cdef extern from "stdlib.h":
    void free(void *ptr)
