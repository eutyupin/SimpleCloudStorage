package ru.simplecloudstorage.server;

import ru.simplecloudstorage.commands.*;

import java.sql.*;

public class AuthorizeService {
    public BaseCommand tryAuthorize(String login, String password) {
        AuthOkCommand authOkCommand = new AuthOkCommand();
        AuthFailedCommand authFailedCommand = new AuthFailedCommand();
        authFailedCommand.setMessage("Wrong login or user name. Please check, or regisner.");
        boolean wrightLogin = false;
        boolean wrightPassword = false;
        try (Connection connection = DriverManager.getConnection("db/base.db")) {
            Statement statement = connection.createStatement();
            String queryString = String.format("SELECT username FROM logins WHERE user_login = \'%s\' AND user_password = \'%s\'", login, password);
            ResultSet resultSet = statement.executeQuery(queryString);
            wrightLogin = login.equals(resultSet.getString(1));
            wrightPassword = password.equals(resultSet.getString(2));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (wrightLogin && wrightPassword) return authOkCommand;
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
