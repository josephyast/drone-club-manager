package db;

import model.Admin;
import model.Customer;
import model.Order;
import model.Product;

import model.Pilot;
import model.Drone;
import model.FlightLog;
import model.Part;

import java.util.List;

public class DatabaseManager {
    public void connect() {

    }

    // FPV Projekt

    public void savePilot(Pilot pilot) {

    }

    public Pilot getPilotById(int id) {
        return null;
    }

    public void saveDrone(Drone drone) {

    }

    public Drone getDroneById(int id) {
        return null;
    }
    public List<Drone> getAllDrones(){
        return null;
    }

    public void addFlightLog(FlightLog flightLog) {

    }

    public void savePart(Part part) {

    }

    public Part getPartById(int id) {
        return null;
    }

    public List<Part> getAllParts(){
        return null;
    }


    // ECommerce Projekt

    public void saveAdmin(Admin admin) {

    }

    public void saveCustomer(Customer customer) {
    }

    public Customer getCustomerById(int id) {
        return null;
    }

    public void saveProduct(Product product) {

    }

    public Product getProductById(int id) {
        return null;
    }

    public List<Product> getAllProducts() {
        return null;
    }

    public void updateStock(int productId, int newStock) {

    }

    public void  saveOrder(Order order) {
    }

    public Order getOrderById(int id) {
        return null;
    }

    public List<Order> getAllOrders() {
        return null;
    }

}
