# FFM

To develop this project I used Java 24 and Gradle 8.3. I have only gotten it to run (relatively) cleanly on Linux. To build and run:
```bash
$ RMATRIX_LIB=/usr/local/lib/librmatrix.so # or wherever it's installed on your system
$ ./gradlew build
$ ./gradlew run [--args="<path to simple text file representing a matrix>"]
```
