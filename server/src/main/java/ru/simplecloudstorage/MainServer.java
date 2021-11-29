package ru.simplecloudstorage;

import ru.simplecloudstorage.server.ServerConnector;

public class MainServer {


    public static void main(String[] args) {
        startServer(args);
    }

    private static void setPortFromArgs(String[] args) {
        if (args.length > 0) {
            try {
                ServerConnector.setPort(Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                System.err.println("Введенный параметр порта не является числом." + System.lineSeparator()+
                        "Сервер запущен на порту по умолчанию - 9000" + System.lineSeparator());
            }
        } else ServerConnector.setPort(ServerConnector.getDefaultPortValue());
    }

    private static void startServer(String[] args) {
        setPortFromArgs(args);
        try {
            new ServerConnector().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
