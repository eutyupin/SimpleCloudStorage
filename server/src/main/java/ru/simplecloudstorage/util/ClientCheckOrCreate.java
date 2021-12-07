package ru.simplecloudstorage.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientCheckOrCreate {

    public static void tryCheckClient(String login, String programRootPath) {
        if (!Files.exists(Path.of(programRootPath, login))) {
            try {
                Files.createDirectory(Path.of(programRootPath, login));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
