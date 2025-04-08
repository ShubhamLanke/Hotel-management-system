package org.example.utility;

import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Log4j2
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
                    FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties");
                    properties.load(fileInputStream);
                    String url = properties.getProperty("url");

                    connection = DriverManager.getConnection(url, properties);
                    log.info("Database connection established successfully.");
                    return connection;

                } catch (SQLException | IOException ex) {
                    attempts++;
                    log.error("Database connection failed (Attempt {} of " + MAX_RETRIES + "): {}", attempts, ex.getMessage());

                    if (attempts >= MAX_RETRIES) {
                        log.error("Max retry attempts reached. Could not establish database connection.");
                        break;
                    }

                    try {
                        log.error("Retrying in " + (RETRY_DELAY_MS / 1000) + " seconds...");
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry interrupted: {}", String.valueOf(ie));
                        break;
                    }
                }
            }
        }
        return connection;
    }
}
