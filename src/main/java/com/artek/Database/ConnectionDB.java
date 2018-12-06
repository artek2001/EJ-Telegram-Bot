package com.artek.Database;

import com.artek.BuildVars;
import com.artek.C3p0DataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.jboss.C3P0PooledDataSource;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.beans.PropertyVetoException;
import java.sql.*;

public class ConnectionDB {
    private static final String LOGTAG = "CONNECTIONDB";
    private static Connection currentConnction;
    public C3p0DataSource dataSource;

    public ConnectionDB(){
        openConnectionWithC3p0();
//        this.currentConnction = openConnection();

//        try {
//            this.currentConnction = C3p0DataSource.getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        this.currentConnction = openConnection();
    }

    private void openConnectionWithC3p0() {
        dataSource = new C3p0DataSource();
        ComboPooledDataSource pooledDataSource = dataSource.getComboPooledDataSource();
        try {
            pooledDataSource.setDriverClass(BuildVars.driverClass);
            pooledDataSource.setJdbcUrl(BuildVars.dbUrl);
            pooledDataSource.setUser(BuildVars.userDB);
            pooledDataSource.setPassword(BuildVars.passwordDB);
//            pooledDataSource.setTestConnectionOnCheckin(false);
//            pooledDataSource.setMaxIdleTime(600);
            pooledDataSource.setIdleConnectionTestPeriod(3000);
            pooledDataSource.setMaxStatements(50);
        } catch (PropertyVetoException e) {
            BotLogger.error(LOGTAG, "Error in static method in DataSource class");
        }
        try {
            currentConnction = dataSource.getComboPooledDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

    public void establichNewCurrentConnection() throws SQLException {

        currentConnction = dataSource.getComboPooledDataSource().getConnection();
    }
}
