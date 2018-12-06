package com.artek.Database;

import org.telegram.telegrambots.meta.logging.BotLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {
    private static final String LOGTAG = "DATABASEMANAGER";
    private static volatile DBManager instance;
    private static volatile ConnectionDB connection;


    public DBManager() {
        connection = new ConnectionDB();
    }


    public static DBManager getInstance() {
        final DBManager currentInstance;
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
                currentInstance = instance;
            }

        }
        else {
            currentInstance = instance;
        }
        return currentInstance;

    }

    public boolean setUserStateForBot(Integer userId, String login, String password, boolean isActive) {
        int updatedRows = 0;

        try {
            final PreparedStatement preparedStatement = connection.getPreparedStatement("INSERT INTO users (userId, login, password, isActive) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE isActive=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, password);
            preparedStatement.setInt(4, isActive ? 1 : 0);
            preparedStatement.setInt(5, isActive ? 1 : 0);

            updatedRows = preparedStatement.executeUpdate();
        }

        catch (SQLException e) {
            BotLogger.error("SQL_EXCEPTION", "Error in userSetUserState method");
        }

        return updatedRows > 0;
    }

    public String[] getUserCredentialsById(Integer userId) {
        String[] credentials = new String[2];
        try {
            final PreparedStatement preparedStatement = connection.getPreparedStatement("SELECT login, password FROM  users WHERE userId=?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                credentials[0] = resultSet.getString("login");
                credentials[1] = resultSet.getString("password");
            }
            return credentials;
        }

        catch (SQLException e) {
            BotLogger.error(LOGTAG, "getUserCredentialsById method error");
        }
        return credentials;

    }

    public boolean getUserStateForBot(Integer userId) {
        int status = -1;

        try {

            final PreparedStatement preparedStatement = connection.getPreparedStatement("SELECT isActive FROM users WHERE userId=?");
            preparedStatement.setInt(1, userId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                status = resultSet.getInt("isActive");
            }

        }

        catch (SQLException e) {
            BotLogger.error("SQL_EXCEPTION", "Error in userGetActive method");
        }
        return status == 1;
    }

    public boolean setUserStateForBot(Integer userId, Integer isActive) {
        int changedRows = 0;
        try {
            final PreparedStatement preparedStatement = connection.getPreparedStatement("UPDATE users SET isActive = ? WHERE userId = ?");
            preparedStatement.setInt(1, isActive);
            preparedStatement.setInt(2, userId);
            changedRows = preparedStatement.executeUpdate();
        }

        catch (SQLException e) {

            BotLogger.error("SQL_EXCEPTION", "error in setUserStateForBot(Integer userId, boolean isActive) for userId = " + userId);
        }
        return changedRows > 0;
    }

    public ConnectionDB getConnectionDB() {
        return connection;
    }
}
