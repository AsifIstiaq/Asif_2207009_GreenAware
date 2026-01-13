package controllers;

import dao.UserDAO_Firebase;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import utils.SceneUtil;
import utils.Session;

public class UserLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserDAO_Firebase userDAO = new UserDAO_Firebase();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        try {
            User user = userDAO.validateUserLogin(username, password);
            if (user != null) {
                Session.setUser(user);
                loadUserDashboard();
            }
            else {
                showError("Invalid username or password");
            }
        } catch (Exception e) {
            showError("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegisterLink() {
        SceneUtil.switchRoot(usernameField, "user_register.fxml");
    }

    @FXML
    public void handleBack() {
        SceneUtil.switchRoot(usernameField, "home.fxml");
    }

    private void loadUserDashboard() {
        SceneUtil.switchRoot(usernameField, "user_dashboard.fxml");
    }

    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
}
