package ru.simplecloudstorage.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplecloudstorage.server.ServerHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public  class ServerUserUtils {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

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
        logger.info("Deleted file: " + path.toString());
        return true;
    }

    public static boolean createNewDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        logger.info("Directory created: " + path.toString());
        return true;
    }
}
