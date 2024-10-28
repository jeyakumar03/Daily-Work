package source.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/bus";
    private static final String USER = "postgres";
    private static final String PASSWORD = "zoho";
    private static Connection connection;
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to connect to the database.");
            }
        }
        return connection;
    }
    public static void closeConnection() {
        try{
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
