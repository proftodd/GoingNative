import ctypes
import os
import sys
import RMatrix

class RASHUNAL(ctypes.Structure):
    _fields_ = [("numerator", ctypes.c_int), ("denominator", ctypes.c_int)]

class RMATRIX(ctypes.Structure):
    pass

class GAUSS_FACTORIZATION(ctypes.Structure):
    _fields_ = [
        ("P_INVERSE", ctypes.POINTER(RMATRIX)),
        ("LOWER", ctypes.POINTER(RMATRIX)),
        ("DIAGONAL", ctypes.POINTER(RMATRIX)),
        ("UPPER", ctypes.POINTER(RMATRIX))
    ]

def get_dll_dirs_from_env(env_var="RMATRIX_LIB_DIRS"):
    val = os.environ.get(env_var, "")
    if not val:
        return []
    return val.split(os.pathsep)

def load_standard_library():
    if sys.platform.startswith("win"):
        return ctypes.CDLL('ucrtbase.dll')
    elif sys.platform.startswith("darwin"):
        return ctypes.CDLL('libSystem.dylib')
    else:
        return ctypes.CDLL('libc.so.6')

def load_library(lib_name):
    if sys.platform.startswith("win"):
        dll_dirs = get_dll_dirs_from_env()
        for d in dll_dirs:
            if not os.path.isdir(d):
                continue
            try:
                os.add_dll_directory(d)
            except AttributeError:
                ctypes.windll.kernel32.SetDllDirectoryW(d)
        filename = f"{lib_name}.dll"
    elif sys.platform.startswith("darwin"):
        filename = f"lib{lib_name}.dylib"
    else:
        filename = f"lib{lib_name}.so"

    try:
        return ctypes.CDLL(filename)
    except OSError as e:
        raise OSError(f"Could not load library '{filename}'")

_std_lib = load_standard_library()
_std_lib.free.argtypes = (ctypes.c_void_p,)
_std_lib.malloc.argtypes = (ctypes.c_size_t,)

_rashunal_lib = load_library('rashunal')
_rashunal_lib.n_Rashunal.argtypes = (ctypes.c_int, ctypes.c_int)
_rashunal_lib.n_Rashunal.restype = ctypes.POINTER(RASHUNAL)

_rmatrix_lib = load_library('rmatrix')
_rmatrix_lib.new_RMatrix.argtypes = (ctypes.c_size_t, ctypes.c_size_t, ctypes.POINTER(ctypes.POINTER(RASHUNAL)))
_rmatrix_lib.new_RMatrix.restype = ctypes.POINTER(RMATRIX)

_rmatrix_lib.free_RMatrix.argtypes = (ctypes.POINTER(RMATRIX),)

_rmatrix_lib.RMatrix_gelim.argtypes = (ctypes.POINTER(RMATRIX),)
_rmatrix_lib.RMatrix_gelim.restype = ctypes.POINTER(GAUSS_FACTORIZATION)

_rmatrix_lib.RMatrix_height.argtypes = (ctypes.POINTER(RMATRIX),)
_rmatrix_lib.RMatrix_width.argtypes = (ctypes.POINTER(RMATRIX),)

_rmatrix_lib.RMatrix_get.argtypes = (ctypes.POINTER(RMATRIX), ctypes.c_size_t, ctypes.c_size_t)
_rmatrix_lib.RMatrix_get.restype = ctypes.POINTER(RASHUNAL)

def allocate_c_rmatrix(m):
    height = m.height
    width = m.width
    element_count = height * width

    c_rashunal_pointers = (ctypes.POINTER(RASHUNAL) * element_count)()
    for i in range(element_count):
        pel = m.data[i]
        r = _rashunal_lib.n_Rashunal(pel.numerator, pel.denominator)
        c_rashunal_pointers[i] = ctypes.cast(r, ctypes.POINTER(RASHUNAL))
    c_rmatrix = _rmatrix_lib.new_RMatrix(height, width, c_rashunal_pointers)
    for i in range(element_count):
        cel = c_rashunal_pointers[i]
        _std_lib.free(cel)
    return c_rmatrix

def allocate_python_rmatrix(m):
    height = _rmatrix_lib.RMatrix_height(m)
    width = _rmatrix_lib.RMatrix_width(m)
    p_rashunals = []
    for i in range(1, height + 1):
        for j in range(1, width + 1):
            c_rashunal = _rmatrix_lib.RMatrix_get(m, i, j)
            p_rashunals.append(RMatrix.PRashunal((c_rashunal.contents.numerator, c_rashunal.contents.denominator)))
            _std_lib.free(ctypes.cast(c_rashunal, ctypes.c_void_p))
    return RMatrix.PRMatrix(height, width, p_rashunals)

def factor(m):
    crm = allocate_c_rmatrix(m)
    gf = _rmatrix_lib.RMatrix_gelim(crm)

    p_inverse = allocate_python_rmatrix(gf.contents.P_INVERSE)
    lower = allocate_python_rmatrix(gf.contents.LOWER)
    diagonal = allocate_python_rmatrix(gf.contents.DIAGONAL)
    upper = allocate_python_rmatrix(gf.contents.UPPER)

    _rmatrix_lib.free_RMatrix(gf.contents.P_INVERSE)
    _rmatrix_lib.free_RMatrix(gf.contents.LOWER)
    _rmatrix_lib.free_RMatrix(gf.contents.DIAGONAL)
    _rmatrix_lib.free_RMatrix(gf.contents.UPPER)
    _std_lib.free(ctypes.cast(gf, ctypes.c_void_p))

    return RMatrix.PGaussFactorization(p_inverse, lower, diagonal, upper)
