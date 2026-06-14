package network.dto;
import java.io.Serializable;

public class PartDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String brand;
    private String type;
    private int droneId;
    private long operatingHoursInSeconds;
    private boolean isWorking;

    public PartDTO(int id, String name, String brand, String type, int droneId, long operatingHoursInSeconds, boolean isWorking) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.droneId = droneId;
        this.operatingHoursInSeconds = operatingHoursInSeconds;
        this.isWorking = isWorking;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getType() {
        return type;
    }

    public int getDroneId() {
        return droneId;
    }

    public long getOperatingHoursInSeconds() {
        return operatingHoursInSeconds;
    }

    public boolean isWorking() {
        return isWorking;
    }
}
