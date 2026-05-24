package db;

import model.Drone;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DroneDAO {
    
    private final DatabaseConnection dbConnection;
    
    public DroneDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    public void insertDrone(Drone drone) {
        String sql = "INSERT INTO drones (modelName, type, weight, isFunctional, buildDate, lastMaintenanceDate, totalFlightTime, currentFrequency) VALUES (?,?,?,?,?,?,?,?);";

        try(Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, drone.getModelName());
            pstmt.setString(2, drone.getType().name());
            pstmt.setDouble(3, drone.getWeight());
            pstmt.setInt(4, drone.isFunctional() ? 1 : 0);
            pstmt.setString(5, drone.getBuildDate().toString());
            pstmt.setString(6, drone.getLastMaintenanceDate().toString());
            pstmt.setString(7, drone.getTotalFlightTime().toString());
            pstmt.setDouble(8, drone.getCurrentFrequency());

            pstmt.executeUpdate();
            System.out.println("Drone successfully inserted: " + drone.getModelName());
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error inserting drone: " + e.getMessage());
        }
    }

    public void updateDrone(Drone drone) {
        String sql = "UPDATE drones SET modelName = ?, type = ?, weight = ?, isFunctional = ?, buildDate = ?, lastMaintenanceDate = ?, totalFlightTime = ?, currentFrequency = ? WHERE id = ?;";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, drone.getModelName());
            pstmt.setString(2, drone.getType().name());
            pstmt.setDouble(3, drone.getWeight());
            pstmt.setInt(4, drone.isFunctional() ? 1 : 0);
            pstmt.setString(5, drone.getBuildDate().toString());
            pstmt.setString(6, drone.getLastMaintenanceDate().toString());
            pstmt.setString(7, drone.getTotalFlightTime().toString());
            pstmt.setDouble(8, drone.getCurrentFrequency());
            pstmt.setInt(9, drone.getId());

            pstmt.executeUpdate();
            System.out.println("Drone successfully updated: " + drone.getModelName());

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating drone: " + e.getMessage());
        }
    }
    public void deleteDrone(int id) {
        String sql = "DELETE FROM drones WHERE id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Drone with ID " + id + " successfully deleted.");

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting drone: " + e.getMessage());
        }
    }

    public Drone getDroneById(int id) {
        String sql = "SELECT * FROM drones WHERE id = ?;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                Drone drone = new Drone(

                        rs.getInt("id"),
                        rs.getString("modelName"),
                        model.DroneType.valueOf(rs.getString("type")),
                        rs.getDouble("weight"),
                        rs.getInt("isFunctional") == 1,
                        java.time.LocalDate.parse(rs.getString("buildDate")),
                        java.time.LocalDate.parse(rs.getString("lastMaintenanceDate")),
                        java.time.Duration.parse(rs.getString("totalFlightTime")),
                        rs.getDouble("currentFrequency")
                );
                return drone;
            } else {
                System.out.println("No drone found with ID: " + id);
                return null;
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error retrieving drone: " + e.getMessage());
            return null;
        }
    }

    public java.util.List<Drone> getAllDrones() {
        java.util.List<Drone> drones = new java.util.ArrayList<>();
        String sql = "SELECT * FROM drones;";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Drone drone = new Drone(
                        rs.getInt("id"),
                        rs.getString("modelName"),
                        model.DroneType.valueOf(rs.getString("type")),
                        rs.getDouble("weight"),
                        rs.getInt("isFunctional") == 1,
                        java.time.LocalDate.parse(rs.getString("buildDate")),
                        java.time.LocalDate.parse(rs.getString("lastmaintenanceDate")),
                        java.time.Duration.parse(rs.getString("totalFlightTime")),
                        rs.getDouble("currentFrequency")
                );
                drones.add(drone);
            }
            return drones;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error retrieving drones: " + e.getMessage());
            return drones;
        }
    }
    
}
