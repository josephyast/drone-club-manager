package model;

import java.util.Objects;

public class Part {

    private int id;
    private String name;
    private String brand;
    private String type;
    private int droneId;
    private double operatingHours;
    private boolean isWorking;


    public Part(int id, String name, String brand, String type, int droneId , double operatingHours, boolean isWorking) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.droneId = droneId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDroneId() {
        return droneId;
    }

    public void setDroneId(int droneId) {
        this.droneId = droneId;
    }

    public double getOperatingHours() {
        return operatingHours;
    }
    public void setOperatingHours(double operatingHours) {
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
        return "Part{" + "id=" + id + ", name=" + name + ", brand=" + brand + ", type=" + type + ", droneId=" + droneId + "operatingHours=" +operatingHours+ "isWorking" + isWorking +'}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return id == part.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, brand, type, droneId, operatingHours, isWorking);
    }
}
