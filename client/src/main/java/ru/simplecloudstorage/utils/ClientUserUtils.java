package ru.simplecloudstorage.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientUserUtils {
    public static String checkDirectory(String destinationPath) {
        String resultPath = "";
        if (Files.isDirectory(Path.of(destinationPath))) {
            return destinationPath;
        } else if (Files.isRegularFile(Path.of(destinationPath))) {
            resultPath = Paths.get(destinationPath).getParent().toString();
        }
        return resultPath;
    }
}
