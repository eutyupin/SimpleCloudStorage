package ru.simplecloudstorage.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

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
            logger.error("User: " + login + " register failed. " + e.getMessage());
            return registerFailedCommand;
        }
        finally {
            registerStatement.close();
        }
        logger.info("User: " + login + " register OK.");
        return registerOkCommnad;
    }
}
