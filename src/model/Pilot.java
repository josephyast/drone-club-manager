package model;

import java.util.Objects;
public class Pilot {

    private int id;
    private String name;
    private String experienceLevel;
    private int totalFlightHours;

    public Pilot(int id, String name, String experienceLevel, int totalFlightHours) {
        this.id = id;
        this.name = name;
        this.experienceLevel = experienceLevel;
        this.totalFlightHours = totalFlightHours;
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

    @Override
    public String toString() {
        return "Pilot{" + "id=" + id + ", name='" + name + '\'' + ", experienceLevel='" + experienceLevel + '\'' + ", totalFlightHours=" + totalFlightHours + '}';
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
        return Objects.hash(id);
    }
}
