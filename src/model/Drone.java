package model;

import java.util.Objects;

public class Drone {

    private int id;
    private String modelName;
    private String type;
    private double weight;
    private boolean isFunctional;

    public Drone(int id, String modelName, String type, double weight, boolean isFunctional) {
        this.id = id;
        this.modelName = modelName;
        this.type = type;
        this.weight = weight;
        this.isFunctional = isFunctional;
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

    @Override
    public String toString() {
        return "Drone{" + "id=" + id + ", modelName='" + modelName + '\'' + ", type='" + type + '\'' + ", weight=" + weight + ", isFunctional=" + isFunctional + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drone drone = (Drone) o;
        return id == drone.id && Double.compare(drone.weight, weight) == 0 && isFunctional == drone.isFunctional && Objects.equals(modelName, drone.modelName) && Objects.equals(type, drone.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
