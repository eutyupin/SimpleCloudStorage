package ru.simplecloudstorage.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public  class ServerUserUtils {
    private static final Logger logger = LogManager.getLogger(ServerUserUtils.class);

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
            logger.info("Deleted file/directory: " + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        logger.info("Deleted file: " + path.toString());
        return true;
    }

    public static boolean createNewDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        logger.info("Directory created: " + path.toString());
        return true;
    }

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
