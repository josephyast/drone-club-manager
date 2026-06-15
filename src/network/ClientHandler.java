package network;

import model.*;
import network.dto.*;
import db.dao.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final DroneDAO droneDAO;
    private final PilotDAO pilotDAO;
    private final PartDAO partDAO;
    private final FlightLogDAO flightLogDAO;

    private static final Object dbLock = new Object();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.droneDAO = new DroneDAO();
        this.pilotDAO = new PilotDAO();
        this.partDAO = new PartDAO();
        this.flightLogDAO = new FlightLogDAO();
    }

    @Override
    public void run() {
        try{
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.in = new ObjectInputStream(clientSocket.getInputStream());

            String threadName = Thread.currentThread().getName();
            System.out.println("Handshake completed. Client assigned to " + threadName);

            while(true){
                Object input = in.readObject();

                if(input instanceof ClientRequest){
                    ClientRequest request = (ClientRequest) input;
                    System.out.println("[" + threadName + "] Received command: " + request.getCommand());

                    ServerResponse response = processRequest(request);

                    out.writeObject(response);
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Client disconnected: " + Thread.currentThread().getName());
        } finally {
            closeResources();
        }
    }

    private ServerResponse processRequest(ClientRequest request) {
        Command command = request.getCommand();

        try {
            switch (command) {
                case LOGIN -> { return new ServerResponse(true, "Login successful"); }
                case LOGOUT -> { return new ServerResponse(true, "Logout successful"); }
                case REGISTER -> {
                    synchronized (dbLock) {
                        PilotDTO dto = (PilotDTO) request.getData();
                        Pilot pilot = new Pilot(0, dto.getName(), dto.getUsername(), dto.getPassword(), ExperienceLevel.valueOf(dto.getExperienceLevel()), java.time.Duration.ofSeconds(dto.getTotalFlightHoursInSeconds()), dto.getAssignedFrequency(), dto.isActive());
                        pilotDAO.insertPilot(pilot);
                        return new ServerResponse(true, "Registration successful");
                    }
                }

                case ADD_DRONE -> {
                    synchronized (dbLock) {
                        DroneDTO dto = (DroneDTO) request.getData();
                        Drone drone = new Drone(0, dto.getModelName(), DroneType.valueOf(dto.getType()), dto.getWeight(), dto.isFunctional(), java.time.LocalDate.parse(dto.getBuildDate()), java.time.LocalDate.parse(dto.getLastMaintenanceDate()), java.time.Duration.ofSeconds(dto.getTotalFlightTimeInSeconds()), dto.getCurrentFrequency());
                        droneDAO.insertDrone(drone);
                        return new ServerResponse(true, "Drone added successfully");
                    }
                }
                case REMOVE_DRONE -> {
                    synchronized (dbLock) {
                        int id = (int) request.getData();
                        droneDAO.deleteDrone(id);
                        return new ServerResponse(true, "Drone removed successfully");
                    }
                }
                case GET_DRONE -> {
                    int id = (int) request.getData();
                    Drone d = droneDAO.getDroneById(id);
                    if (d == null) return new ServerResponse(false, "Drone not found");
                    DroneDTO dto = new DroneDTO(d.getId(), d.getModelName(), d.getType().name(), d.getWeight(), d.isFunctional(), d.getBuildDate().toString(), d.getLastMaintenanceDate().toString(), d.getTotalFlightTime().toSeconds(), d.getCurrentFrequency());
                    return new ServerResponse(true, "Drone fetched", dto);
                }
                case GET_ALL_DRONES -> {
                    List<Drone> drones = droneDAO.getAllDrones();
                    List<DroneDTO> dtos = new ArrayList<>();
                    for (Drone d : drones) {
                        dtos.add(new DroneDTO(d.getId(), d.getModelName(), d.getType().name(), d.getWeight(), d.isFunctional(), d.getBuildDate().toString(), d.getLastMaintenanceDate().toString(), d.getTotalFlightTime().toSeconds(), d.getCurrentFrequency()));
                    }
                    return new ServerResponse(true, "Drones fetched", dtos);
                }
                case UPDATE_DRONE -> {
                    synchronized (dbLock) {
                        DroneDTO dto = (DroneDTO) request.getData();
                        Drone drone = new Drone(dto.getId(), dto.getModelName(), DroneType.valueOf(dto.getType()), dto.getWeight(), dto.isFunctional(), java.time.LocalDate.parse(dto.getBuildDate()), java.time.LocalDate.parse(dto.getLastMaintenanceDate()), java.time.Duration.ofSeconds(dto.getTotalFlightTimeInSeconds()), dto.getCurrentFrequency());
                        droneDAO.updateDrone(drone);
                        return new ServerResponse(true, "Drone updated successfully");
                    }
                }

                case ADD_PILOT -> {
                    synchronized (dbLock) {
                        PilotDTO dto = (PilotDTO) request.getData();
                        Pilot pilot = new Pilot(0, dto.getName(), dto.getUsername(), dto.getPassword(), ExperienceLevel.valueOf(dto.getExperienceLevel()), java.time.Duration.ofSeconds(dto.getTotalFlightHoursInSeconds()), dto.getAssignedFrequency(), dto.isActive());
                        pilotDAO.insertPilot(pilot);
                        return new ServerResponse(true, "Pilot added successfully");
                    }
                }
                case REMOVE_PILOT -> {
                    synchronized (dbLock) {
                        int id = (int) request.getData();
                        pilotDAO.deletePilot(id);
                        return new ServerResponse(true, "Pilot removed successfully");
                    }
                }
                case GET_PILOT -> {
                    int id = (int) request.getData();
                    Pilot p = pilotDAO.getPilotById(id);
                    if (p == null) return new ServerResponse(false, "Pilot not found");
                    PilotDTO dto = new PilotDTO(p.getId(), p.getName(), p.getUsername(), null, p.getExperienceLevel().name(), p.getTotalFlightHours().toSeconds(), p.getAssignedFrequency(), p.isActive());
                    return new ServerResponse(true, "Pilot fetched", dto);
                }
                case GET_ALL_PILOTS -> {
                    List<Pilot> pilots = pilotDAO.getAllPilots();
                    List<PilotDTO> dtos = new ArrayList<>();
                    for (Pilot p : pilots) {
                        dtos.add(new PilotDTO(p.getId(), p.getName(), p.getUsername(), null, p.getExperienceLevel().name(), p.getTotalFlightHours().toSeconds(), p.getAssignedFrequency(), p.isActive()));
                    }
                    return new ServerResponse(true, "Pilots fetched", dtos);
                }
                case UPDATE_PILOT -> {
                    synchronized (dbLock) {
                        PilotDTO dto = (PilotDTO) request.getData();
                        Pilot pilot = new Pilot(dto.getId(), dto.getName(), dto.getUsername(), dto.getPassword(), ExperienceLevel.valueOf(dto.getExperienceLevel()), java.time.Duration.ofSeconds(dto.getTotalFlightHoursInSeconds()), dto.getAssignedFrequency(), dto.isActive());
                        pilotDAO.updatePilot(pilot);
                        return new ServerResponse(true, "Pilot updated successfully");
                    }
                }

                case ADD_FLIGHT -> {
                    synchronized (dbLock) {
                        FlightLogDTO dto = (FlightLogDTO) request.getData();

                        Pilot mockPilot = new Pilot(dto.getPilotId(), "Unknown", "Unknown", "Unknown", ExperienceLevel.BEGINNER, java.time.Duration.ZERO, 0.0, false);
                        Drone mockDrone = new Drone(dto.getDroneId(), "Unknown", DroneType.TOOTHPICKS, 0.0, false, java.time.LocalDate.now(), java.time.LocalDate.now(), java.time.Duration.ZERO, 0.0);

                        FlightLog log = new FlightLog(
                                0,
                                mockPilot,
                                mockDrone,
                                java.time.LocalDate.parse(dto.getDate()),
                                java.time.Duration.ofSeconds(dto.getFlightDurationInSeconds()),
                                dto.getComment(),
                                dto.getUsedFrequency(),
                                dto.getLocation()
                        );
                        flightLogDAO.insertFlightLog(log);
                        return new ServerResponse(true, "Flight log added successfully");
                    }
                }
                case REMOVE_FLIGHT -> {
                    synchronized (dbLock) {
                        int id = (int) request.getData();
                        flightLogDAO.deleteFlightLog(id);
                        return new ServerResponse(true, "Flight log removed successfully");
                    }
                }
                case GET_FLIGHT -> {
                    int id = (int) request.getData();
                    FlightLog f = flightLogDAO.getFlightLogById(id);
                    if (f == null) return new ServerResponse(false, "Flight log not found");

                    int associatedPilotId = (f.getPilot() != null) ? f.getPilot().getId() : 0;
                    int associatedDroneId = (f.getDrone() != null) ? f.getDrone().getId() : 0;

                    FlightLogDTO dto = new FlightLogDTO(f.getId(), associatedPilotId, "", associatedDroneId, "", f.getDate().toString(), f.getFlightDuration().toSeconds(), f.getComment(), f.getUsedFrequency(), f.getLocation());
                    return new ServerResponse(true, "Flight log fetched", dto);
                }
                case GET_ALL_FLIGHTS -> {
                    List<FlightLog> flights = flightLogDAO.getAllFlightLogs();
                    List<FlightLogDTO> dtos = new ArrayList<>();
                    for (FlightLog f : flights) {
                        int associatedPilotId = (f.getPilot() != null) ? f.getPilot().getId() : 0;
                        int associatedDroneId = (f.getDrone() != null) ? f.getDrone().getId() : 0;

                        dtos.add(new FlightLogDTO(f.getId(), associatedPilotId, "", associatedDroneId, "", f.getDate().toString(), f.getFlightDuration().toSeconds(), f.getComment(), f.getUsedFrequency(), f.getLocation()));
                    }
                    return new ServerResponse(true, "Flight logs fetched", dtos);
                }
                case UPDATE_FLIGHT -> {
                    synchronized (dbLock) {
                        FlightLogDTO dto = (FlightLogDTO) request.getData();

                        Pilot mockPilot = new Pilot(dto.getPilotId(), "Unknown", "Unknown", "Unknown", ExperienceLevel.BEGINNER, java.time.Duration.ZERO, 0.0, false);
                        Drone mockDrone = new Drone(dto.getDroneId(), "Unknown", DroneType.TOOTHPICKS, 0.0, false, java.time.LocalDate.now(), java.time.LocalDate.now(), java.time.Duration.ZERO, 0.0);

                        FlightLog log = new FlightLog(
                                dto.getId(),
                                mockPilot,
                                mockDrone,
                                java.time.LocalDate.parse(dto.getDate()),
                                java.time.Duration.ofSeconds(dto.getFlightDurationInSeconds()),
                                dto.getComment(),
                                dto.getUsedFrequency(),
                                dto.getLocation()
                        );
                        flightLogDAO.updateFlightLog(log);
                        return new ServerResponse(true, "Flight log updated successfully");
                    }
                }
                case ADD_PART -> {
                    synchronized (dbLock) {
                        PartDTO dto = (PartDTO) request.getData();
                        Drone mockDrone = new Drone(dto.getDroneId(), "Unknown", DroneType.TOOTHPICKS, 0.0, false, java.time.LocalDate.now(), java.time.LocalDate.now(), java.time.Duration.ZERO, 0.0);
                        Part part = new Part(0, dto.getName(), dto.getBrand(), PartType.valueOf(dto.getType()), mockDrone, java.time.Duration.ofSeconds(dto.getOperatingHoursInSeconds()), dto.isWorking());
                        partDAO.insertPart(part);
                        return new ServerResponse(true, "Part added successfully");
                    }
                }
                case REMOVE_PART -> {
                    synchronized (dbLock) {
                        int id = (int) request.getData();
                        partDAO.deletePart(id);
                        return new ServerResponse(true, "Part removed successfully");
                    }
                }
                case GET_PART -> {
                    int id = (int) request.getData();
                    Part p = partDAO.getPartById(id);
                    if (p == null) return new ServerResponse(false, "Part not found");

                    int associatedDroneId = (p.getDrone() != null) ? p.getDrone().getId() : 0;

                    PartDTO dto = new PartDTO(p.getId(), p.getName(), p.getBrand(), p.getType().name(), associatedDroneId, p.getOperatingHours().toSeconds(), p.isWorking());
                    return new ServerResponse(true, "Part fetched", dto);
                }
                case GET_ALL_PARTS -> {
                    List<Part> parts = partDAO.getAllParts();
                    List<PartDTO> dtos = new ArrayList<>();
                    for (Part p : parts) {
                        int associatedDroneId = (p.getDrone() != null) ? p.getDrone().getId() : 0;

                        dtos.add(new PartDTO(p.getId(), p.getName(), p.getBrand(), p.getType().name(), associatedDroneId, p.getOperatingHours().toSeconds(), p.isWorking()));
                    }
                    return new ServerResponse(true, "Parts fetched", dtos);
                }
                case UPDATE_PART -> {
                    synchronized (dbLock) {
                        PartDTO dto = (PartDTO) request.getData();

                        Drone mockDrone = new Drone(dto.getDroneId(), "Unknown", DroneType.TOOTHPICKS, 0.0, false, java.time.LocalDate.now(), java.time.LocalDate.now(), java.time.Duration.ZERO, 0.0);

                        Part part = new Part(
                                dto.getId(),
                                dto.getName(),
                                dto.getBrand(),
                                PartType.valueOf(dto.getType()),
                                mockDrone,
                                java.time.Duration.ofSeconds(dto.getOperatingHoursInSeconds()),
                                dto.isWorking()
                        );

                        partDAO.updatePart(part);
                        return new ServerResponse(true, "Part updated successfully");
                    }
                }

                case ATTACH_PART_TO_DRONE -> {
                    synchronized (dbLock) {
                        ActionRequestDTO dto = (ActionRequestDTO) request.getData();
                        partDAO.attachPartToDrone(dto.getTargetId(), dto.getAssociatedId());
                        return new ServerResponse(true, "Part successfully attached to drone");
                    }
                }
                case DETACH_PART_TO_DRONE -> {
                    synchronized (dbLock) {
                        int partId = (int) request.getData();
                        partDAO.detachPartFromDrone(partId);
                        return new ServerResponse(true, "Part detached from drone");
                    }
                }

                case REQUEST_FREQUENCY -> {
                    synchronized (dbLock) {
                        ActionRequestDTO dto = (ActionRequestDTO) request.getData();
                        boolean success = pilotDAO.requestFrequency(dto.getTargetId(), dto.getValue());
                        if (success) {
                            return new ServerResponse(true, "Frequency allocated successfully");
                        } else {
                            return new ServerResponse(false, "Frequency allocation failed: Channel busy or overlap detected");
                        }
                    }
                }
                case RELEASE_FREQUENCY -> {
                    synchronized (dbLock) {
                        int droneId = (int) request.getData();
                        pilotDAO.releaseFrequency(droneId);
                        return new ServerResponse(true, "Frequency released successfully");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error while executing command " + command + ": " + e.getMessage());
            return new ServerResponse(false, "Server Business Logic Error: " + e.getMessage());
        }

        return new ServerResponse(false, "Command workflow not finalized.");
    }
    private void closeResources(){
        try{
            if(in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Resources successfully cleaned up for disconnected client.");
        } catch(IOException e){
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
