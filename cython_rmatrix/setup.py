from setuptools import setup, Extension
from Cython.Build import cythonize

extensions = [
    Extension(
        "rashunal._rashunal",
        ["rashunal/rashunal.pyx"],
        include_dirs=[],
        libraries=["rashunal"],
        library_dirs=[],
    ),
    Extension(
        "rmatrix._rmatrix",
        ["rmatrix/rmatrix.pyx"],
        include_dirs=[],
        libraries=["rmatrix"],
        library_dirs=[]
    ),
]

setup(
    name="cython_rmatrix",
    packages=["rashunal", "rmatrix"],
    ext_modules=cythonize(
        extensions,
        language_level="3",
        include_path=["rashunal", "rmatrix"]
    ),
)
