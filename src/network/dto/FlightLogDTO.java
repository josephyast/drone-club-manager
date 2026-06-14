package network.dto;
import java.io.Serializable;

public class FlightLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int PilotId;
    private String pilotName;
    private int droneId;
    private String droneModelName;
    private String date;
    private long flightDurationInSeconds;
    private String comment;
    private double usedFrequency;
    private String location;

    public FlightLogDTO(int id, int pilotId, String pilotName, int droneId, String droneModelName, String date, long flightDurationInSeconds, String comment, double usedFrequency, String location) {
        this.id = id;
        PilotId = pilotId;
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
        return PilotId;
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
}
