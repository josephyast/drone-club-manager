package gui;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.DroneStatus;
import network.dto.DroneDTO;
import model.DroneType;

import java.time.LocalDate;

public class DroneDialog extends Dialog<DroneDTO> {
    public DroneDialog(DroneDTO existingDrone) {
        setTitle(existingDrone == null ? "Add New Drone" : "Edit Drone Details");

        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().getStylesheets().add(getClass().getResource("modern.css").toExternalForm());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        TextField modelField = new TextField();
        ComboBox<DroneType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(DroneType.values());
        TextField weightField = new TextField();
        CheckBox funcCheckBox = new CheckBox("Functional");
        DatePicker buildDatePicker = new DatePicker(LocalDate.now());
        TextField freqField = new TextField("0.0");
        Button btnMaintenance = new Button("Mark Maintenance Today");
        Label lblLastMaint = new Label(existingDrone != null ? existingDrone.getLastMaintenanceDate() : "N/A");

        if (existingDrone != null) {
            modelField.setText(existingDrone.getModelName());
            typeBox.setValue(DroneType.valueOf(existingDrone.getType()));
            weightField.setText(String.valueOf(existingDrone.getWeight()));
            buildDatePicker.setValue(LocalDate.parse(existingDrone.getBuildDate()));
        }

        btnMaintenance.setOnAction(e -> lblLastMaint.setText(LocalDate.now().toString()));

        grid.add(new Label("Model:"), 0, 0); grid.add(modelField, 1, 0);
        grid.add(new Label("Type:"), 0, 1); grid.add(typeBox, 1, 1);
        grid.add(new Label("Weight:"), 0, 2); grid.add(weightField, 1, 2);
        grid.add(new Label("Status:"), 0, 3); grid.add(funcCheckBox, 1, 3);
        grid.add(new Label("Build Date:"), 0, 4); grid.add(buildDatePicker, 1, 4);
        grid.add(new Label("Last Maintenance:"), 0, 5); grid.add(lblLastMaint, 1, 5);
        grid.add(btnMaintenance, 2, 5);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String status = funcCheckBox.isSelected() ?
                            DroneStatus.AVAILABLE.name() :
                            DroneStatus.MAINTENANCE.name();
                    return new DroneDTO(
                            existingDrone == null ? 0 : existingDrone.getId(),
                            modelField.getText(),
                            typeBox.getValue().name(),
                            Double.parseDouble(weightField.getText()),
                            status,
                            buildDatePicker.getValue().toString(),
                            lblLastMaint.getText(),
                            existingDrone == null ? 0 : existingDrone.getTotalFlightTimeInSeconds(),
                            existingDrone == null ? 0.0 : existingDrone.getCurrentFrequency()
                    );
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
    }
}