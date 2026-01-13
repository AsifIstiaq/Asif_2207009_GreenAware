package controllers;

import dao.WorkerDAO_Firebase;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.Worker;
import utils.SceneUtil;

public class WorkerLoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private WorkerDAO_Firebase workerDAO = new WorkerDAO_Firebase();

    @FXML
    public void handleLogin() {
        messageLabel.setText("");

        String username = emailField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            Worker worker = workerDAO.authenticateWorker(username, password);

            if (worker != null) {
                messageLabel.setText("Login successful! Redirecting...");
                messageLabel.setStyle("-fx-text-fill: green;");

                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            try {
                                SceneUtil.switchScene(
                                        emailField,
                                        "worker_dashboard.fxml",
                                        "Worker Dashboard - " + worker.getName(),
                                        (WorkerDashboardController controller) -> {
                                            controller.setWorker(worker);
                                        }
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Invalid username or password");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            messageLabel.setText("Login failed: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegister() {
        SceneUtil.switchRoot(emailField, "worker_register.fxml");
    }

    @FXML
    public void handleBackToMain() {
        SceneUtil.switchRoot(emailField, "home.fxml");
    }
}
