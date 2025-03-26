package org.example.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {

                Properties properties = new Properties();
                FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties");
                properties.load(fileInputStream);
                String url = properties.getProperty("url");
                connection = DriverManager.getConnection(url,properties);
            } catch (SQLException | IOException ex) {
                System.out.println("Properties file not found"+ ex.getLocalizedMessage());
            }
        }
        return connection;
    }
}