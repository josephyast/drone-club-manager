package db;


import model.Pilot;
import model.Drone;
import model.FlightLog;
import model.Part;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager {

    Connection conn = DriverManager.getConnection( "jdbc:sqlite:data.db" ) ;
    public void connect() {

    }

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

    public void printMainenanceReport() {
    }

}
