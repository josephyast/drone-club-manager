package network;

import model.*;
import network.dto.*;
import db.dao.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDate;
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
                case LOGIN -> {
                    synchronized (dbLock) {
                        PilotDTO loginData = (PilotDTO) request.getData();
                        if (loginData == null || loginData.getUsername() == null || loginData.getPassword() == null) {
                            return new ServerResponse(false, "Username or password missing.");
                        }
                        Pilot verifiedPilot = pilotDAO.loginPilot(loginData.getUsername(), loginData.getPassword());

                        if (verifiedPilot != null) {
                            PilotDTO responseDto = new PilotDTO(
                                    verifiedPilot.getId(),
                                    verifiedPilot.getName(),
                                    verifiedPilot.getUsername(),
                                    null,
                                    verifiedPilot.getExperienceLevel().name(),
                                    verifiedPilot.getTotalFlightHours().toSeconds(),
                                    verifiedPilot.getAssignedFrequency(),
                                    verifiedPilot.isActive()
                            );
                            return new ServerResponse(true, "Login successful", responseDto);
                        } else {
                            return new ServerResponse(false, "Invalid username or password.");
                        }
                    }
                }
                case LOGOUT -> {
                    synchronized (dbLock) {
                        if (request.getData() instanceof Integer) {
                            int pilotId = (int) request.getData();
                            pilotDAO.releaseFrequency(pilotId);

                            System.out.println("Pilot with ID " + pilotId + " has successfully logged out.");
                            return new ServerResponse(true, "Logout successful and frequency released.");
                        }

                        return new ServerResponse(true, "Logout successfully completed.");
                    }
                }
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
                        Drone drone = new Drone(0, dto.getModelName(), DroneType.valueOf(dto.getType()), dto.getWeight(), DroneStatus.valueOf(dto.getStatus()), java.time.LocalDate.parse(dto.getBuildDate()), java.time.LocalDate.parse(dto.getLastMaintenanceDate()), java.time.Duration.ofSeconds(dto.getTotalFlightTimeInSeconds()), dto.getCurrentFrequency());
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
                    DroneDTO dto = new DroneDTO(d.getId(), d.getModelName(), d.getType().name(), d.getWeight(), d.getStatus().name(), d.getBuildDate().toString(), d.getLastMaintenanceDate().toString(), d.getTotalFlightTime().toSeconds(), d.getCurrentFrequency());
                    return new ServerResponse(true, "Drone fetched", dto);
                }
                case GET_ALL_DRONES -> {
                    List<Drone> drones = droneDAO.getAllDrones();
                    List<DroneDTO> dtos = new ArrayList<>();
                    for (Drone d : drones) {
                        dtos.add(new DroneDTO(d.getId(), d.getModelName(), d.getType().name(), d.getWeight(), d.getStatus().name(), d.getBuildDate().toString(), d.getLastMaintenanceDate().toString(), d.getTotalFlightTime().toSeconds(), d.getCurrentFrequency()));
                    }
                    return new ServerResponse(true, "Drones fetched", dtos);
                }
                case UPDATE_DRONE -> {
                    synchronized (dbLock) {
                        DroneDTO dto = (DroneDTO) request.getData();
                        Drone drone = new Drone(dto.getId(), dto.getModelName(), DroneType.valueOf(dto.getType()), dto.getWeight(), DroneStatus.valueOf(dto.getStatus()), java.time.LocalDate.parse(dto.getBuildDate()), java.time.LocalDate.parse(dto.getLastMaintenanceDate()), java.time.Duration.ofSeconds(dto.getTotalFlightTimeInSeconds()), dto.getCurrentFrequency());
                        droneDAO.updateDrone(drone);
                        return new ServerResponse(true, "Drone updated successfully");
                    }
                }

                case UPDATE_DRONE_STATUS -> {
                    synchronized (dbLock) {
                        DroneDTO dto = (DroneDTO) request.getData();
                        droneDAO.updateDroneStatus(dto.getId(), DroneStatus.valueOf(dto.getStatus()));
                        return new ServerResponse(true, "Drone status updated successfully");
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

                        if (!FrequencyManager.lockFrequency(dto.getUsedFrequency())) {
                            return new ServerResponse(false, "Frequency " + dto.getUsedFrequency() + " MHz is currently in use!");
                        }

                        try {
                            int pId = (dto.getPilotId() <= 0) ? 1 : dto.getPilotId();
                            Pilot pilot = pilotDAO.getPilotById(pId);
                            Drone drone = droneDAO.getDroneById(dto.getDroneId());

                            if (pilot == null || drone == null) {
                                FrequencyManager.releaseFrequency(dto.getUsedFrequency());
                                return new ServerResponse(false, "Invalid Pilot or Drone ID.");
                            }

                            if (drone.getStatus() != DroneStatus.AVAILABLE) {
                                FrequencyManager.releaseFrequency(dto.getUsedFrequency());
                                return new ServerResponse(false, "Cannot start flight: Drone is currently " + drone.getStatus() + ". Only AVAILABLE drones can fly.");
                            }

                            FlightLog log = new FlightLog(
                                    0, pilot, drone,
                                    LocalDate.parse(dto.getDate()),
                                    Duration.ofSeconds(dto.getFlightDurationInSeconds()),
                                    dto.getComment(), dto.getUsedFrequency(), dto.getLocation()
                            );

                            flightLogDAO.insertFlightLog(log);

                            List<FlightLog> allLogs = flightLogDAO.getAllFlightLogs();
                            int createdId = allLogs.isEmpty() ? 0 : allLogs.get(allLogs.size() - 1).getId();

                            drone.setStatus(DroneStatus.IN_FLIGHT);
                            drone.setCurrentFrequency(dto.getUsedFrequency());

                            if (dto.getFlightDurationInSeconds() > 0) {
                                drone.setTotalFlightTime(drone.getTotalFlightTime().plus(Duration.ofSeconds(dto.getFlightDurationInSeconds())));
                            }

                            droneDAO.updateDrone(drone);

                            List<Part> parts = partDAO.getPartsByDroneId(dto.getDroneId());
                            for (Part p : parts) {
                                Duration newTime = p.getOperatingHours().plus(Duration.ofSeconds(dto.getFlightDurationInSeconds()));
                                p.setOperatingHours(newTime);

                                int limit = getMaintenanceLimitHours(p.getType().name()) * 3600;
                                if (newTime.toSeconds() >= limit) {
                                    p.setWorking(false);
                                    partDAO.updatePartStatus(p.getId(), false);
                                } else {
                                    partDAO.updatePart(p);
                                }
                            }

                            FlightLogDTO responseDto = new FlightLogDTO(
                                    createdId, pId, pilot.getName(), drone.getId(), drone.getModelName(),
                                    dto.getDate(), dto.getFlightDurationInSeconds(), dto.getComment(),
                                    dto.getUsedFrequency(), dto.getLocation()
                            );

                            return new ServerResponse(true, "Flight recorded, drone status updated to IN_FLIGHT.", responseDto);
                        } catch (Exception e) {
                            FrequencyManager.releaseFrequency(dto.getUsedFrequency());
                            throw e;
                        }
                    }
                }
                case LAND_DRONE -> {
                    synchronized (dbLock) {
                        int droneId = (int) request.getData();

                        Drone d = droneDAO.getDroneById(droneId);
                        if (d != null) {
                            if (d.getCurrentFrequency() > 0) {
                                FrequencyManager.releaseFrequency(d.getCurrentFrequency());
                            }

                            d.setCurrentFrequency(0.0);

                            List<Part> parts = partDAO.getPartsByDroneId(droneId);
                            boolean needsMaintenance = parts.stream().anyMatch(p -> !p.isWorking());

                            DroneStatus finalStatus = needsMaintenance ? DroneStatus.MAINTENANCE : DroneStatus.AVAILABLE;
                            d.setStatus(finalStatus);

                            droneDAO.updateDrone(d);

                            return new ServerResponse(true, "Drone landed. Status: " + finalStatus);
                        }
                        return new ServerResponse(false, "Drone not found for landing.");
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
                    synchronized (dbLock) {
                        List<FlightLog> logs = flightLogDAO.getAllFlightLogs();
                        List<FlightLogDTO> dtos = new ArrayList<>();

                        for (FlightLog log : logs) {

                            int pId = (log.getPilot() != null) ? log.getPilot().getId() : 0;
                            int dId = (log.getDrone() != null) ? log.getDrone().getId() : 0;

                            Pilot p = (pId != 0) ? pilotDAO.getPilotById(pId) : null;
                            Drone d = (dId != 0) ? droneDAO.getDroneById(dId) : null;

                            String pName = (p != null) ? p.getName() : "Unknown Pilot";
                            String dModel = (d != null) ? d.getModelName() : "Unknown Drone";

                            dtos.add(new FlightLogDTO(
                                    log.getId(),
                                    pId, pName,
                                    dId, dModel,
                                    log.getDate().toString(),
                                    log.getFlightDuration().getSeconds(),
                                    log.getComment(),
                                    log.getUsedFrequency(),
                                    log.getLocation()
                            ));
                        }
                        return new ServerResponse(true, "Flights loaded", dtos);
                    }
                }
                case UPDATE_FLIGHT -> {
                    synchronized (dbLock) {
                        FlightLogDTO dto = (FlightLogDTO) request.getData();
                        int targetLogId = dto.getId();

                        if (targetLogId <= 0) {
                            List<FlightLog> allLogs = flightLogDAO.getAllFlightLogs();
                            for (int i = allLogs.size() - 1; i >= 0; i--) {
                                FlightLog l = allLogs.get(i);
                                if (l.getDrone() != null && l.getDrone().getId() == dto.getDroneId()) {
                                    targetLogId = l.getId();
                                    break;
                                }
                            }
                        }

                        FlightLog existingLog = flightLogDAO.getFlightLogById(targetLogId);
                        long oldDurationSeconds = (existingLog != null) ? existingLog.getFlightDuration().toSeconds() : 0;
                        long durationDiff = dto.getFlightDurationInSeconds() - oldDurationSeconds;

                        int pilotId = (dto.getPilotId() > 0) ? dto.getPilotId() : ((existingLog != null && existingLog.getPilot() != null) ? existingLog.getPilot().getId() : 0);
                        int droneId = (dto.getDroneId() > 0) ? dto.getDroneId() : ((existingLog != null && existingLog.getDrone() != null) ? existingLog.getDrone().getId() : 0);

                        Pilot realPilot = (pilotId > 0) ? pilotDAO.getPilotById(pilotId) : null;
                        Drone realDrone = (droneId > 0) ? droneDAO.getDroneById(droneId) : null;

                        FlightLog log = new FlightLog(
                                targetLogId,
                                realPilot,
                                realDrone,
                                java.time.LocalDate.parse(dto.getDate()),
                                java.time.Duration.ofSeconds(dto.getFlightDurationInSeconds()),
                                dto.getComment(),
                                dto.getUsedFrequency(),
                                dto.getLocation()
                        );
                        flightLogDAO.updateFlightLog(log);

                        if (durationDiff > 0) {
                            Duration addedDuration = Duration.ofSeconds(durationDiff);
                            realDrone.setTotalFlightTime(realDrone.getTotalFlightTime().plus(addedDuration));
                            droneDAO.updateDrone(realDrone);

                            realPilot.setTotalFlightHours(realPilot.getTotalFlightHours().plus(addedDuration));
                            pilotDAO.updatePilot(realPilot);

                            List<Part> attachedParts = partDAO.getPartsByDroneId(realDrone.getId());
                            for (Part part : attachedParts) {
                                Duration newOperatingHours = part.getOperatingHours().plus(addedDuration);
                                part.setOperatingHours(newOperatingHours);

                                int limitInSeconds = getMaintenanceLimitHours(part.getType().name()) * 3600;
                                if (newOperatingHours.toSeconds() >= limitInSeconds) {
                                    part.setWorking(false);
                                    partDAO.updatePartStatus(part.getId(), false);
                                } else {
                                    partDAO.updatePart(part);
                                }
                            }
                        }

                        return new ServerResponse(true, "Flight log updated successfully");
                    }
                }
                case ADD_PART -> {
                    synchronized (dbLock) {
                        PartDTO dto = (PartDTO) request.getData();
                        Drone mockDrone = new Drone(dto.getDroneId(), "Unknown", DroneType.TOOTHPICKS, 0.0, DroneStatus.AVAILABLE, java.time.LocalDate.now(), java.time.LocalDate.now(), java.time.Duration.ZERO, 0.0);
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

                    Drone d = (associatedDroneId != 0) ? droneDAO.getDroneById(associatedDroneId) : null;
                    String dName = (d != null) ? d.getModelName() : "None";

                    PartDTO dto = new PartDTO(p.getId(), p.getName(), p.getBrand(), p.getType().name(), associatedDroneId, dName, p.getOperatingHours().toSeconds(), p.isWorking());
                    return new ServerResponse(true, "Part fetched", dto);
                }
                case GET_ALL_PARTS -> {
                    List<Part> parts = partDAO.getAllParts();
                    List<PartDTO> dtos = new ArrayList<>();

                    for (Part p : parts) {
                        int maintenanceLimitHours = getMaintenanceLimitHours(p.getType().name());
                        long hoursWorked = p.getOperatingHours().toHours();

                        if (hoursWorked >= maintenanceLimitHours && p.isWorking()) {
                            partDAO.updatePartStatus(p.getId(), false);
                            p.setWorking(false);
                        }

                        int dId = (p.getDrone() != null) ? p.getDrone().getId() : 0;
                        Drone d = (dId != 0) ? droneDAO.getDroneById(dId) : null;
                        String dName = (d != null) ? d.getModelName() : "None";

                        dtos.add(new PartDTO(
                                p.getId(), p.getName(), p.getBrand(), p.getType().name(),
                                dId, dName, p.getOperatingHours().toSeconds(), p.isWorking()
                        ));
                    }
                    return new ServerResponse(true, "Parts fetched", dtos);
                }
                case UPDATE_PART -> {
                    synchronized (dbLock) {
                        PartDTO dto = (PartDTO) request.getData();

                        Drone mockDrone = new Drone(dto.getDroneId(), "Unknown", DroneType.TOOTHPICKS, 0.0, DroneStatus.AVAILABLE, java.time.LocalDate.now(), java.time.LocalDate.now(), java.time.Duration.ZERO, 0.0);

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

                        Part part = partDAO.getPartById(dto.getTargetId());
                        if (part == null) return new ServerResponse(false, "Part not found.");

                        if (!part.isWorking()) {
                            return new ServerResponse(false, "Cannot attach: This part is currently not working (needs repair).");
                        }

                        List<Part> attachedParts = partDAO.getPartsByDroneId(dto.getAssociatedId());

                        long count = attachedParts.stream()
                                .filter(p -> p.getType() == part.getType())
                                .count();

                        int limit = getCapacityLimitForType(part.getType());
                        if (count >= limit) {
                            return new ServerResponse(false, "Limit exceeded: Max " + limit + " " + part.getType() + " allowed per drone.");
                        }

                        partDAO.attachPartToDrone(dto.getTargetId(), dto.getAssociatedId());
                        return new ServerResponse(true, "Part successfully attached to drone");
                    }
                }                case DETACH_PART_TO_DRONE -> {
                    synchronized (dbLock) {
                        int partId = (int) request.getData();
                        partDAO.detachPartFromDrone(partId);
                        return new ServerResponse(true, "Part detached from drone");
                    }
                }
                case REQUEST_DRONE -> {
                    synchronized (dbLock) {
                        int droneId = (int) request.getData();
                        Drone d = droneDAO.getDroneById(droneId);

                        if (d.getStatus() == DroneStatus.AVAILABLE) {
                            droneDAO.updateDroneStatus(droneId, DroneStatus.IN_FLIGHT);
                            return new ServerResponse(true, "Flight started");
                        } else {
                            return new ServerResponse(false, "Drone is not available (Status: " + d.getStatus() + ")");
                        }
                    }
                }
                case RELEASE_DRONE -> {
                    synchronized (dbLock) {
                        int droneId = (int) request.getData();
                        droneDAO.updateDroneStatus(droneId, DroneStatus.AVAILABLE);
                        return new ServerResponse(true, "Drone is now available");
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

                case GET_AVAILABLE_FREQUENCIES -> {
                    return new ServerResponse(true, "List fetched", FrequencyManager.getAvailableFreqs());
                }

                case FIX_PART -> {
                    synchronized (dbLock) {
                        int partId = (int) request.getData();
                        partDAO.fixPart(partId);
                        return new ServerResponse(true, "Part has been repaired successfully.");
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

    private int getCapacityLimitForType(PartType type) {
        return switch (type) {
            case FRAME, FC, BATTERY, PDB, VTX, VIDEOANTENNA, CAMERA, TRANSMITTER, GOOGLES -> 1;
            case MOTOR, PROPELLERS -> 4;
            case ESC -> 4;
            default -> 1;
        };
    }

    public void updatePartStatus(PartDTO part) {
        long maxHours = getMaintenanceLimitHours(part.getType()) * 3600L;

        if (part.getOperatingHoursInSeconds() >= maxHours) {
            part.setWorking(false);
        }
    }

    private int getMaintenanceLimitHours(String type) {
        return switch (type.toUpperCase()) {
            case "MOTOR", "ESC" -> 50;
            case "PROPELLERS" -> 10;
            default -> 100;
        };
    }
}
