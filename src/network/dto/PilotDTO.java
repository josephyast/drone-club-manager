package network.dto;

import java.io.Serializable;

public class PilotDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String username;
    private String password;
    private String experienceLevel;
    private long totalFlightHoursInSeconds;
    private double assignedFrequency;
    private boolean isActive;

    public PilotDTO(int id, String name, String username, String password, String experienceLevel, long totalFlightHoursInSeconds, double assignedFrequency, boolean isActive) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.experienceLevel = experienceLevel;
        this.totalFlightHoursInSeconds = totalFlightHoursInSeconds;
        this.assignedFrequency = assignedFrequency;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public long getTotalFlightHoursInSeconds() {
        return totalFlightHoursInSeconds;
    }

    public double getAssignedFrequency() {
        return assignedFrequency;
    }

    public boolean isActive() {
        return isActive;
    }
}

