package org.example.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;
    private static final int MAX_RETRIES = 12; // Maximum retry attempts
    private static final int RETRY_DELAY_MS = 5000; // 5 seconds delay

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            int attempts = 0;
            while (attempts < MAX_RETRIES) {
                try {
                    Properties properties = new Properties();
                    try(FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties")){
                        properties.load(fileInputStream);
                    }
                    String url = properties.getProperty("url");

                    connection = DriverManager.getConnection(url, properties);
                    System.out.println("Database connection established successfully.");
                    return connection;

                } catch (SQLException | IOException ex) {
                    attempts++;
                    System.out.println("Database connection failed (Attempt " + attempts + " of " + MAX_RETRIES + "): "
                            + ex.getMessage());

                    if (attempts >= MAX_RETRIES) {
                        System.out.println("Max retry attempts reached. Could not establish database connection.");
                        break;
                    }

                    try {
                        System.out.println("Retrying in " + (RETRY_DELAY_MS / 1000) + " seconds...");
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.out.println("Retry interrupted: " + ie.getMessage());
                        break;
                    }
                }
            }
        }
        return connection;
    }
}
