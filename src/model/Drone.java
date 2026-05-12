package model;

import java.util.Objects;

public class Drone {

    private int id;
    private String modelName;
    private String type;
    private double weight;
    private boolean isFunctional;
    private String buildDate;
    private String lastMaintenanceDate;
    private long totalFlightTime;
    private int currentFrequency;

    public Drone(int id, String modelName, String type, double weight, boolean isFunctional, String buildDate, String lastMaintenanceDate, long totalFlightTime, int currentFrequency) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public String getBuildDate() {
        return buildDate;
    }
    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }
    public String getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }
    public void setLastMaintenanceDate(String lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }
    public long getTotalFlightTime() {
        return totalFlightTime;
    }
    public void setTotalFlightTime(long totalFlightTime) {
        this.totalFlightTime = totalFlightTime;
    }
    public int getCurrentFrequency() {
        return currentFrequency;
    }
    public void setCurrentFrequency(int currentFrequency) {
        this.currentFrequency = currentFrequency;
    }

    @Override
    public String toString() {
        return "Drone{" + "id=" + id + ", modelName='" + modelName + '\'' + ", type='" + type + '\'' + ", weight=" + weight + ", isFunctional=" + isFunctional + '\'' + ", buildDate=" + buildDate + ", lastMaintenanceDate=" + lastMaintenanceDate + ", totalFlightTime=" + totalFlightTime +   ", currentFrequency=" + currentFrequency + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drone drone = (Drone) o;
        return id == drone.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
