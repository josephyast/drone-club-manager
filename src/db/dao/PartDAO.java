package db.dao;

import db.DatabaseConnection;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class PartDAO {
    
    private final DroneDAO droneDAO;

    public PartDAO() {
        this.droneDAO = new DroneDAO();
    }

    public void insertPart(Part part) {
        String sql = "INSERT INTO parts (name, brand, type, drone_id, operating_hours, is_working) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, part.getName());
            pstmt.setString(2, part.getBrand());
            pstmt.setString(3, part.getType().name());
            pstmt.setInt(4, part.getDrone().getId());
            pstmt.setString(5, part.getOperatingHours().toString());
            pstmt.setInt(6, part.isWorking() ? 1 : 0);

            pstmt.executeUpdate();
            System.out.println("Part successfully inserted: " + part.getName());

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    part.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException  e) {
            System.err.println("Error inserting part: " + e.getMessage());

        }
    }

    public void updatePart(Part part) {
        String sql = "UPDATE parts SET name = ?, brand = ?, type = ?, drone_id = ?, operating_hours = ?, is_working = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, part.getName());
            pstmt.setString(2, part.getBrand());
            pstmt.setString(3, part.getType().name());
            pstmt.setInt(4, part.getDrone().getId());
            pstmt.setString(5, part.getOperatingHours().toString());
            pstmt.setInt(6, part.isWorking() ? 1 : 0);
            pstmt.setInt(7, part.getId());

            pstmt.executeUpdate();
            System.out.println("Part successfully updated: " + part.getName());

        } catch (SQLException  e) {
            System.err.println("Error updating part: " + e.getMessage());
        }
    }

    public void deletePart(int id) {
        String sql = "DELETE FROM parts WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Part with ID " + id + " successfully deleted.");

        } catch (SQLException  e) {
            System.err.println("Error deleting Part: " + e.getMessage());
        }
    }

    public Part getPartById(int id) {
        String sql = "SELECT * FROM parts WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                int droneId = rs.getInt("drone_id");

                model.Drone drone = new model.Drone(droneId, "Unknown", DroneType.TOOTHPICKS, 0.0, false, LocalDate.now(), LocalDate.now(), Duration.ZERO, 0.0);
                Part part = new Part(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        PartType.valueOf(rs.getString("type")),
                        drone,
                        Duration.parse(rs.getString("operating_hours")),
                        rs.getInt("is_working") == 1

                );
                return part;
            } else {
                System.out.println("No part found with ID: " + id);
                return null;
            }

        } catch (SQLException  e) {
            System.err.println("Error retrieving part: " + e.getMessage());
            return null;
        }
    }

    public java.util.List<Part> getAllParts() {
        java.util.List<Part> parts = new java.util.ArrayList<>();
        String sql = "SELECT * FROM parts;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {

                int droneId = rs.getInt("drone_id");

                model.Drone drone = new model.Drone(droneId, "Unknown", DroneType.TOOTHPICKS, 0.0, false, LocalDate.now(), LocalDate.now(), Duration.ZERO, 0.0);
                Part part = new Part(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        PartType.valueOf(rs.getString("type")),
                        drone,
                        Duration.parse(rs.getString("operating_hours")),
                        rs.getInt("is_working") == 1
                );
                parts.add(part);
            }
            return parts;

        } catch (SQLException  e) {
            System.err.println("Error retrieving parts: " + e.getMessage());
            return parts;
        }
    }

    public Part getFullyLoadedPart(int id) {
        Part part = getPartById(id);

        if (part != null) {
            int realDroneId = part.getDrone().getId();
            Drone realDrone = this.droneDAO.getDroneById(realDroneId);
            part.setDrone(realDrone);
        }

        return part;
    }

    public List<Part> getAllFullyLoadedParts() {
        List<Part> rawParts = getAllParts();

        for (Part part : rawParts) {
            Drone realDrone = this.droneDAO.getDroneById(part.getDrone().getId());
            if(realDrone != null) {
                part.setDrone(realDrone);
            }
        }

        return rawParts;
    }
}
