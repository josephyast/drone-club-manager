package model;

import java.util.Objects;

public class FlightLog {

    private int id;
    private int pilotId;
    private int droneId;
    private String date;
    private int durationMinutes;
    private String comment;
    private int usedFrequency;
    private String location;

    public FlightLog(int id, int pilotId, int droneId, String date, int durationMinutes, String comment, int usedFrequency, String location) {
        this.id = id;
        this.pilotId = pilotId;
        this.droneId = droneId;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.comment = comment;
        this.usedFrequency = usedFrequency;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPilotId() {
        return pilotId;
    }

    public void setPilotId(int pilotId) {
        this.pilotId = pilotId;
    }

    public int getDroneId() {
        return droneId;
    }

    public void setDroneId(int droneId) {
        this.droneId = droneId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getUsedFrequency() {
        return usedFrequency;
    }
    public void setUsedFrequency(int usedFrequency) {
        this.usedFrequency = usedFrequency;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }



    @Override
    public String toString() {
        return "FlightLog{" + "id=" + id + ", pilotId=" + pilotId + ", droneId=" + droneId + ", date=" + date + ", durationMinutes=" + durationMinutes + ", comment=" + comment + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightLog flightLog = (FlightLog) o;
        return id == flightLog.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pilotId, droneId, date, durationMinutes, comment);
    }
}
