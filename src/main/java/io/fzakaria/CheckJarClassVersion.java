package io.fzakaria;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Checks the class file version of all class files within a JAR file.
 * To execute this program, provide the path to the JAR file as an argument.
 * https://gist.githubusercontent.com/jpstotz/84091fd79d1e5682f64aa5daac868363/raw/12175283f64149b3360b5bcec9cddb3b85a012ae/CheckJarClassVersion.java
 */
public class CheckJarClassVersion {

    private static void printUsage() {
        System.out.println("""
            Usage: check-jar-versions [-h] [jar]
            Checks the class file version of all class files within a JAR file.
            
            positional arguments:
                jar        The jar file to analyze

            options:
                -h, --help      show this help message and exit
            """);     
    }

    public static void main(String[] args) {
        
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            printUsage();
            System.exit(0);
        }

        if (args.length != 1) {
            printUsage();
            System.exit(1);
        }

        Path jarPath = Path.of(args[0]);
        if (!Files.exists(jarPath) || !Files.isRegularFile(jarPath)) {
            System.err.println("Error: The provided path does not exist or is not a valid file: " + jarPath);
            System.exit(1);
        }

        Map<Integer, List<String>> classVersionMap = new TreeMap<>();
        try (ZipFile jarFile = new ZipFile(jarPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                final String filename = entry.getName();

                if (!filename.endsWith(".class")) {
                    continue;
                }

                try (DataInputStream in = new DataInputStream(jarFile.getInputStream(entry))) {
                    int magicNumber = in.readInt();
                    if (magicNumber != 0xCAFEBABE) {
                        System.err.println("Skipping non-class file: " + entry.getName());
                        continue;
                    }

					int classFileVersion = in.readInt();
                    classVersionMap
                        .computeIfAbsent(classFileVersion, k -> new ArrayList<>())
                        .add(filename);
                } catch (IOException e) {
                    System.err.println("Error reading class file " + filename + ": " + e.getMessage());
                }
            }

            classVersionMap.forEach((version, files) -> {
                    int javaVersion = version - 44;
                    System.out.printf("Class File Format Version: %d (Java %d) - Number of files: %d%n",
                                      version, javaVersion, files.size());
                    // Uncomment the next line to list all files in each category
                    // files.forEach(file -> System.out.println("\t" + file));
            });
        } catch (IOException e) {
            System.err.println("Error processing the JAR file: " + e.getMessage());
        }
    }
}
