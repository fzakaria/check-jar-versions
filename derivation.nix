{
  lib,
  stdenv,
  graalvm-ce,
}: let
  fs = lib.fileset;
in
  stdenv.mkDerivation {
    pname = "check-jar-versions";
    version = "1.0";

    src = fs.toSource {
      root = ./.;
      fileset = ./src/main/java/io/fzakaria/CheckJarClassVersion.java;
    };

    buildInputs = [graalvm-ce];

    # Build the AOT binary
    buildPhase = ''
      javac ./src/main/java/io/fzakaria/CheckJarClassVersion.java
      native-image -H:+UnlockExperimentalVMOptions -H:-CheckToolchain -H:+ReportExceptionStackTraces -march=compatibility \
                   io.fzakaria.CheckJarClassVersion check-jar-class-version -cp ./src/main/java
    '';

    # Install the binary
    installPhase = ''
      mkdir -p $out/bin
      mv check-jar-class-version $out/bin/
    '';
  }
