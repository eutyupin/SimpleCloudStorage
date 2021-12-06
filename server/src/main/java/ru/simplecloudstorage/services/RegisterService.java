package ru.simplecloudstorage.services;

import ru.simplecloudstorage.commands.BaseCommand;
import ru.simplecloudstorage.commands.RegisterFailedCommand;
import ru.simplecloudstorage.commands.RegisterOkCommnad;
import ru.simplecloudstorage.util.DBCheckOrCreate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class RegisterService {
    private Statement registerStatement;

    public BaseCommand tryRegister(String login, int passwordHash, String email, String dbURL) throws SQLException {
        DBCheckOrCreate.tryCheckOrCreate(dbURL);
        RegisterOkCommnad registerOkCommnad = new RegisterOkCommnad();
        RegisterFailedCommand registerFailedCommand = new RegisterFailedCommand();
        try (Connection checkConnection = DriverManager.getConnection(dbURL)) {
            registerStatement = checkConnection.createStatement();
            registerStatement.executeUpdate(String.format("INSERT  INTO login (login_value) VALUES (\'%s\');", login));
            registerStatement.executeUpdate(String.format("INSERT  INTO password (login_value_id, password_value) VALUES (" +
                    "(SELECT id FROM login WHERE login_value = \'%s\'), %d);", login, passwordHash));
            registerStatement.executeUpdate(String.format("INSERT  INTO email (login_value_id, email) VALUES (" +
                    "(SELECT id FROM login WHERE login_value = \'%s\'), \'%s\');", login, email));

        } catch (SQLException e) {
            registerFailedCommand.setMessage(e.getMessage());
            System.out.println("User: " + login + " register failed.");
            return registerFailedCommand;
        }
        finally {
            registerStatement.close();
        }
        System.out.println("User: " + login + " register OK.");
        return registerOkCommnad;
    }
}
