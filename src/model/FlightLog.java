package model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public class FlightLog {

    private int id;
    private Pilot pilot;
    private Drone drone;
    private LocalDate date;
    private Duration flightDuration;
    private String comment;
    private double usedFrequency;
    private String location;

    public FlightLog(int id, Pilot pilot, Drone drone, LocalDate date, Duration flightDuration, String comment, double usedFrequency, String location) {
        this.id = id;
        this.pilot = pilot;
        this.drone = drone;
        this.date = date;
        this.flightDuration = flightDuration;
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

    public Pilot getPilot() {
        return pilot;
    }

    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
    }

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Duration getDurationMinutes() {
        return flightDuration;
    }

    public void setDurationMinutes(Duration flightDuration) {
        this.flightDuration = flightDuration;
    }

    public double getUsedFrequency() {
        return usedFrequency;
    }
    public void setUsedFrequency(double usedFrequency) {
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
        return "FlightLog{" + "id=" + id + ", pilot=" + pilot + ", drone=" + drone + ", date=" + date + ", flightDuration=" + flightDuration + ", comment=" + comment +", usedFrequency=" + usedFrequency + ", location='" + location + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightLog flightLog)) return false;
        return id == flightLog.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
