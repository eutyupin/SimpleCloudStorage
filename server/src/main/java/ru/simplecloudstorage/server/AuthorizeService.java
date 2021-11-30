package ru.simplecloudstorage.server;

import ru.simplecloudstorage.commands.*;

import java.sql.*;

public class AuthorizeService {

    private final String DB_URL = "jdbc:sqlite:C:/Users/Jeka/IdeaProjects/SimpleCloudStorage/server/db/base.db";
    private String errorMessage;

    public BaseCommand tryAuthorize(String login, String password) {
        AuthOkCommand authOkCommand = new AuthOkCommand();
        AuthFailedCommand authFailedCommand = new AuthFailedCommand();
        errorMessage = "Неверный логин или пароль. Попробуйте еще раз или зарегистрируйтесь.";
        authFailedCommand.setMessage(errorMessage);
        boolean correctLogin = false;
        boolean correctPassword = false;
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            Statement statement = connection.createStatement();
            String queryString = String.format("SELECT login_value, password_value " +
                    "FROM login JOIN password on login.login_value = \'%s\' " +
                    "AND password.password_value = \'%s\' " +
                    "AND login.id = password.login_value_id;", login, password);
            ResultSet resultSet = statement.executeQuery(queryString);
            if (resultSet.next()) {
                correctLogin = login.equals(resultSet.getString(1));
                correctPassword = password.equals(resultSet.getString(2));
            } else {
                correctLogin = false;
                correctPassword = false;
                System.out.println("Error: " + errorMessage);
            }
        } catch (SQLException e) {
           correctLogin = false;
           correctPassword = false;
        }
        if (correctLogin && correctPassword) return authOkCommand;
        else return authFailedCommand;
    }

/* In perspective */

//    public BaseCommand register(String login, String password, String email) {
//
//        RegisterOkCommnad registerOkCommnad = new RegisterOkCommnad();
//        RegisterFailedCommand registerFailedCommand = new RegisterFailedCommand();
//        registerFailedCommand.setMessage("");
//
//        if (true) return registerOkCommnad;
//        else return registerFailedCommand;
//    }
}
