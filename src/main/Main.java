package main;

import controller.LoginController;
import gui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.DroneClient;

public class Main extends Application {

    private DroneClient client;

    @Override
    public void start(Stage primaryStage) {
        client = new DroneClient();
        LoginView loginView = new LoginView();

        new LoginController(loginView, primaryStage, client);

        Scene scene = new Scene(loginView, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drone Operations Center - Login");

        primaryStage.setOnCloseRequest(e -> {
            if (client != null) {
                client.disconnect();
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}