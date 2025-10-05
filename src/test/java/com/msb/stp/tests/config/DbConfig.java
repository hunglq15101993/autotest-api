package com.msb.stp.tests.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {
    private static final String DB_URL = "jdbc:postgresql://10.0.132.102:5445/monitor?currentSchema=stp-autotest";
    private static final String DB_USER = "monitor_db_dev";
    private static final String DB_PASSWORD = "monitor_db_dev";
    private static final String DB_SCHEMA = "stp_autotest";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        connection.setSchema(DB_SCHEMA);
        return connection;
    }
}