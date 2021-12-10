package ru.simplecloudstorage.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public  class ServerUserUtils {

    public static boolean delete (Path path) {
        if (Files.isDirectory(path)) {
            return recursiveDelete(path.toFile());
        } else if (Files.isRegularFile(path)) {
            return deleteFile(path);
        }
        return false;
    }

    private static boolean recursiveDelete(File file) {
        try {
            if (!file.exists()) return false;
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    recursiveDelete(f);
                }
            }
            file.delete();
            System.out.println("Deleted file/folder: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Deleted file: " + path.toString());
        return true;
    }
}
