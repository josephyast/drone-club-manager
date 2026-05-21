package model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public class Drone {

    private int id;
    private String modelName;
    private DroneType type;
    private double weight;
    private boolean isFunctional;
    private LocalDate buildDate;
    private LocalDate lastMaintenanceDate;
    private Duration totalFlightTime;
    private double currentFrequency;

    public Drone(int id, String modelName, DroneType type, double weight, boolean isFunctional, LocalDate buildDate, LocalDate lastMaintenanceDate, Duration totalFlightTime, double currentFrequency) {
        this.id = id;
        this.modelName = modelName;
        this.type = type;
        this.weight = weight;
        this.isFunctional = isFunctional;
        this.buildDate = buildDate;
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.totalFlightTime = totalFlightTime;
        this.currentFrequency = currentFrequency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public DroneType getType() {
        return type;
    }

    public void setType(DroneType type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isFunctional() {
        return isFunctional;
    }

    public void setFunctional(boolean functional) {
        isFunctional = functional;
    }

    public LocalDate getBuildDate() {
        return buildDate;
    }
        public void setBuildDate(LocalDate buildDate) {
        this.buildDate = buildDate;
    }
    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }
    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }
    public Duration getTotalFlightTime() {
        return totalFlightTime;
    }
    public void setTotalFlightTime(Duration totalFlightTime) {
        this.totalFlightTime = totalFlightTime;
    }
    public double getCurrentFrequency() {
        return currentFrequency;
    }
    public void setCurrentFrequency(double currentFrequency) {
        this.currentFrequency = currentFrequency;
    }

    @Override
    public String toString() {
        return "Drone{" + "id=" + id + ", modelName='" + modelName + '\'' + ", type='" + type + '\'' + ", weight=" + weight + ", isFunctional=" + isFunctional + '\'' + ", buildDate=" + buildDate + ", lastMaintenanceDate=" + lastMaintenanceDate + ", totalFlightTime=" + totalFlightTime +   ", currentFrequency=" + currentFrequency + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Drone drone)) return false;
        return id == drone.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
