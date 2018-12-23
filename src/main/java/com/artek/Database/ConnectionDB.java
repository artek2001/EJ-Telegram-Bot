package com.artek.Database;

import org.telegram.telegrambots.meta.logging.BotLogger;

import java.sql.*;

public class ConnectionDB {
    private static final String LOGTAG = "CONNECTIONDB";
    private static volatile Connection currentConnction;

    public ConnectionDB() {
        try {
            currentConnction = DataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        this.currentConnction = openConnection();

//        try {
//            this.currentConnction = C3p0DataSource.getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        this.currentConnction = openConnection();
    }


    private Connection openConnection() {
        Connection connection = null;
        try {
            Class.forName(BuildVars.driverClass).newInstance();
            connection = DriverManager.getConnection(BuildVars.dbUrl, BuildVars.userDB, BuildVars.passwordDB);

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            BotLogger.error(LOGTAG, "openConnection method failed");
        }
        return connection;
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return currentConnction.prepareStatement(query);
    }

    public boolean executeQuery(String query) throws SQLException {
        final Statement statement = currentConnction.createStatement();
        return statement.execute(query);
    }

    public void closeConnection() {
        try {
            currentConnction.close();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, "closeConnetion failed");
        }
    }

    public Connection getCurrentConnction() {
        return currentConnction;
    }

    public Connection establichNewCurrentConnection() throws SQLException {
        if (currentConnction.isClosed()) {
            currentConnction = DataSource.getConnection();
        }
        return currentConnction;
    }
}
