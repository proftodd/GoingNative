import os
import sys
from setuptools import setup, Extension
from Cython.Build import cythonize

extensions = [
    Extension(
        "rmatrix._rmatrix",
        ["rmatrix/rmatrix.pyx"],
        include_dirs=[os.environ.get("RMATRIX_INCLUDE", "/usr/local/include")],
        libraries=["rmatrix"],
        library_dirs=[os.environ.get("RMATRIX_LIB", "/usr/local/lib")]
    )
]

setup(
    name="rmatrix",
    version="0.1.0",
    packages=["rmatrix"],
    install_requires=["rashunal>=0.1.0"],
    ext_modules=cythonize(
        extensions,
        language_level="3",
        include_path=["rmatrix"]
    )
)
