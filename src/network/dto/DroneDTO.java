package network.dto;

import java.io.Serializable;

public class DroneDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private int id;
    private String modelName;
    private String type;
    private double weight;
    private boolean isFunctional;
    private String buildDate;
    private String lastMaintenanceDate;
    private long totalFlightTimeInSeconds;
    private double currentFrequency;

    public DroneDTO(int id, String modelName, String type, double weight, boolean isFunctional, String buildDate, String lastMaintenanceDate, long totalFlightTimeInSeconds, double currentFrequency) {
        this.id = id;
        this.modelName = modelName;
        this.type = type;
        this.weight = weight;
        this.isFunctional = isFunctional;
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

    public boolean isFunctional() {
        return isFunctional;
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
}
