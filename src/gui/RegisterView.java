package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.ExperienceLevel;

public class RegisterView extends VBox {

    private final TextField nameField;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final PasswordField confirmPasswordField;
    private final ComboBox<ExperienceLevel> experienceComboBox;
    private final Button registerButton;
    private final Button backToLoginButton;
    private final Label statusLabel;

    public RegisterView() {
        attachStylesheetOnceOnScene();

        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("New Pilot Registration");
        titleLabel.getStyleClass().add("label-title");

        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(12);
        formGrid.setVgap(12);

        Label nameLabel = new Label("Full Name:");
        nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        nameField = new TextField();
        nameField.setPromptText("Enter your first and last name.");

        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        usernameField = new TextField();
        usernameField.setPromptText("Set a username");

        Label expLabel = new Label("Experience Level:");
        expLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        experienceComboBox = new ComboBox<>();
        experienceComboBox.getItems().addAll(ExperienceLevel.values());
        experienceComboBox.setPromptText("Choose Experience Level");
        experienceComboBox.setPrefWidth(160);

        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        passwordField = new PasswordField();
        passwordField.setPromptText("Set a password");

        Label confirmPassLabel = new Label("Confirm Password:");
        confirmPassLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Reenter password");

        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(userLabel, 0, 1);
        formGrid.add(usernameField, 1, 1);
        formGrid.add(expLabel, 0, 2);
        formGrid.add(experienceComboBox, 1, 2);
        formGrid.add(passLabel, 0, 3);
        formGrid.add(passwordField, 1, 3);
        formGrid.add(confirmPassLabel, 0, 4);
        formGrid.add(confirmPasswordField, 1, 4);

        registerButton = new Button("Register");
        registerButton.setPrefWidth(200);
        registerButton.getStyleClass().add("button-success");

        backToLoginButton = new Button("Back to Login");
        backToLoginButton.setPrefWidth(200);
        backToLoginButton.getStyleClass().add("button-link");

        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);

        this.getChildren().addAll(titleLabel, formGrid, registerButton, backToLoginButton, statusLabel);
    }
    private void attachStylesheetOnceOnScene() {
        String cssUrl = getClass().getResource("modern.css").toExternalForm();
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && !newScene.getStylesheets().contains(cssUrl)) {
                newScene.getStylesheets().add(cssUrl);
            }
        });
    }

    public String getNameInput() { return nameField.getText().trim(); }
    public String getUsername() { return usernameField.getText().trim(); }
    public ExperienceLevel getExperienceLevel() { return experienceComboBox.getValue(); }
    public String getPassword() { return passwordField.getText(); }
    public String getConfirmPassword() { return confirmPasswordField.getText(); }

    public Button getRegisterButton() { return registerButton; }
    public Button getBackToLoginButton() { return backToLoginButton; }

    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #F38BA8; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #A6E3A1; -fx-font-weight: bold;");
        }
    }
}