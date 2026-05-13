package model;

import java.util.Objects;
public class Pilot {

    private int id;
    private String name;
    private String experienceLevel;
    private int totalFlightHours;
    private int assignedFrequency;
    private boolean isActive;

    public Pilot(int id, String name, String experienceLevel, int totalFlightHours, int assignedFrequency, boolean isActive) {
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

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public int getTotalFlightHours() {
        return totalFlightHours;
    }

    public void setTotalFlightHours(int totalFlightHours) {
        this.totalFlightHours = totalFlightHours;
    }

    public int getAssignedFrequency() {
        return assignedFrequency;
    }
    public void setAssignedFrequency(int assignedFrequency) {
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
        if (o == null || getClass() != o.getClass()) return false;
        Pilot pilot = (Pilot) o;
        return id == pilot.id;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, name, experienceLevel, totalFlightHours, assignedFrequency, isActive);
    }
}
