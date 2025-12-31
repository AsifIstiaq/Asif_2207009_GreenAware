package controllers;

import dao.WorkerDAO_Firebase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Worker;

public class WorkerRegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> specializationCombo;
    @FXML private Label messageLabel;

    private WorkerDAO_Firebase workerDAO = new WorkerDAO_Firebase();

    @FXML
    public void handleRegister() {
        messageLabel.setText("");

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String specialization = specializationCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || phone.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() || specialization == null) {
            messageLabel.setText("Please fill in all fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            messageLabel.setText("Please enter a valid email address");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (username.length() < 3) {
            messageLabel.setText("Username must be at least 3 characters");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            if (workerDAO.emailExists(email)) {
                messageLabel.setText("Email already registered");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (workerDAO.usernameExists(username)) {
                messageLabel.setText("Username already taken");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            Worker worker = new Worker();
            worker.setName(name);
            worker.setEmail(email);
            worker.setUsername(username);
            worker.setPhone(phone);
            worker.setPassword(password);
            worker.setSpecialization(specialization);
            worker.setStatus("AVAILABLE");

            workerDAO.insertWorker(worker);

            messageLabel.setText("Registration successful! Redirecting to login...");
            messageLabel.setStyle("-fx-text-fill: green;");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleBackToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            messageLabel.setText("Registration failed: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/greenaware/worker_login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
