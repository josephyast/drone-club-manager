package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    public void createTableIfNotExists() {
        String createPilots = "CREATE TABLE IF NOT EXISTS pilots (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "experience_level TEXT, " +
                "total_flight_hours TEXT, " +
                "assigned_frequency REAL, " +
                "is_active INTEGER);";

        String createDrones = "CREATE TABLE IF NOT EXISTS drones (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "model_name TEXT NOT NULL, " +
                "type TEXT, " +
                "weight REAL, " +
                "is_functional INTEGER, " +
                "build_date TEXT, " +
                "last_maintenance_date TEXT, " +
                "total_flight_time TEXT, " +
                "current_frequency REAL);";

        String createFlightLogs = "CREATE TABLE IF NOT EXISTS flight_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pilot_id INTEGER, " +
                "drone_id INTEGER, " +
                "date TEXT, " +
                "flight_duration TEXT, " +
                "comment TEXT, " +
                "used_frequency REAL, " +
                "location TEXT, " +
                "FOREIGN KEY(pilot_id) REFERENCES pilots(id), " +
                "FOREIGN KEY(drone_id) REFERENCES drones(id));";

        String createParts = "CREATE TABLE IF NOT EXISTS parts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "brand TEXT, " +
                "type TEXT, " +
                "drone_id INTEGER, " +
                "operating_hours TEXT, " +
                "is_working INTEGER, " +
                "FOREIGN KEY(drone_id) REFERENCES drones(id));";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createPilots);
            stmt.execute(createDrones);
            stmt.execute(createFlightLogs);
            stmt.execute(createParts);

            System.out.println("Tables created successfully.");

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
}