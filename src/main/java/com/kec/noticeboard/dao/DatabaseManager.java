package com.kec.noticeboard.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/kec_noticeboard?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root"; // Update to your MySQL password if any

    private static boolean isMySqlAvailable = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                isMySqlAvailable = true;
                initializeSchema(conn);
                System.out.println(">>> Connected to MySQL Database successfully!");
            }
        } catch (Exception e) {
            System.out.println(">>> MySQL server offline. Operating in High-Speed Local In-Memory Java Mode.");
            isMySqlAvailable = false;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!isMySqlAvailable) {
            throw new SQLException("MySQL is not running locally");
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static boolean isMySqlConnected() {
        return isMySqlAvailable;
    }

    private static void initializeSchema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Create notices table if not exists
            String createNoticesSql = "CREATE TABLE IF NOT EXISTS notices (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "description LONGTEXT NOT NULL, " +
                    "category VARCHAR(50) NOT NULL, " +
                    "priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL', " +
                    "notice_date DATE NOT NULL, " +
                    "department VARCHAR(100) NOT NULL DEFAULT 'All Departments', " +
                    "is_published BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "attachment_url VARCHAR(500) NULL, " +
                    "attachment_file_name VARCHAR(255) NULL, " +
                    "attachment_file_type VARCHAR(100) NULL, " +
                    "posted_by VARCHAR(100) NOT NULL DEFAULT 'Principal Office', " +
                    "view_count INT NOT NULL DEFAULT 0, " +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.execute(createNoticesSql);

            // Create users table if not exists
            String createUsersSql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "full_name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'ROLE_ADMIN', " +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.execute(createUsersSql);
        } catch (SQLException e) {
            System.err.println("Schema initialization note: " + e.getMessage());
        }
    }
}
