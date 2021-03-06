package ru.simplecloudstorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.simplecloudstorage.server.ServerConnector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private static final Logger logger = LogManager.getLogger(ServerApp.class);
    private static ExecutorService consoleReaderService = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        startServer(args);
    }

    private static void argumentSetParameters(String[] args) {
//        PropertyConfigurator.configure("server/src/main/resources/log4j.properties");
        if (args.length > 0) {
            try {
                ServerConnector.setPort(Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                logger.warn(String.format("Entered port number %s is not a digit. Server will started with default port number - %d",
                        args[0], ServerConnector.getDefaultPortValue()));
                System.out.printf("Entered port number %s is not a digit. Server will started with default port number - %d",
                        args[0], ServerConnector.getDefaultPortValue());
            }
        } else ServerConnector.setPort(ServerConnector.getDefaultPortValue());
    }

    private static void startServer(String[] args) {
        argumentSetParameters(args);
        try {
            new ServerConnector().run();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }
}
