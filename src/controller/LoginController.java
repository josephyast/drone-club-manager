package controller;

import gui.LoginView;
import gui.MainDashboardView;
import gui.RegisterView;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.DroneClient;
import network.ClientRequest;
import network.ServerResponse;
import network.Command;

public class LoginController {

    private final LoginView view;
    private final Stage primaryStage;
    private final DroneClient client;

    public LoginController(LoginView view, Stage primaryStage, DroneClient client) {
        this.view = view;
        this.primaryStage = primaryStage;
        this.client = client;

        this.view.getRegisterFormButton().setOnAction(e -> switchToRegister());
        this.view.getLoginButton().setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.setStatusMessage("Username or password cannot be empty", true);
            return;
        }

        view.setStatusMessage("Logging in, please wait", false);
        view.getLoginButton().setDisable(true);

        Task<ServerResponse> loginTask = new Task<>() {
            @Override
            protected ServerResponse call() throws Exception {
                client.connect();

                network.dto.PilotDTO loginData = new network.dto.PilotDTO(
                        0,
                        "",
                        username,
                        password,
                        "",
                        0L,
                        0.0,
                        true
                );
                ClientRequest request = new ClientRequest(Command.LOGIN, loginData);
                return client.sendRequest(request);
            }
        };


        loginTask.setOnSucceeded(workerStateEvent -> {
            view.getLoginButton().setDisable(false);
            ServerResponse response = loginTask.getValue();

            if (response != null && response.isSuccess()) {
                view.setStatusMessage("Login successful You are being redirected", false);
                switchToDashboard();
            } else {
                String errMsg = (response != null) ? response.getMessage() : "Incorrect username or password!";
                view.setStatusMessage(errMsg, true);
                client.disconnect();
            }
        });

        loginTask.setOnFailed(workerStateEvent -> {
            view.getLoginButton().setDisable(false);
            Throwable exception = loginTask.getException();
            view.setStatusMessage("Server connection error: " + exception.getMessage(), true);
        });

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void switchToRegister() {
        RegisterView registerView = new RegisterView();
        new RegisterController(registerView, primaryStage, client);
        Scene scene = new Scene(registerView, 400, 480);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drone Operations Center - Register");
    }

    private void switchToDashboard() {
        MainDashboardView dashboardView = new MainDashboardView();
        new DashboardController(dashboardView, primaryStage, client);

        Scene scene = new Scene(dashboardView, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drone Operations Center - Main Panel");
    }
}