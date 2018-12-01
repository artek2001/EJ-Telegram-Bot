package com.artek.Database;

import com.artek.BuildVars;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.sql.*;

public class ConnectionDB {
    private static final String LOGTAG = "CONNECTIONDB";
    private Connection currentConnction;

    public ConnectionDB(){
        this.currentConnction = openConnection();
    }

    private Connection openConnection() {
        Connection connection = null;
        try {
            Class.forName(BuildVars.driverClass).newInstance();
            connection = DriverManager.getConnection(BuildVars.dbUrl, BuildVars.userDB, BuildVars.passwordDB);

        }
        catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            BotLogger.error(LOGTAG, "openConnection method failed");
        }
        return connection;
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return this.currentConnction.prepareStatement(query);
    }

    public boolean executeQuery(String query) throws SQLException {
        final Statement statement = this.currentConnction.createStatement();
        return statement.execute(query);
    }

    public void closeConnection() {
        try {
            this.currentConnction.close();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, "closeConnetion failed");
        }
    }
}
