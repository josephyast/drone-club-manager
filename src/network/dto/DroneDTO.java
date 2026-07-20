package network.dto;

import model.DroneStatus;

import java.io.Serializable;

public class DroneDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private int id;
    private String modelName;
    private String type;
    private double weight;
    private String status;
    private String buildDate;
    private String lastMaintenanceDate;
    private long totalFlightTimeInSeconds;
    private double currentFrequency;

    public DroneDTO(int id, String modelName, String type, double weight, String status, String buildDate, String lastMaintenanceDate, long totalFlightTimeInSeconds, double currentFrequency) {
        this.id = id;
        this.modelName = modelName;
        this.type = type;
        this.weight = weight;
        this.status = status;
        this.buildDate = buildDate;
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.totalFlightTimeInSeconds = totalFlightTimeInSeconds;
        this.currentFrequency = currentFrequency;
    }


    public int getId() {
        return id;
    }

    public String getModelName() {
        return modelName;
    }

    public String getType() {
        return type;
    }

    public double getWeight() {
        return weight;
    }

    public String getStatus() {
        return status;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public String getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public long getTotalFlightTimeInSeconds() {
        return totalFlightTimeInSeconds;
    }

    public double getCurrentFrequency() {
        return currentFrequency;
    }

    public String getFormattedFlightTime() {
        long totalSeconds = this.totalFlightTimeInSeconds;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        return String.format("%dh %dm", hours, minutes);
    }


}
