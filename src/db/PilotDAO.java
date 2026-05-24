package db;

import model.Pilot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PilotDAO {
    private final DatabaseConnection dbConnection;

    public PilotDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void insertPilot(Pilot pilot) {
        String sql = "INSERT INTO pilots (name, experience_level, total_flight_hours, assigned_frequency, is_active) VALUES (?,?,?,?,?,?);";

        try(Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pilot.getName());
            pstmt.setString(2, pilot.getExperienceLevel().name());
            pstmt.setString(3, pilot.getTotalFlightHours().toString());
            pstmt.setDouble(4, pilot.getAssignedFrequency());
            pstmt.setInt(5, pilot.isActive() ? 1 : 0);

            pstmt.executeUpdate();
            System.out.println("Pilot successfully inserted " + pilot.getName());
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error inserting pilot: " + e.getMessage());
        }
    }

    public void updatePilot(Pilot pilot) {
        String sql = "UPDATE pilots SET name = ?, experience_level = ?, total_flight_hours = ?, assigned_frequency = ?, is_active = ? WHERE id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pilot.getName());
            pstmt.setString(2, pilot.getExperienceLevel().name());
            pstmt.setString(3, pilot.getTotalFlightHours().toString());
            pstmt.setDouble(4, pilot.getAssignedFrequency());
            pstmt.setInt(5, pilot.isActive() ? 1 : 0);
            pstmt.setInt(6, pilot.getId());

            pstmt.executeUpdate();
            System.out.println("Pilot successfully updated: " + pilot.getName());

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating pilot: " + e.getMessage());
        }
    }

    public void deletePilot(int id) {
        String sql = "DELETE FROM pilots WHERE id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Pilot with ID " + id + " successfully deleted.");

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting pilot: " + e.getMessage());
        }
    }

    public Pilot getPilotById(int id) {
        String sql = "SELECT * FROM pilots WHERE id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                Pilot pilot = new Pilot(
                        rs.getInt("id"),
                        rs.getString("name"),
                        model.ExperienceLevel.valueOf(rs.getString("experience_level")),
                        java.time.Duration.parse(rs.getString("total_flight_hours")),
                        rs.getDouble("assigned_frequency"),
                        rs.getInt("is_active") == 1
                );
                return pilot;
            } else {
                System.out.println("No pilot found with ID: " + id);
                return null;
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error retrieving pilot: " + e.getMessage());
            return null;
        }
    }

    public java.util.List<Pilot> getAllPilots() {
        java.util.List<Pilot> pilots = new java.util.ArrayList<>();
        String sql = "SELECT * FROM pilots;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Pilot pilot = new Pilot(
                        rs.getInt("id"),
                        rs.getString("name"),
                        model.ExperienceLevel.valueOf(rs.getString("experience_level")),
                        java.time.Duration.parse(rs.getString("total_flight_hours")),
                        rs.getDouble("assigned_frequency"),
                        rs.getInt("is_active") == 1
                );
                pilots.add(pilot);
            }
            return pilots;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error retrieving pilots: " + e.getMessage());
            return pilots;
        }
    }
}
