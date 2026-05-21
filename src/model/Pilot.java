package model;

import java.time.Duration;
import java.util.Objects;
public class Pilot {

    private int id;
    private String name;
    private ExperienceLevel experienceLevel;
    private Duration totalFlightHours;
    private double assignedFrequency;
    private boolean isActive;

    public Pilot(int id, String name, ExperienceLevel experienceLevel, Duration totalFlightHours, double assignedFrequency, boolean isActive) {
        this.id = id;
        this.name = name;
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
        return "Pilot{" + "id=" + id + ", name='" + name + '\'' + ", experienceLevel='" + experienceLevel + '\'' + ", totalFlightHours=" + totalFlightHours + "assignedFrequency=" +assignedFrequency+ "isActive=" +isActive+ '}';
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
