package ru.geekbrains.core;

import ru.geekbrains.data.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AuthController {

    private Connection connection;
    private static PreparedStatement preparedStatement;
    private static Statement statement;

    public void init() {

        try {
            connect();
            initPrepareStatements();
            statement.executeUpdate("INSERT INTO users (login, password, nick) VALUES ('admin', 'admin', 'sysroot');");
            Savepoint savepoint = connection.setSavepoint();
            statement.executeUpdate("INSERT INTO users (login, password, nick) VALUES ('alex', '123', 'alex-st');");
            connection.rollback(savepoint);
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        connection = DriverManager.getConnection("jdbc:sqlite:chat.db");

        if (connection != null) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id    INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "login TEXT,"
                    + "password  TEXT,"
                    + "nick TEXT)");
        }
    }

    public String getNickname(String login, String password) {


        try ( Connection connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
             PreparedStatement ps = connection.prepareStatement("SELECT nick FROM users WHERE login = ? AND password = ?")) {

            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nick");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void initPrepareStatements() throws SQLException {
        preparedStatement = connection.prepareStatement("INSERT INTO users (login, password, nick) VALUES (?, ?, ?)");
    }

    public  void disconnect() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}


