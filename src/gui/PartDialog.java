package gui;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import network.dto.PartDTO;

public class PartDialog extends Dialog<PartDTO> {
    public PartDialog() {
        setTitle("New Part");
        setHeaderText("Enter part details");

        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().getStylesheets().add(getClass().getResource("modern.css").toExternalForm());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField nameField = new TextField();
        TextField brandField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("FRAME", "FC", "ESC", "MOTOR", "PROPELLERS", "BATTERY", "VTX", "CAMERA","PDB","VIDEOANTENNA","TRANSMITTER","GOOGLES");

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Brand:"), brandField);
        grid.addRow(2, new Label("Type:"), typeBox);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new PartDTO(0, nameField.getText(), brandField.getText(), typeBox.getValue(), 0, null, 0, true);
            }
            return null;
        });
    }
}