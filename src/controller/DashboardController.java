package controller;

import gui.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.DroneStatus;
import network.ClientRequest;
import network.Command;
import network.DroneClient;
import network.ServerResponse;
import network.dto.*;
import java.time.LocalDate;
import java.util.List;


public class DashboardController {

    private final MainDashboardView view;
    private final Stage primaryStage;
    private final DroneClient client;
    private int activeFlightLogId = 0;
    private String currentCategory = "Drones";
    private int loggedInPilotId;

    public DashboardController(MainDashboardView view, Stage primaryStage, DroneClient client, int loggedInPilotId) {        this.view = view;
        this.primaryStage = primaryStage;
        this.client = client;
        this.loggedInPilotId = loggedInPilotId;

        initEventHandlers();
        loadCategoryData("Drones");
    }

    private void initEventHandlers() {
        view.getBtnDrones().setOnAction(e -> loadCategoryData("Drones"));
        view.getBtnPilots().setOnAction(e -> loadCategoryData("Pilots"));
        view.getBtnFlights().setOnAction(e -> loadCategoryData("Flights"));
        view.getBtnParts().setOnAction(e -> loadCategoryData("Parts"));

        view.getBtnLogout().setOnAction(e -> handleLogout());

        view.getBtnAdd().setOnAction(e -> handleAddAction());
        view.getBtnRemove().setOnAction(e -> handleRemoveAction());
        view.getBtnUpdate().setOnAction(e -> handleUpdateAction());

        view.getBtnFreqRequest().setOnAction(e -> handleFrequencyAction(Command.REQUEST_FREQUENCY));
        view.getBtnFreqRelease().setOnAction(e -> handleFrequencyAction(Command.RELEASE_FREQUENCY));
        view.getBtnAttachPart().setOnAction(e -> handlePartAttachment(Command.ATTACH_PART_TO_DRONE));
        view.getBtnDetachPart().setOnAction(e -> handlePartAttachment(Command.DETACH_PART_TO_DRONE));
        view.getBtnFixPart().setOnAction(e -> handleFixPart());
        view.getBtnFlightNow().setOnAction(e -> handleFlightAction());
    }

    private void loadCategoryData(String category) {
        this.currentCategory = category;

        view.getMainTable().getItems().clear();
        view.getMainTable().getColumns().clear();

        view.getBtnAdd().setVisible(false);
        view.getBtnRemove().setVisible(false);
        view.getBtnUpdate().setVisible(false);
        view.hideSpecialButtons();

        Command fetchCommand = switch (category) {
            case "Pilots" -> {
                setupPilotColumns();
                yield Command.GET_ALL_PILOTS;
            }
            case "Flights" -> {
                setupFlightColumns();
                yield Command.GET_ALL_FLIGHTS;
            }
            case "Parts" -> {
                setupPartColumns();
                view.getBtnAdd().setVisible(true);
                view.getBtnAttachPart().setVisible(true);
                view.getBtnDetachPart().setVisible(true);
                view.getBtnFixPart().setVisible(true);
                yield Command.GET_ALL_PARTS;
            }
            case "Drones" -> {
                setupDroneColumns();
                view.getBtnAdd().setVisible(true);
                view.getBtnRemove().setVisible(true);
                view.getBtnUpdate().setVisible(true);
                view.getBtnFlightNow().setVisible(true);
                yield Command.GET_ALL_DRONES;
            }
            default -> {
                showError("Unknown Category", "Invalid category selected", "The selected category is not recognized.");
                yield null;
            }
        };

        Task<ServerResponse> loadTask = new Task<>() {
            @Override
            protected ServerResponse call() throws Exception {
                if(!client.isConnected()) client.connect();
                return client.sendRequest(new ClientRequest(fetchCommand, null));
            }
        };

        loadTask.setOnSucceeded(e -> {
            ServerResponse response = loadTask.getValue();
            if (response != null && response.isSuccess()) {
                List<?> dataList = (List<?>) response.getData();
                view.getMainTable().setItems(FXCollections.observableArrayList(dataList));
            } else {
                String errorMsg = (response != null && response.getMessage() != null) ? response.getMessage() : "Unknown reason.";
                showError("Data Error", "Data could not be loaded from server.", "Reason: " + errorMsg);
            }
        });

        loadTask.setOnFailed(e -> {
            handleNetworkError(loadTask.getException());
        });

        Thread t = new Thread(loadTask);
        t.setDaemon(true);
        t.start();

    }

    private void setupDroneColumns() {
        TableColumn<Object, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Object, String> colModel = new TableColumn<>("Model");
        colModel.setCellValueFactory(new PropertyValueFactory<>("modelName"));

        TableColumn<Object, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Object, Double> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<Object, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String statusStr, boolean empty) {
                super.updateItem(statusStr, empty);
                if (empty || statusStr == null) {
                    setText(null); setGraphic(null);
                } else {
                    setText(statusStr);

                    try {
                        DroneStatus status = DroneStatus.valueOf(statusStr.toUpperCase().trim());
                        switch (status) {
                            case AVAILABLE -> setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            case IN_FLIGHT -> setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            case MAINTENANCE -> setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        }
                    } catch (IllegalArgumentException ex) {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });

        TableColumn<Object, String> colBuildDate = new TableColumn<>("Build Date");
        colBuildDate.setCellValueFactory(new PropertyValueFactory<>("buildDate"));

        TableColumn<Object, String> colLastMaint = new TableColumn<>("Last Maint.");
        colLastMaint.setCellValueFactory(new PropertyValueFactory<>("lastMaintenanceDate"));

        TableColumn<Object, String> colFlightTime = new TableColumn<>("Total Flight Time");
        colFlightTime.setCellValueFactory(new PropertyValueFactory<>("formattedFlightTime"));

        TableColumn<Object, Double> colFreq = new TableColumn<>("Frequency");
        colFreq.setCellValueFactory(new PropertyValueFactory<>("currentFrequency"));

        view.getMainTable().getColumns().addAll(
                colId, colModel, colType, colWeight, colStatus,
                colBuildDate, colLastMaint, colFlightTime, colFreq
        );
    }

    private void setupPilotColumns() {
        TableColumn<Object, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Object, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Object, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Object, String> colExp = new TableColumn<>("Experience");
        colExp.setCellValueFactory(new PropertyValueFactory<>("experienceLevel"));

        TableColumn<Object, String> colHours = new TableColumn<>("Flight Time");
        colHours.setCellValueFactory(new PropertyValueFactory<>("formattedFlightHours"));

        TableColumn<Object, Double> colFreq = new TableColumn<>("Frequency");
        colFreq.setCellValueFactory(new PropertyValueFactory<>("assignedFrequency"));

        TableColumn<Object, Boolean> colActive = new TableColumn<>("Active");
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        view.getMainTable().getColumns().addAll(colId, colName, colUser, colExp, colHours, colFreq, colActive);
    }
    private void setupFlightColumns() {
        TableColumn<Object, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Object, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Object, String> colPilot = new TableColumn<>("Pilot");
        colPilot.setCellValueFactory(new PropertyValueFactory<>("pilotName"));

        TableColumn<Object, String> colDrone = new TableColumn<>("Drone");
        colDrone.setCellValueFactory(new PropertyValueFactory<>("droneModelName"));

        TableColumn<Object, String> colDuration = new TableColumn<>("Duration");
        colDuration.setCellValueFactory(new PropertyValueFactory<>("formattedDuration"));

        TableColumn<Object, Double> colFreq = new TableColumn<>("Freq.");
        colFreq.setCellValueFactory(new PropertyValueFactory<>("usedFrequency"));

        TableColumn<Object, String> colLocation = new TableColumn<>("Location");
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Object, String> colComment = new TableColumn<>("Comment");
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        view.getMainTable().getColumns().addAll(
                colId, colDate, colPilot, colDrone, colDuration, colFreq, colLocation, colComment
        );
    }
    private void setupPartColumns() {
        TableColumn<Object, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Object, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Object, String> colBrand = new TableColumn<>("Brand");
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Object, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Object, Integer> colDroneId = new TableColumn<>("Drone ID");
        colDroneId.setCellValueFactory(new PropertyValueFactory<>("droneId"));

        TableColumn<Object, String> colDrone = new TableColumn<>("Drone");
        colDrone.setCellValueFactory(new PropertyValueFactory<>("droneModelName"));

        TableColumn<Object, String> colHours = new TableColumn<>("Hours");
        colHours.setCellValueFactory(new PropertyValueFactory<>("formattedOperatingHours"));

        TableColumn<Object, Boolean> colStatus = new TableColumn<>("Working");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("working"));
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item) {
                        setGraphic(new javafx.scene.shape.Circle(5, javafx.scene.paint.Color.GREEN));
                        setText(" Working");
                    } else {
                        setGraphic(new javafx.scene.shape.Circle(5, javafx.scene.paint.Color.RED));
                        setText(" Not Working");
                    }
                }
            }
        });

        view.getMainTable().getColumns().addAll(
                colId, colName, colBrand, colType, colDroneId, colDrone, colHours, colStatus
        );
    }
    private void handleAddAction() {
        if ("Drones".equals(currentCategory)) {
            DroneDialog dialog = new DroneDialog(null);
            dialog.showAndWait().ifPresent(newDrone -> {
                executeAsyncOperation(Command.ADD_DRONE, newDrone);
            });
        } else if ("Parts".equals(currentCategory)) {
            PartDialog dialog = new PartDialog();
            dialog.showAndWait().ifPresent(newPart -> executeAsyncOperation(Command.ADD_PART, newPart));
        } else  {
            showInfo("Not Supported", "Add Action", currentCategory + " addition is not supported in this version.");
        }
    }

    private void handleRemoveAction() {
        Object selected = view.getMainTable().getSelectionModel().getSelectedItem();
        if (!(selected instanceof DroneDTO drone)) {
            showError("Selection", "No Drone", "Select a drone first.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Drone: " + drone.getModelName());
        alert.setContentText("Are you sure you want to permanently remove this drone from the database?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                executeAsyncOperation(Command.REMOVE_DRONE, drone.getId());
            }
        });
    }

    private void handleUpdateAction() {
        Object selectedItem = view.getMainTable().getSelectionModel().getSelectedItem();
        Command fetchCommand;
        if (selectedItem == null) {
            showError("Selection Required", "No item selected", "Please select an item from the table first.");
            return;
        }

        if (currentCategory.equals("Drones") && selectedItem instanceof DroneDTO drone) {
            DroneDialog dialog = new DroneDialog(drone);
            dialog.showAndWait().ifPresent(updatedDrone -> {
                executeAsyncOperation(Command.UPDATE_DRONE, updatedDrone);
            });
        } else {
            Command updateCommand = switch (currentCategory) {
                case "Pilots" -> Command.UPDATE_PILOT;
                case "Flights" -> Command.UPDATE_FLIGHT;
                case "Parts" -> Command.UPDATE_PART;
                default -> null;
            };
            if (updateCommand != null) executeAsyncOperation(updateCommand, selectedItem);
        }
    }
    private void handleFrequencyAction(Command freqCommand) {
        Object selectedItem = view.getMainTable().getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            executeAsyncOperation(freqCommand, selectedItem);
        } else {
            showError("Selection Required", "No item selected", "Please select an item from the table first.");        }
    }

    private void handlePartAttachment(Command command) {
        Object selectedItem = view.getMainTable().getSelectionModel().getSelectedItem();

        if (!(selectedItem instanceof PartDTO part)) {
            showError("Selection Required", "No part selected", "Please select a part from the table.");
            return;
        }

        if (command == Command.ATTACH_PART_TO_DRONE) {
            if (part.getDroneId() != 0) {
                showError("Already Attached", "Conflict", "This part is already attached to Drone ID: " + part.getDroneId());
                return;
            }

            showDroneSelectionDialog(part);
        }
        else if (command == Command.DETACH_PART_TO_DRONE) {
            if (part.getDroneId() == 0) {
                showInfo("Information", "Not Attached", "This part is not currently attached to any drone.");
                return;
            }
            executeAsyncOperation(Command.DETACH_PART_TO_DRONE, part.getId());
        }
    }

    private void executeAsyncOperation(Command cmd, Object data) {
        Task<ServerResponse> task = new Task<> () {
            @Override
            protected ServerResponse call() throws Exception {
                return client.sendRequest(new ClientRequest(cmd, data));
            }
        };

        task.setOnSucceeded(e -> {
            ServerResponse response = task.getValue();
            if (response != null && response.isSuccess()) {
                showInfo("Success", "Operation successful", "The database has been updated successfully.");
                loadCategoryData(currentCategory);
            } else {
                String errorMsg = (response != null && response.getMessage() != null) ? response.getMessage() : "Action rejected by server.";
                showError("Operation Failed", "The server rejected this action.", "Reason: " + errorMsg);
            }
        });

        task.setOnFailed(e -> handleNetworkError(task.getException()));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
    private void handleLogout() {
        Task<Void> logoutTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                client.sendRequest(new ClientRequest(Command.LOGOUT, null));
                client.disconnect();
                return null;
            }
        };

        logoutTask.setOnScheduled(e -> view.getMainTable().setDisable(true));

        Runnable navigateToLogin = () -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView, primaryStage, client);
            Scene scene = new Scene(loginView, 400, 350);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Drone Operations Center - Login");
        };

        logoutTask.setOnSucceeded(e -> navigateToLogin.run());
        logoutTask.setOnFailed(e -> {
            client.disconnect();
            navigateToLogin.run();
        });

        Thread t = new Thread(logoutTask);
        t.setDaemon(true);
        t.start();
    }

    private void handleFlightAction() {
        Object selected = view.getMainTable().getSelectionModel().getSelectedItem();
        if (!(selected instanceof DroneDTO drone)) {
            showError("Selection Required", "No Drone Selected", "Please select a drone to start a flight.");
            return;
        }

        FlightNowDialog dialog = new FlightNowDialog(this.loggedInPilotId);

        dialog.showAndWait().ifPresent(setup -> {
            startActualFlight(drone, setup);
        });
    }
    private void showError(String title, String header, String content) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void showInfo(String title, String header, String content) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void handleNetworkError(Throwable exception) {
        String message = exception.getMessage() != null ? exception.getMessage() : "";

        if (exception instanceof java.net.ConnectException || message.contains("Connection refused")) {
            showError("Connection Error",
                    "Could not connect to the server.",
                    "Please ensure the backend server is running and check your network connection.");
        } else if (exception instanceof java.net.SocketTimeoutException) {
            showError("Timeout Error",
                    "The server took too long to respond.",
                    "The connection timed out. Please try again later.");
        } else {
            showError("System Error",
                    "An unexpected network error occurred.",
                    "Details: " + (message.isEmpty() ? "Unknown server error" : message));
        }
    }


    private void handleFixPart() {
        Object selected = view.getMainTable().getSelectionModel().getSelectedItem();
        if (!(selected instanceof PartDTO part)) {
            showError("Selection", "No Selection", "Please select a part.");
            return;
        }

        if (part.isWorking()) {
            showInfo("Info", "Already Working", "This part is already in working condition.");
            return;
        }

        executeAsyncOperation(Command.FIX_PART, part.getId());
    }

    private void showLandDialog(DroneDTO drone, FlightSetupDTO setup) {
        long startTime = System.currentTimeMillis();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Flight in Progress");
        alert.setHeaderText(drone.getModelName() + " is currently flying...");
        alert.setContentText("Click OK to land the drone.");
        alert.showAndWait();

        long endTime = System.currentTimeMillis();
        long realElapsedMillis = endTime - startTime;

        long virtualDurationSeconds = (realElapsedMillis * 60) / 1000;

        TextInputDialog commentDialog = new TextInputDialog();
        commentDialog.setTitle("Flight Finished");
        commentDialog.setHeaderText("Enter flight comment:");

        String comment = commentDialog.showAndWait().orElse("No comment provided");
        executeLanding(drone, virtualDurationSeconds, setup, comment);
    }
    private void startActualFlight(DroneDTO drone, FlightSetupDTO setup) {
        Task<ServerResponse> startTask = new Task<>() {
            @Override
            protected ServerResponse call() throws Exception {
                if (!client.isConnected()) client.connect();

                FlightLogDTO startLog = new FlightLogDTO(
                        0,
                        setup.pilotId(), "",
                        drone.getId(), "",
                        LocalDate.now().toString(),
                        0,
                        "",
                        setup.frequency(),
                        setup.location()
                );

                System.out.println("[DEBUG] Sending ADD_FLIGHT request...");
                return client.sendRequest(new ClientRequest(Command.ADD_FLIGHT, startLog));
            }
        };

        startTask.setOnSucceeded(e -> {
            ServerResponse res = startTask.getValue();
            System.out.println("[DEBUG] ADD_FLIGHT Response: success=" + res.isSuccess() + ", message=" + res.getMessage() + ", data=" + res.getData());

            if (res != null && res.isSuccess()) {
                if (res.getData() instanceof FlightLogDTO createdLog) {
                    this.activeFlightLogId = createdLog.getId();
                } else if (res.getData() instanceof Integer createdId) {
                    this.activeFlightLogId = createdId;
                } else if (res.getData() instanceof Double createdIdDouble) {
                    this.activeFlightLogId = createdIdDouble.intValue();
                }

                System.out.println("[DEBUG] Active Flight Log ID captured as: " + this.activeFlightLogId);

                loadCategoryData("Drones");
                showLandDialog(drone, setup);
            } else {
                String errorMsg = (res != null && res.getMessage() != null) ? res.getMessage() : "Unknown error";
                showError("Flight Error", "Failed to start flight", errorMsg);
            }
        });
        startTask.setOnFailed(e -> handleNetworkError(startTask.getException()));

        Thread t = new Thread(startTask);
        t.setDaemon(true);
        t.start();
    }
    private void executeLanding(DroneDTO drone, long duration, FlightSetupDTO setup, String comment) {
        Task<ServerResponse> landTask = new Task<>() {
            @Override
            protected ServerResponse call() throws Exception {
                if (!client.isConnected()) client.connect();

                FlightLogDTO finalLog = new FlightLogDTO(
                        activeFlightLogId,
                        setup.pilotId(), "",
                        drone.getId(), "",
                        LocalDate.now().toString(),
                        duration,
                        comment,
                        setup.frequency(),
                        setup.location()
                );

                System.out.println("[DEBUG] Sending UPDATE_FLIGHT with Log ID: " + activeFlightLogId + ", Comment: " + comment);
                ServerResponse updateRes = client.sendRequest(new ClientRequest(Command.UPDATE_FLIGHT, finalLog));
                System.out.println("[DEBUG] UPDATE_FLIGHT Response: success=" + updateRes.isSuccess() + ", message=" + updateRes.getMessage());

                System.out.println("[DEBUG] Sending LAND_DRONE for Drone ID: " + drone.getId());
                ServerResponse landRes = client.sendRequest(new ClientRequest(Command.LAND_DRONE, drone.getId()));
                System.out.println("[DEBUG] LAND_DRONE Response: success=" + landRes.isSuccess() + ", message=" + landRes.getMessage());

                return landRes;
            }
        };

        landTask.setOnSucceeded(e -> {
            ServerResponse res = landTask.getValue();
            if (res != null && res.isSuccess()) {
                showInfo("Success", "Flight Completed", "Drone landed successfully. " + res.getMessage());
            } else {
                String errorMsg = (res != null && res.getMessage() != null) ? res.getMessage() : "Unknown error";
                showError("Landing Error", "Failed to land drone", errorMsg);
            }

            activeFlightLogId = 0;

            Platform.runLater(() -> {
                loadCategoryData("Drones");
            });
        });

        landTask.setOnFailed(e -> handleNetworkError(landTask.getException()));

        Thread t = new Thread(landTask);
        t.setDaemon(true);
        t.start();
    }
    private void showDroneSelectionDialog(PartDTO part) {
        Task<ServerResponse> fetchTask = new Task<>() {
            @Override
            protected ServerResponse call() throws Exception {
                ServerResponse dronesResponse = client.sendRequest(new ClientRequest(Command.GET_ALL_DRONES, null));
                ServerResponse partsResponse = client.sendRequest(new ClientRequest(Command.GET_ALL_PARTS, null));

                Object[] combinedData = new Object[]{dronesResponse.getData(), partsResponse.getData()};
                return new ServerResponse(true, "Success", combinedData);
            }
        };

        fetchTask.setOnSucceeded(e -> {
            ServerResponse response = fetchTask.getValue();
            if (response != null && response.isSuccess()) {
                Object[] data = (Object[]) response.getData();
                List<DroneDTO> drones = (List<DroneDTO>) data[0];
                List<PartDTO> allParts = (List<PartDTO>) data[1];

                int limit = switch (part.getType().toUpperCase()) {
                    case "MOTOR", "PROPELLERS", "ESC" -> 4;
                    default -> 1;
                };

                List<DroneDTO> availableDrones = drones.stream().filter(drone -> {
                    long currentCount = allParts.stream()
                            .filter(p -> p.getDroneId() == drone.getId() && p.getType().equalsIgnoreCase(part.getType()))
                            .count();
                    return currentCount < limit;
                }).toList();

                if (availableDrones.isEmpty()) {
                    showInfo("No Drones", "Attach Failed", "No available capacity for this part type on any drone.");
                    return;
                }

                javafx.scene.control.Dialog<DroneDTO> dialog = new javafx.scene.control.Dialog<>();
                dialog.setTitle("Attach Part");
                dialog.setHeaderText("Select a Drone for: " + part.getName());

                javafx.scene.control.ComboBox<DroneDTO> comboBox = new javafx.scene.control.ComboBox<>();
                comboBox.getItems().addAll(availableDrones);

                comboBox.setConverter(new javafx.util.StringConverter<>() {
                    @Override
                    public String toString(DroneDTO d) {
                        return (d == null) ? "" : "ID: " + d.getId() + " - " + d.getModelName();
                    }
                    @Override
                    public DroneDTO fromString(String s) { return null; }
                });

                dialog.getDialogPane().setContent(comboBox);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dialog.setResultConverter(button -> (button == ButtonType.OK) ? comboBox.getValue() : null);

                dialog.showAndWait().ifPresent(selectedDrone -> {
                    if (selectedDrone != null) {
                        ActionRequestDTO action = new ActionRequestDTO(part.getId(), selectedDrone.getId());
                        executeAsyncOperation(Command.ATTACH_PART_TO_DRONE, action);
                    }
                });
            } else {
                showError("Error", "Failed to fetch data", response != null ? response.getMessage() : "Unknown error");
            }
        });

        Thread t = new Thread(fetchTask);
        t.setDaemon(true);
        t.start();
    }
}