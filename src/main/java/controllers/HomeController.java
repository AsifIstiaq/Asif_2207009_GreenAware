package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    public void handleUserLogin() {
        loadScene("/com/example/greenaware/user_login.fxml", "GreenAware - User Login");
    }

    @FXML
    public void handleWorkerLogin() {
        loadScene("/com/example/greenaware/worker_login.fxml", "GreenAware - Worker Login");
    }

    @FXML
    public void handleAdminLogin() {
        loadScene("/com/example/greenaware/admin_login.fxml", "GreenAware - Admin Login");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            Stage stage = (Stage) javafx.stage.Stage.getWindows().stream()
                    .filter(javafx.stage.Window::isShowing)
                    .findFirst()
                    .orElse(null);

            if (stage != null) {
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
