package model;

import java.time.Duration;
import java.util.Objects;

public class Part {

    private int id;
    private String name;
    private String brand;
    private PartType type;
    private Drone drone;
    private Duration operatingHours;
    private boolean isWorking;


    public Part(int id, String name, String brand, PartType type, Drone drone , Duration operatingHours, boolean isWorking) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.drone = drone;
        this.operatingHours = operatingHours;
        this.isWorking = isWorking;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public PartType getType() {
        return type;
    }

    public void setType(PartType type) {
        this.type = type;
    }

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public Duration getOperatingHours() {
        return operatingHours;
    }
    public void setOperatingHours(Duration operatingHours) {
        this.operatingHours = operatingHours;
    }
    public boolean isWorking() {
        return isWorking;
    }
    public void setWorking(boolean working) {
        isWorking = working;
    }

    @Override
    public String toString() {
        return "Part{" + "id=" + id + ", name=" + name + ", brand=" + brand + ", type=" + type + ", drone=" + drone + "operatingHours=" +operatingHours+ "isWorking" + isWorking +'}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Part part)) return false;
        return id == part.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
