package com.artek.Database;

import org.telegram.telegrambots.meta.logging.BotLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {
    private static final String LOGTAG = "DATABASEMANAGER";
    private static DBManager instance;
    private static ConnectionDB connection;

    private  DBManager() {
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

    public int getUserIdByLogin(String login) {
        int userId = 0;

        try {
            final PreparedStatement preparedStatement = connection.getPreparedStatement("SELECT id FROM users WHERE login=?");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
            return userId;
        }

        catch (SQLException e) {
            BotLogger.error(LOGTAG, "getUserIdByLogin method error");
        }
        return userId;

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
            BotLogger.error("SQL_EXCEPTION", "Error in userSetActive method");
        }
        return status == 1;
    }

}
