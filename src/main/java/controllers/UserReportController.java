package controllers;

import java.io.File;
import java.time.LocalDate;

import dao.ReportDAO_Firebase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.User;
import utils.Session;

public class UserReportController {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField locationField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> severityCombo;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imagePathField;
    @FXML private TextField reporterNameField;
    @FXML private TextField reporterEmailField;
    @FXML private Label statusLabel;

    private User currentUser;
    private final ReportDAO_Firebase reportDAO = new ReportDAO_Firebase();

    @FXML
    public void initialize() {
        currentUser = Session.getUser();
        if (currentUser != null) {
            reporterNameField.setText(currentUser.getFullName());
            reporterEmailField.setText(currentUser.getEmail());
        } else {
            showError("User session missing. Please log in again.");
            return;
        }

        try {
//            categoryCombo.setItems(reportDAO.getAllCategories());
            categoryCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                    "Air pollution", "Noise pollution", "Water pollution"
            ));
        } catch (Exception e) {
            showError("Error loading categories: " + e.getMessage());
        }

        severityCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "LOW", "MEDIUM", "HIGH"
        ));

        datePicker.setValue(LocalDate.now());
    }

    @FXML
    public void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(imagePathField.getScene().getWindow());
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void handleSubmitReport() {
        if (currentUser == null) {
            showError("User session expired. Please log in again.");
            return;
        }
        String category = categoryCombo.getValue();
        String location = locationField.getText().trim();
        LocalDate date = datePicker.getValue();
        String severity = severityCombo.getValue();
        String description = descriptionArea.getText().trim();
        String imagePath = imagePathField.getText().trim();

        if (category == null || location.isEmpty() || date == null ||
                severity == null || description.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        try {
            reportDAO.addReport(
                    currentUser.getId(),
                    category,
                    null,
                    location,
                    date.toString(),
                    severity,
                    description,
                    imagePath.isEmpty() ? null : imagePath
            );

            showSuccess("Report submitted successfully!");

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> handleBack());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showError("Error submitting report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/greenaware/user_dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Error returning to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }
}
