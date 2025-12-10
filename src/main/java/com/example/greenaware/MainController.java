package com.example.greenaware;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    private Node currentView;

    @FXML
    public void initialize() {
    }

    @FXML
    public void showDashboard() {
    }

    @FXML
    public void showIncidents() {
    }

    @FXML
    public void showCategories() {
        loadView("/com/example/greenaware/categories.fxml", "Categories");
    }

    @FXML
    public void showActions() {
        loadView("/com/example/greenaware/actions.fxml", "Actions");
    }

    @FXML
    public void showWasteLogs() {
    }

    @FXML
    public void showEnvironment() {
    }

    @FXML
    public void showEvents() {
    }

    @FXML
    public void showExportImport() {
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
