import os
from setuptools import setup, Extension
from Cython.Build import cythonize

extensions = [
    Extension(
        "rashunal._rashunal",
        ["rashunal/rashunal.pyx"],
        include_dirs=[os.environ.get("RASHUNAL_INCLUDE", "/usr/local/include")],
        libraries=["rashunal"],
        library_dirs=[os.environ.get("RASHUNAL_LIB", "/usr/local/lib")]
    )
]

setup(
    name="rashunal",
    version="0.1.0",
    packages=["rashunal"],
    ext_modules=cythonize(
        extensions,
        language_level="3",
        include_path=["rashunal"]
    ),
)