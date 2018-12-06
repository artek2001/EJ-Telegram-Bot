package com.artek;


import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class C3p0DataSource {

    private static final String LOGTAG = "C3p0DATASOURCE";
    private static ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();


    public static Connection getConnection() throws SQLException {
        return comboPooledDataSource.getConnection();
    }

    public ComboPooledDataSource getComboPooledDataSource() {
        return comboPooledDataSource;
    }

    public C3p0DataSource() {}
}
