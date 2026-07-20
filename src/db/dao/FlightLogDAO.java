package db.dao;

import db.DatabaseConnection;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class FlightLogDAO { 
    private final PilotDAO pilotDAO;
    private final DroneDAO droneDAO;

    public FlightLogDAO() {
       
        this.pilotDAO = new PilotDAO();
        this.droneDAO = new DroneDAO();
    }

    public void insertFlightLog(FlightLog flightLog) {
        String sql = "INSERT INTO flight_logs (pilot_id, drone_id, date, flight_duration, comment, used_frequency, location) VALUES (?, ?, ?, ?, ?, ?, ?);";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightLog.getPilot().getId());
            pstmt.setInt(2, flightLog.getDrone().getId());
            pstmt.setString(3, flightLog.getDate().toString());
            pstmt.setString(4, flightLog.getFlightDuration().toString());
            pstmt.setString(5, flightLog.getComment());
            pstmt.setDouble(6, flightLog.getUsedFrequency());
            pstmt.setString(7, flightLog.getLocation());

            pstmt.executeUpdate();
            System.out.println("FlightLog successfully inserted: " + flightLog.getDate());
        } catch (SQLException  e) {
            System.err.println("Error inserting flightLog: " + e.getMessage());
        }
    }

    public void updateFlightLog(FlightLog flightLog) {
        String sql = "UPDATE flight_logs SET pilot_id = ?, drone_id = ?, date = ?, flight_duration = ?, comment = ?, used_frequency = ?, location = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setInt(1, flightLog.getPilot().getId());
            pstmt.setInt(2, flightLog.getDrone().getId());
            pstmt.setString(3, flightLog.getDate().toString());
            pstmt.setString(4, flightLog.getFlightDuration().toString());
            pstmt.setString(5, flightLog.getComment());
            pstmt.setDouble(6, flightLog.getUsedFrequency());
            pstmt.setString(7, flightLog.getLocation());
            pstmt.setInt(8, flightLog.getId());

            pstmt.executeUpdate();
            System.out.println("FlightLog successfully updated: " + flightLog.getDate());

        } catch (SQLException  e) {
            System.err.println("Error updating flightLog: " + e.getMessage());
        }
    }
    public void deleteFlightLog(int id) {
        String sql = "DELETE FROM flight_logs WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("FlightLog with ID " + id + " successfully deleted.");

        } catch (SQLException  e) {
            System.err.println("Error deleting flightLog: " + e.getMessage());
        }
    }

    public FlightLog getFlightLogById(int id) {
        String sql = "SELECT * FROM flight_logs WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {

                int pilotId = rs.getInt("pilot_id");
                int droneId = rs.getInt("drone_id");

                model.Pilot pilot = new model.Pilot(pilotId, "Unknown","Unknown","Unknown", ExperienceLevel.BEGINNER, Duration.ZERO, 0.0, false);
                model.Drone drone = new model.Drone(droneId, "Unknown", DroneType.TOOTHPICKS, 0.0, DroneStatus.AVAILABLE, LocalDate.now(), LocalDate.now(), Duration.ZERO, 0.0);

                FlightLog flightLog = new FlightLog(
                        rs.getInt("id"),
                        pilot,
                        drone,
                        java.time.LocalDate.parse(rs.getString("date")),
                        java.time.Duration.parse(rs.getString("flight_duration")),
                        rs.getString("comment"),
                        rs.getDouble("used_frequency"),
                        rs.getString("location")
                );
                return flightLog;
            } else {
                System.out.println("No flightLog found with ID: " + id);
                return null;
            }

        } catch (SQLException  e) {
            System.err.println("Error retrieving flightLog: " + e.getMessage());
            return null;
        }
    }

    public java.util.List<FlightLog> getAllFlightLogs() {
        java.util.List<FlightLog> flightLogs = new java.util.ArrayList<>();
        String sql = "SELECT * FROM flight_logs;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {

                int pilotId = rs.getInt("pilot_id");
                int droneId = rs.getInt("drone_id");

                model.Pilot pilot = new model.Pilot(pilotId, "Unknown", "Unknown","Unknown",ExperienceLevel.BEGINNER, Duration.ZERO, 0.0, false);
                model.Drone drone = new model.Drone(droneId, "Unknown", DroneType.TOOTHPICKS, 0.0, DroneStatus.AVAILABLE, LocalDate.now(), LocalDate.now(), Duration.ZERO, 0.0);

                FlightLog flightLog = new FlightLog(
                        rs.getInt("id"),
                        pilot,
                        drone,
                        LocalDate.parse(rs.getString("date")),
                        Duration.parse(rs.getString("flight_duration")),
                        rs.getString("comment"),
                        rs.getDouble("used_frequency"),
                        rs.getString("location")
                );
                flightLogs.add(flightLog);
            }
            return flightLogs;

        } catch (SQLException  e) {
            System.err.println("Error retrieving flightLogs: " + e.getMessage());
            return flightLogs;
        }
    }

    public FlightLog getFullyLoadedFlightLog(int id) {
        FlightLog flightLog = getFlightLogById(id);

        if (flightLog != null) {

            int realPilotId = flightLog.getPilot().getId();
            int realDroneId = flightLog.getDrone().getId();

            Pilot realPilot = this.pilotDAO.getPilotById(realPilotId);
            Drone realDrone = this.droneDAO.getDroneById(realDroneId);

            if (realPilot != null) {
                flightLog.setPilot(realPilot);
            }
            if (realDrone != null) {
                flightLog.setDrone(realDrone);
            }
        }
        return flightLog;
    }

    public List<FlightLog> getAllFullyLoadedFlightLogs() {
        List<FlightLog> flightLogs = getAllFlightLogs();

        for (FlightLog flightLog : flightLogs) {
            int realPilotId = flightLog.getPilot().getId();
            int realDroneId = flightLog.getDrone().getId();

            Pilot realPilot = this.pilotDAO.getPilotById(realPilotId);
            Drone realDrone = this.droneDAO.getDroneById(realDroneId);

            if (realPilot != null) {
                flightLog.setPilot(realPilot);
            }
            if (realDrone != null) {
                flightLog.setDrone(realDrone);
            }
        }
        return flightLogs;
    }
}
