package controller;

import gui.LoginView;
import gui.RegisterView;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.ExperienceLevel;
import network.DroneClient;
import network.ClientRequest;
import network.ServerResponse;
import network.Command;

public class RegisterController {

    private final RegisterView view;
    private final Stage primaryStage;
    private final DroneClient client;

    public RegisterController(RegisterView view, Stage primaryStage, DroneClient client) {
        this.view = view;
        this.primaryStage = primaryStage;
        this.client = client;

        this.view.getRegisterButton().setOnAction(e -> handleRegister());
        this.view.getBackToLoginButton().setOnAction(e -> switchToLogin());
    }

    private void handleRegister() {
        String name = view.getNameInput();
        String username = view.getUsername();
        ExperienceLevel expLevel = view.getExperienceLevel();
        String password = view.getPassword();
        String confirmPassword = view.getConfirmPassword();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            view.setStatusMessage("Please fill in all the fields", true);
            return;
        }

        if (expLevel == null) {
            view.setStatusMessage("Please select an experience level.", true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.setStatusMessage("The passwords do not match", true);
            return;
        }

        view.setStatusMessage("Creating record", false);
        view.getRegisterButton().setDisable(true);;

        Task<ServerResponse> registerTask = new Task<>() {
            @Override
            protected ServerResponse call() throws Exception {
                client.connect();

                network.dto.PilotDTO registerData = new network.dto.PilotDTO(
                        0,
                        name,
                        username,
                        password,
                        expLevel.name(),
                        0L,
                        0.0,
                        true
                );

                ClientRequest request = new ClientRequest(Command.REGISTER, registerData);
                return client.sendRequest(request);
            }
        };

        registerTask.setOnSucceeded(e -> {
            view.getRegisterButton().setDisable(false);
            ServerResponse response = registerTask.getValue();

            if (response != null && response.isSuccess()) {
                view.setStatusMessage("Registration successful You are being redirected to the login screen", false);
                javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                delay.setOnFinished(event -> switchToLogin());
                delay.play();
            } else {
                String errMsg = (response != null) ? response.getMessage() : "Registration failed";
                view.setStatusMessage(errMsg, true);
                client.disconnect();
            }
        });

        registerTask.setOnFailed(e -> {
            view.getRegisterButton().setDisable(false);
            view.setStatusMessage("Server connection error", true);
            client.disconnect();
        });

        new Thread(registerTask).start();
    }

    private void switchToLogin() {
        LoginView loginView = new LoginView();
        new LoginController(loginView, primaryStage, client);
        Scene scene = new Scene(loginView, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drone Operations Center - Login");
    }
}