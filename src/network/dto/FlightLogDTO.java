package network.dto;
import java.io.Serializable;

public class FlightLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int pilotId;
    private String pilotName;
    private int droneId;
    private String droneModelName;
    private String date;
    private long flightDurationInSeconds;
    private String comment;
    private double usedFrequency;
    private String location;

    public FlightLogDTO(int id, int pilotid, String pilotName, int droneId, String droneModelName, String date, long flightDurationInSeconds, String comment, double usedFrequency, String location) {
        this.id = id;
        pilotId = pilotid;
        this.pilotName = pilotName;
        this.droneId = droneId;
        this.droneModelName = droneModelName;
        this.date = date;
        this.flightDurationInSeconds = flightDurationInSeconds;
        this.comment = comment;
        this.usedFrequency = usedFrequency;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public int getPilotId() {
        return pilotId;
    }

    public String getPilotName() {
        return pilotName;
    }

    public int getDroneId() {
        return droneId;
    }

    public String getDroneModelName() {
        return droneModelName;
    }

    public String getDate() {
        return date;
    }

    public long getFlightDurationInSeconds() {
        return flightDurationInSeconds;
    }

    public String getComment() {
        return comment;
    }

    public double getUsedFrequency() {
        return usedFrequency;
    }

    public String getLocation() {
        return location;
    }

    public String getFormattedDuration() {
        long totalSeconds = this.flightDurationInSeconds;
        long days = totalSeconds / (24 * 3600);
        totalSeconds %= (24 * 3600);
        long hours = totalSeconds / 3600;
        totalSeconds %= 3600;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }
}
