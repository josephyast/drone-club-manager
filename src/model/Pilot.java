package model;

import java.time.Duration;
import java.util.Objects;
public class Pilot {

    private int id;
    private String name;
    private String username;
    private String passwordHash;
    private ExperienceLevel experienceLevel;
    private Duration totalFlightHours;
    private double assignedFrequency;
    private boolean isActive;

    public Pilot(int id, String name, String username,String plainPassword, ExperienceLevel experienceLevel, Duration totalFlightHours, double assignedFrequency, boolean isActive) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.passwordHash = util.PasswordHasher.hashPassword(plainPassword);
        this.experienceLevel = experienceLevel;
        this.totalFlightHours = totalFlightHours;
        this.assignedFrequency = assignedFrequency;
        this.isActive = isActive;
    }

    public Pilot(int id, String name, String username, String passwordHash, ExperienceLevel experienceLevel, Duration totalFlightHours, double assignedFrequency, boolean isActive, boolean isAlreadyHashed) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.experienceLevel = experienceLevel;
        this.totalFlightHours = totalFlightHours;
        this.assignedFrequency = assignedFrequency;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Duration getTotalFlightHours() {
        return totalFlightHours;
    }

    public void setTotalFlightHours(Duration totalFlightHours) {
        this.totalFlightHours = totalFlightHours;
    }

    public double getAssignedFrequency() {
        return assignedFrequency;
    }
    public void setAssignedFrequency(double assignedFrequency) {
        this.assignedFrequency = assignedFrequency;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }


    @Override
    public String toString() {
        return "Pilot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='******'" +
                ", experienceLevel=" + experienceLevel +
                ", totalFlightHours=" + totalFlightHours +
                ", assignedFrequency=" + assignedFrequency +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pilot pilot)) return false;
        return id == pilot.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
