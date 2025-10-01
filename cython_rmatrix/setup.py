from setuptools import Extension, setup
from Cython.Build import cythonize

setup(
    ext_modules = cythonize([
        Extension("rashunal", ["rashunal.pyx"], libraries=["rashunal"]),
        Extension("rmatrix", ["rmatrix.pyx"], libraries=["rashunal", "rmatrix"])
    ])
)
