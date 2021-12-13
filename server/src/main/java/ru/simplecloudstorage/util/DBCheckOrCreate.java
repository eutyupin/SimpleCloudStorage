package ru.simplecloudstorage.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCheckOrCreate {

    private  static Statement checkStatement;
    private static final Logger logger = LogManager.getLogger(DBCheckOrCreate.class);

    public static void tryCheckOrCreate(String dbURL) throws SQLException {
        try (Connection checkConnection = DriverManager.getConnection(dbURL)) {
            checkStatement = checkConnection.createStatement();

            checkStatement.execute("CREATE TABLE IF NOT EXISTS login (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                    "login_value STRING UNIQUE NOT NULL);");

            checkStatement.execute("CREATE TABLE IF NOT EXISTS password (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                    "login_value_id INTEGER REFERENCES login (id) NOT NULL, password_value INTEGER NOT NULL);");

            checkStatement.execute("CREATE TABLE IF NOT EXISTS email (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                    "login_value_id INTEGER REFERENCES login (id) NOT NULL, email STRING NOT NULL);");

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        finally {
            checkStatement.close();
        }
    }
}
