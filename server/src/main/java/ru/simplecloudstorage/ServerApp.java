package ru.simplecloudstorage;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplecloudstorage.server.ServerConnector;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class);
    private static ExecutorService consoleReaderService = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        startServer(args);
    }

    private static void argumentSetParameters(String[] args) {
        PropertyConfigurator.configure("server/src/main/resources/log4jServer.properties");
        if (args.length > 0) {
            try {
                ServerConnector.setPort(Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                System.out.println("Entered port number is not a digit." + System.lineSeparator()+
                        "Server will started with default port number - 9000" + System.lineSeparator());
                logger.error(String.format("Entered port number %s is not a digit. Server will started with default port number - %d",
                        args[0], ServerConnector.getDefaultPortValue()));
            }
        } else ServerConnector.setPort(ServerConnector.getDefaultPortValue());
    }

    private static void startServer(String[] args) {
        argumentSetParameters(args);
        try {
            new ServerConnector().run();
        } catch (InterruptedException e) {
            logger.error(e.getStackTrace().toString());
        }
    }
}
