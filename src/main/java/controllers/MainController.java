package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    private Node currentView;

    @FXML
    public void initialize() {
        showDashboard();
    }

    @FXML
    public void showDashboard() {
        loadView("/com/example/greenaware/admin_dashboard.fxml", "Admin Dashboard");
    }

    @FXML
    public void showIncidents() {
        loadView("/com/example/greenaware/incidents.fxml", "Incidents");
    }

    @FXML
    public void showActions() {
        loadView("/com/example/greenaware/actions.fxml", "Actions");
    }

    @FXML
    public void showUserReports() {
        loadView("/com/example/greenaware/user_reports.fxml", "User Reports");
    }

    @FXML
    public void showExportImport() {
    }

    @FXML
    public void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/greenaware/home.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("GreenAware - Community Waste Management");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            currentView = view;

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText("Failed to load view");
            alert.setContentText("Error loading " + viewName + " view:\n" + e.getMessage());
            alert.show();
        }
    }
}
