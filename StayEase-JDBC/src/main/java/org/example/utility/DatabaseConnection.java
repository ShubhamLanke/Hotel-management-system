package org.example.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
//    private static final String URL = "jdbc:postgresql://172.16.1.195:5331/dbdemo";
//    private static final String USER = "dbuser";
//    private static final String PASSWORD = "yF2awnXt";
    private static Connection connection;

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
//            try {
//                connection = DriverManager.getConnection(URL, USER, PASSWORD);
//            } catch (SQLException e) {
//                System.out.println("Properties file source not found!");
//            }

            try {
                Properties properties = new Properties();
                FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
                properties.load(fileInputStream);
                String url = properties.getProperty("url");
                connection = DriverManager.getConnection(url,properties);
            } catch (SQLException | IOException ex) {
                System.out.println("Properties file not found");
            }
        }
        return connection;
    }
}