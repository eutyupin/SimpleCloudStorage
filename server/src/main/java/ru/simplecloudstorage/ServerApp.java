package ru.simplecloudstorage;

//import org.apache.log4j.PropertyConfigurator;
import ru.simplecloudstorage.server.ServerConnector;

public class ServerApp {


    public static void main(String[] args) {
        startServer(args);
    }

    private static void argumentSetParameters(String[] args) {
//        PropertyConfigurator.configure("server/src/main/resources/log4j.properties");
        if (args.length > 0) {
            try {
                ServerConnector.setPort(Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                System.out.println("Entered port number is not a digit." + System.lineSeparator()+
                        "Server will started with default port number - 9000" + System.lineSeparator());
            }
        } else ServerConnector.setPort(ServerConnector.getDefaultPortValue());
    }

    private static void startServer(String[] args) {
        argumentSetParameters(args);
        try {
            new ServerConnector().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
