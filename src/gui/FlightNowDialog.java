package gui;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import network.dto.FlightSetupDTO;

public class FlightNowDialog extends Dialog<FlightSetupDTO> {

    public FlightNowDialog() {
        setTitle("Flight Setup");
        setHeaderText("Enter flight details before takeoff");

        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().getStylesheets().add(getClass().getResource("modern.css").toExternalForm());

        ButtonType okButtonType = new ButtonType("Takeoff", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        ComboBox<Double> freqBox = new ComboBox<>();
        TextField locField = new TextField();
        freqBox.getItems().addAll(1630.0, 1660.0, 1690.0, 1790.0, 1810.0, 1830.0, 2030.0, 2050.0,
                2070.0, 2090.0, 2205.0, 2215.0, 2225.0, 2240.0, 2260.0, 2280.0,
                2315.0, 2340.0, 2365.0, 2410.0, 2430.0, 2450.0, 2470.0, 2490.0, 2505.0);
        locField.setPromptText("e.g. Duisburg Base");

        grid.add(new Label("Frequency:"), 0, 0); grid.add(freqBox, 1, 0);
        grid.add(new Label("Location:"), 0, 1); grid.add(locField, 1, 1);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                Double selectedFreq = freqBox.getValue();
                if (selectedFreq == null) selectedFreq = 0.0;

                return new FlightSetupDTO(0, selectedFreq, locField.getText());
            }
            return null;
        });
    }
}