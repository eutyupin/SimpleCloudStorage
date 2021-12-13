package ru.simplecloudstorage.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.simplecloudstorage.commands.AuthFailedCommand;
import ru.simplecloudstorage.commands.AuthOkCommand;
import ru.simplecloudstorage.commands.BaseCommand;
import ru.simplecloudstorage.util.ClientCheckOrCreate;
import ru.simplecloudstorage.util.DBCheckOrCreate;

import java.nio.file.Paths;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthorizeService {

    private  Statement statement;
    private ResultSet resultSet;
    private String programRootPath = Paths.get("./").toUri().normalize().toString().substring(6);
    private ExecutorService dbWorkThreadPool = Executors.newSingleThreadExecutor();
    private static final Logger logger = LogManager.getLogger(AuthorizeService.class);

    public BaseCommand tryAuthorize(String login, int passwordHash, String dbURL) throws SQLException {
        dbCheckOrCreate(dbURL);
        return authCheck(login, passwordHash, dbURL);
    }

    private void dbCheckOrCreate(String dbURL) {
        dbWorkThreadPool.execute(() -> {
            try {
                DBCheckOrCreate.tryCheckOrCreate(dbURL);
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });
        dbWorkThreadPool.shutdownNow();
    }

    private BaseCommand authCheck(String login, int passwordHash, String dbURL) throws SQLException {
        AuthOkCommand authOkCommand = new AuthOkCommand();
        AuthFailedCommand authFailedCommand = new AuthFailedCommand();
        authFailedCommand.setMessage("Неверный логин или пароль. Попробуйте еще раз или зарегистрируйтесь.");
        boolean correctLogin = false;
        boolean correctPassword = false;
        try (Connection connection = DriverManager.getConnection(dbURL)) {
            statement = connection.createStatement();

            String queryString = String.format("SELECT login_value, password_value " +
                    "FROM login JOIN password on login.login_value = \'%s\' " +
                    "AND password.password_value = %d " +
                    "AND login.id = password.login_value_id;", login, passwordHash);
            resultSet = statement.executeQuery(queryString);
            if (resultSet.next()) {
                correctLogin = login.equals(resultSet.getString(1));
                correctPassword = passwordHash == resultSet.getInt(2);
            } else {
                correctLogin = false;
                correctPassword = false;
            }
        } catch (SQLException e) {
           correctLogin = false;
           correctPassword = false;
           logger.error(e.getMessage());
        }
        finally {
            statement.close();
            if (resultSet != null) resultSet.close();
        }
        if (correctLogin && correctPassword) {
            ClientCheckOrCreate.tryCheckClient(login, programRootPath);
            return authOkCommand;
        }
        else return authFailedCommand;
    }
}
