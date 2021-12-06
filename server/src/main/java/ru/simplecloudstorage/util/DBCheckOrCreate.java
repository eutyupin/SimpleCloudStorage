package ru.simplecloudstorage.util;

import java.sql.*;

public class DBCheckOrCreate {

    private  static Statement checkStatement;

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
            System.out.println(e.getMessage()); // at next time will logger in this place
        }
        finally {
            checkStatement.close();
        }
    }
}
