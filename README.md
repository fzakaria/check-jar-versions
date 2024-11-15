# check-jar-versions

[![built with nix](https://builtwithnix.org/badge.svg)](https://builtwithnix.org)

Check the class file version of all class files within a JAR file

```console
❯ nix run github:fzakaria/check-jar-versions -- --help
Usage: check-jar-versions [jar]
Checks the class file version of all class files within a JAR file.

positional arguments:
    jar        The jar file to analyze

options:
    -h, --help      show this help message and exit
```

Using it is pretty straightforward.

```
> nix run github:fzakaria/check-jar-versions --  bazel-bin/hello/libhello.jar
Class File Format Version: 55 (Java 11) - Number of files: 3831
```
