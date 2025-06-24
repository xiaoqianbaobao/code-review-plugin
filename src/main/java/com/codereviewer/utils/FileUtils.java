package com.codereviewer.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static List<File> findJavaFiles(File directory) {
        try {
            return Files.walk(directory.toPath())
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.toString().contains("target"))
                    .filter(path -> !path.toString().contains(".git"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to find Java files", e);
        }
    }

    public static String readFileContent(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), "UTF-8");
    }

    public static void writeFile(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes("UTF-8"));
    }

    public static boolean isJavaFile(String filePath) {
        return filePath != null && filePath.endsWith(".java");
    }

    public static String getRelativePath(File baseDir, File file) {
        return baseDir.toPath().relativize(file.toPath()).toString();
    }
}