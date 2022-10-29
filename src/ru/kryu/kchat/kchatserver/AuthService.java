package ru.kryu.kchat.kchatserver;

import java.sql.*;

public class AuthService {
    private Connection connection;
    private PreparedStatement psGetNick;

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:mainServer.db");
        psGetNick = connection.prepareStatement("SELECT nick FROM users WHERE login = ? AND pass = ?;");
    }

    public String getNickByLoginAndPass(String login, String pass) {
        try {
            psGetNick.setString(1, login);
            psGetNick.setString(2, pass);
            ResultSet rs = psGetNick.executeQuery();
            if (rs.next()) {
                return rs.getString("nick");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect() throws SQLException {
        psGetNick.close();
        connection.close();
    }

}
