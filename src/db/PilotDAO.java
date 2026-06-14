package db;

import model.Pilot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PilotDAO {



    public void insertPilot(Pilot pilot) {
        String sql = "INSERT INTO pilots (name,username,password_hash, experience_level, total_flight_hours, assigned_frequency, is_active) VALUES (?,?,?,?,?,?,?);";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pilot.getName());
            pstmt.setString(2, pilot.getUsername());
            pstmt.setString(3, pilot.getPasswordHash());
            pstmt.setString(4, pilot.getExperienceLevel().name());
            pstmt.setString(5, pilot.getTotalFlightHours().toString());
            pstmt.setDouble(6, pilot.getAssignedFrequency());
            pstmt.setInt(7, pilot.isActive() ? 1 : 0);

            pstmt.executeUpdate();

            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pilot.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Pilot successfully inserted " + pilot.getName());
        } catch (SQLException e) {
            System.err.println("Error inserting pilot: " + e.getMessage());
        }
    }

    public void updatePilot(Pilot pilot) {
        String sql = "UPDATE pilots SET name = ?,username = ?, password_hash = ?, experience_level = ?, total_flight_hours = ?, assigned_frequency = ?, is_active = ? WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pilot.getName());
            pstmt.setString(2, pilot.getUsername());
            pstmt.setString(3, pilot.getPasswordHash());
            pstmt.setString(4, pilot.getExperienceLevel().name());
            pstmt.setString(5, pilot.getTotalFlightHours().toString());
            pstmt.setDouble(6, pilot.getAssignedFrequency());
            pstmt.setInt(7, pilot.isActive() ? 1 : 0);
            pstmt.setInt(8, pilot.getId());

            pstmt.executeUpdate();
            System.out.println("Pilot successfully updated: " + pilot.getName());

        } catch (SQLException e) {
            System.err.println("Error updating pilot: " + e.getMessage());
        }
    }

    public void deletePilot(int id) {
        String sql = "DELETE FROM pilots WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Pilot with ID " + id + " successfully deleted.");

        } catch (SQLException  e) {
            System.err.println("Error deleting pilot: " + e.getMessage());
        }
    }

    public Pilot getPilotById(int id) {
        String sql = "SELECT * FROM pilots WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                Pilot pilot = new Pilot(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        model.ExperienceLevel.valueOf(rs.getString("experience_level")),
                        java.time.Duration.parse(rs.getString("total_flight_hours")),
                        rs.getDouble("assigned_frequency"),
                        rs.getInt("is_active") == 1,
                        true
                );
                return pilot;
            } else {
                System.out.println("No pilot found with ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving pilot: " + e.getMessage());
            return null;
        }
    }

    public java.util.List<Pilot> getAllPilots() {
        java.util.List<Pilot> pilots = new java.util.ArrayList<>();
        String sql = "SELECT * FROM pilots;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                Pilot pilot = new Pilot(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        model.ExperienceLevel.valueOf(rs.getString("experience_level")),
                        java.time.Duration.parse(rs.getString("total_flight_hours")),
                        rs.getDouble("assigned_frequency"),
                        rs.getInt("is_active") == 1,
                        true
                );
                pilots.add(pilot);
            }
            return pilots;

        } catch (SQLException  e) {
            System.err.println("Error retrieving pilots: " + e.getMessage());
            return pilots;
        }
    }

    public void registerPilot(Pilot pilot, String password) {

        String hashedPassword = util.PasswordHasher.hashPassword(password);
        String sql = "INSERT INTO pilots (name, username, password_hash, experience_level, total_flight_hours, assigned_frequency, is_active) VALUES (?,?,?,?,?,?,?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, pilot.getName());
            pstmt.setString(2, pilot.getUsername());
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, pilot.getExperienceLevel().name());
            pstmt.setString(5, pilot.getTotalFlightHours().toString());
            pstmt.setDouble(6, pilot.getAssignedFrequency());
            pstmt.setInt(7, pilot.isActive() ? 1 : 0);

            pstmt.executeUpdate();
            System.out.println("Pilot successfully registered: " + pilot.getName());
        } catch (SQLException  e) {
            System.err.println("Error registering pilot: " + e.getMessage());
        }
    }

    public boolean isUsernameTaken(String username){
        String sql = "SELECT COUNT(*) FROM pilots WHERE username = ?;";
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            var rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException  e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    public Pilot loginPilot(String username, String password) {
        String sql = "SELECT * FROM pilots WHERE username = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,username);
            var rs = pstmt.executeQuery();

            if (rs.next()){
                String storedHash = rs.getString("password_hash");

                if(util.PasswordHasher.checkPassword(password, storedHash)){
                    System.out.println("Login successful: " + username);
                    return new Pilot(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            model.ExperienceLevel.valueOf(rs.getString("experience_level")),
                            java.time.Duration.parse(rs.getString("total_flight_hours")),
                            rs.getDouble("assigned_frequency"),
                            rs.getInt("is_active") == 1,
                            true
                    );
                }
            }
        } catch (SQLException  e) {
            System.err.println("Error during login: " + e.getMessage());
        }

        System.out.println("Login failed for username or password.");
        return null;
    }
}
