package main;

import db.*;
import model.*;

import java.time.Duration;
import java.time.LocalDate;


public class Main {
    public static void main(String[] args) {
        System.out.println("Drone Management System Initialization");

        DatabaseManager dbManager = new DatabaseManager();
        dbManager.createTableIfNotExists();

        PilotDAO pilotDAO = new PilotDAO();
        DroneDAO droneDAO = new DroneDAO();
        FlightLogDAO flightLogDAO = new FlightLogDAO();
        PartDAO partDAO = new PartDAO();

        try{
            System.out.println("Insert 5 Pilots");
            Pilot p1 = new Pilot(0,"Yusuf Yasti", "yusufyasti", "Yusuf123!",ExperienceLevel.EXPERT, Duration.ofHours(5), 120.0, true);
            Pilot p2 = new Pilot(0,"Ahmet Kerem Yasti", "ahmetkeremyasti", "Ahmet123!", ExperienceLevel.ADVANCED, Duration.ofHours(3), 139.5, true);
            Pilot p3 = new Pilot(0,"Fevziye Yasti", "fevziyeyasti", "Fevziye123!", ExperienceLevel.INTERMEDIATE, Duration.ofHours(8), 123.4, true);
            Pilot p4 = new Pilot(0,"Hakan Yasti", "hakanyasti", "Hakan123!", ExperienceLevel.BEGINNER, Duration.ofHours(2), 98.2, false);
            Pilot p5 = new Pilot(0,"Fatma Nur Yasti", "fatmanuryasti", "Fatma123!", ExperienceLevel.INTERMEDIATE, Duration.ofHours(7), 110.0, true);

            pilotDAO.insertPilot(p1);
            pilotDAO.insertPilot(p2);
            pilotDAO.insertPilot(p3);
            pilotDAO.insertPilot(p4);
            pilotDAO.insertPilot(p5);

            System.out.println("Insert 5 Drones");
            Drone d1 = new Drone(0, "DJI Mavic 3", DroneType.FIVEINCHRACING, 410.0, true, LocalDate.now().minusMonths(6), LocalDate.now(), Duration.ofHours(15), 2200.0);
            Drone d2 = new Drone(0, "BetaFPV Toothpick", DroneType.TOOTHPICKS, 75.0, true, LocalDate.now().minusMonths(2), LocalDate.now(), Duration.ofHours(4), 350.0);
            Drone d3 = new Drone(0, "iFlight Nazgul5", DroneType.TWOINCHTINYWHOOP, 280.0, false, LocalDate.now().minusYears(1), LocalDate.now().minusMonths(1), Duration.ofHours(42), 550.0);
            Drone d4 = new Drone(0, "Tinyhawk III", DroneType.TOOTHPICKS, 42.0, true, LocalDate.now().minusWeeks(3), LocalDate.now(), Duration.ofHours(1), 180.0);
            Drone d5 = new Drone(0, "Custom Cinewhoop", DroneType.SEVENINCHLONGRAGE, 623.0, true, LocalDate.now().minusMonths(8), LocalDate.now(), Duration.ofHours(28), 890.0);

            droneDAO.insertDrone(d1);
            droneDAO.insertDrone(d2);
            droneDAO.insertDrone(d3);
            droneDAO.insertDrone(d4);
            droneDAO.insertDrone(d5);

            System.out.println("Insert 5 Flight Logs");

            FlightLog fl1 = new FlightLog(0, p1, d1, LocalDate.now(), Duration.ofMinutes(22), "Smooth park flight", 5.8, "Essen Campus");
            FlightLog fl2 = new FlightLog(0, p2, d2, LocalDate.now().minusDays(1), Duration.ofMinutes(15), "Testing new props", 5.8, "Backyard");
            FlightLog fl3 = new FlightLog(0, p3, d3, LocalDate.now().minusDays(3), Duration.ofMinutes(30), "Cinematic sunset video", 2.4, "Rhein River");
            FlightLog fl4 = new FlightLog(0, p4, d4, LocalDate.now().minusWeeks(1), Duration.ofMinutes(8), "Accidental minor crash", 5.8, "Duisburg Campus");
            FlightLog fl5 = new FlightLog(0, p5, d5, LocalDate.now().minusWeeks(2), Duration.ofMinutes(45), "Long range endurance test", 2.4, "Open Field");

            flightLogDAO.insertFlightLog(fl1);
            flightLogDAO.insertFlightLog(fl2);
            flightLogDAO.insertFlightLog(fl3);
            flightLogDAO.insertFlightLog(fl4);
            flightLogDAO.insertFlightLog(fl5);

            System.out.println("Insert 5 Parts");

            Part part1 = new Part(0, "Brushless Motor 2306", "Emax", PartType.MOTOR, d1, Duration.ofHours(12), true);
            Part part2 = new Part(0, "F4 Flight Controller", "BetaFPV", PartType.FC, d2, Duration.ofHours(4), true);
            Part part3 = new Part(0, "Ethix S5 Propellers", "HQProp", PartType.PROPELLERS, d3, Duration.ofHours(2), true);
            Part part4 = new Part(0, "Caddx Vista VTX", "Caddx", PartType.FRAME, d4, Duration.ofHours(25), false);
            Part part5 = new Part(0, "Lipo 4S 1300mAh", "Tattu", PartType.BATTERY, d5, Duration.ofHours(18), true);


            partDAO.insertPart(part1);
            partDAO.insertPart(part2);
            partDAO.insertPart(part3);
            partDAO.insertPart(part4);
            partDAO.insertPart(part5);

            System.out.println("Testing Relationships");

            FlightLog detailedLog = flightLogDAO.getFullyLoadedFlightLog(1);
            if (detailedLog != null) {
                System.out.println("Successfully fetched fully loaded FlightLog.");
                System.out.println("Flight Date: " + detailedLog.getDate());
                System.out.println("Location: " + detailedLog.getLocation());
                if (detailedLog.getPilot() != null && !detailedLog.getPilot().getName().equals("Unknown")) {
                    System.out.println(" Real Pilot Name: " + detailedLog.getPilot().getName());
                } else {
                    System.out.println("Real Pilot Name: Pilot could not be loaded from DB");
                }

                if (detailedLog.getDrone() != null && !detailedLog.getDrone().getModelName().equals("Unknown")) {
                    System.out.println("Real Drone Model: " + detailedLog.getDrone().getModelName());
                } else {
                    System.out.println("Real Drone Model: Drone could not be loaded from DB");
                }
            }

            System.out.println("Trying to login with correct credentials.");
            Pilot loggedInPilot = pilotDAO.loginPilot("yusufyasti", "Yusuf123!");
            if (loggedInPilot != null) {
                System.out.println(" Auth Success! Welcome, " + loggedInPilot.getName());
            }

            System.out.println("Trying to login with WRONG password.");
            pilotDAO.loginPilot("yusufyasti", "wrong_password_123");

            System.out.println("Registering a new pilot with unique username check.");
            String candidateUsername = "dr_fpv_pilot";
            if (pilotDAO.isUsernameTaken(candidateUsername)) {
                System.out.println("Registration Aborted: Username '" + candidateUsername + "' already exists.");
            } else {
                Pilot newPilotCandidate = new Pilot(0, "Hakkı Cetin", candidateUsername, "SecureJohn99!", ExperienceLevel.ADVANCED, Duration.ZERO, 5.8, true);
                pilotDAO.registerPilot(newPilotCandidate, "Cetin99!");
            }
        } catch (Exception e) {
            System.err.println("An error occurred during testing: " + e.getMessage());
        }
        System.out.println("Drone Management System Testing Completed");
    }
}
