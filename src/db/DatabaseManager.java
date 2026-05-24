package db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private final DatabaseConnection dbConnection;

    public DatabaseManager(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void closeConnection() {
        try {
            Connection conn = dbConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection successfully closed.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}