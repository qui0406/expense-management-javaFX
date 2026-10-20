/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.pojo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author QUI
 */
public class JdbcUtils {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost/qlct";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "12345678";

    private static String dbUrl = System.getProperty("db.url", DEFAULT_URL);
    private static String dbUser = System.getProperty("db.user", DEFAULT_USER);
    private static String dbPassword = System.getProperty("db.password", DEFAULT_PASS);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            // Driver may also be loaded automatically via ServiceLoader
        }
    }
    
    public static void setTestDataSource(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
    }

    public static void resetDefaultDataSource() {
        dbUrl = DEFAULT_URL;
        dbUser = DEFAULT_USER;
        dbPassword = DEFAULT_PASS;
    }

    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
