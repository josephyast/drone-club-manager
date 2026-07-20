package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends VBox {

    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Label statusLabel;
    private final Button registerFormButton;

    public LoginView() {
        attachStylesheetOnceOnScene();

        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Drone Management System");
        titleLabel.getStyleClass().add("label-title");

        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(12);
        formGrid.setVgap(12);

        registerFormButton = new Button("Register");
        registerFormButton.getStyleClass().add("button-link");

        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefWidth(200);

        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(200);

        formGrid.add(userLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(passLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);

        loginButton = new Button("Login");
        loginButton.setPrefWidth(200);
        loginButton.getStyleClass().add("button-accent");

        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));


        this.getChildren().addAll(titleLabel, formGrid, loginButton, registerFormButton, statusLabel);    }

    private void attachStylesheetOnceOnScene() {
        String cssUrl = getClass().getResource("modern.css").toExternalForm();
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && !newScene.getStylesheets().contains(cssUrl)) {
                newScene.getStylesheets().add(cssUrl);
            }
        });
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Button getRegisterFormButton() {
        return registerFormButton;
    }



    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #F38BA8; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #A6E3A1; -fx-font-weight: bold;");
        }
    }
}