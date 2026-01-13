package com.example.greenaware;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.FirebaseService;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Initializing Firebase...");
        FirebaseService.initialize();
        System.out.println("Firebase initialized successfully!");

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("GreenAware - Community Waste & Pollution Reporting Manager");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() {
        try {
            FirebaseService.shutdown();
            System.out.println("Firebase shut down successfully");
        } catch (Exception e) {
            System.err.println("Error shutting down Firebase: " + e.getMessage());
        }
        System.out.println("Application closed successfully");
    }
}
