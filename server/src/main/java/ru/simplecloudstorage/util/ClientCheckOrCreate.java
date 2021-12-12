package ru.simplecloudstorage.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientCheckOrCreate {
    private static final Logger logger = LoggerFactory.getLogger(DBCheckOrCreate.class);

    public static void tryCheckClient(String login, String programRootPath) {
        if (!Files.exists(Path.of(programRootPath, login))) {
            try {
                Files.createDirectory(Path.of(programRootPath, login));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
