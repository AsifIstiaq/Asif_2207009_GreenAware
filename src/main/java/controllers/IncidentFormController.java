package controllers;

import java.time.LocalDate;

import dao.IncidentDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Incident;

public class IncidentFormController {

    @FXML private TextField incidentTypeField;
    @FXML private TextField locationField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> severityCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField reporterNameField;
    @FXML private TextField reporterContactField;
    @FXML private TextField imagePathField;
    @FXML private TextArea descriptionArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private IncidentDAO incidentDAO;
    private Incident currentIncident;
    private boolean saved = false;

    @FXML
    public void initialize() {
        incidentDAO = new IncidentDAO();

        severityCombo.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));

        statusCombo.setItems(FXCollections.observableArrayList(
                "PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED"
        ));
        statusCombo.setValue("PENDING");

        saveButton.setOnAction(e -> saveIncident());
        cancelButton.setOnAction(e -> cancel());
    }

    public void setIncident(Incident incident) {
        this.currentIncident = incident;

        if (incident != null) {
            incidentTypeField.setText(incident.getIncidentType());
            locationField.setText(incident.getLocation());
            datePicker.setValue(LocalDate.parse(incident.getDate()));
            severityCombo.setValue(incident.getSeverity());
            statusCombo.setValue(incident.getStatus());
            reporterNameField.setText(incident.getReporterName());
            reporterContactField.setText(incident.getReporterContact());
            imagePathField.setText(incident.getImagePath());
            descriptionArea.setText(incident.getDescription());
        } else {
            datePicker.setValue(LocalDate.now());
        }
    }

    private void saveIncident() {
        if (incidentTypeField.getText().trim().isEmpty()) {
            showError("Incident type is required");
            return;
        }
        if (locationField.getText().trim().isEmpty()) {
            showError("Location is required");
            return;
        }
        if (datePicker.getValue() == null) {
            showError("Date is required");
            return;
        }
        if (severityCombo.getValue() == null) {
            showError("Severity is required");
            return;
        }
        if (statusCombo.getValue() == null) {
            showError("Status is required");
            return;
        }

        try {
            if (currentIncident == null) {
                Incident newIncident = new Incident(
                        0,
                        incidentTypeField.getText().trim(),
                        locationField.getText().trim(),
                        datePicker.getValue().toString(),
                        severityCombo.getValue(),
                        reporterNameField.getText().trim(),
                        reporterContactField.getText().trim(),
                        imagePathField.getText().trim(),
                        statusCombo.getValue(),
                        descriptionArea.getText().trim(),
                        0,
                        null,
                        null
                );
                incidentDAO.insertIncident(newIncident);
            } else {
                currentIncident.setIncidentType(incidentTypeField.getText().trim());
                currentIncident.setLocation(locationField.getText().trim());
                currentIncident.setDate(datePicker.getValue().toString());
                currentIncident.setSeverity(severityCombo.getValue());
                currentIncident.setStatus(statusCombo.getValue());
                currentIncident.setReporterName(reporterNameField.getText().trim());
                currentIncident.setReporterContact(reporterContactField.getText().trim());
                currentIncident.setImagePath(imagePathField.getText().trim());
                currentIncident.setDescription(descriptionArea.getText().trim());
                currentIncident.setAssignedWorkerId(0);

                incidentDAO.updateIncident(currentIncident);
            }

            saved = true;
            closeDialog();

        } catch (Exception e) {
            showError("Error saving incident: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public boolean isSaved() {
        return saved;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Input");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
