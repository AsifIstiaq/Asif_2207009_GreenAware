package controllers;

import java.time.LocalDate;

import dao.ActionDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Action;

public class ActionFormController {

    @FXML private TextField incidentIdField;
    @FXML private TextField workerNameField;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextArea actionNoteArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ActionDAO actionDAO;
    private Action currentAction;
    private boolean saved = false;

    @FXML
    public void initialize() {
        actionDAO = new ActionDAO();

        statusCombo.setItems(FXCollections.observableArrayList(
                "PENDING", "IN_PROGRESS", "COMPLETED"
        ));
        statusCombo.setValue("PENDING");

        saveButton.setOnAction(e -> saveAction());
        cancelButton.setOnAction(e -> cancel());

        deadlinePicker.setValue(LocalDate.now().plusDays(1));
    }

    public void setAction(Action action) {
        this.currentAction = action;

        if (action != null) {
            incidentIdField.setText(String.valueOf(action.getIncidentId()));
            incidentIdField.setDisable(true);
            deadlinePicker.setValue(LocalDate.parse(action.getDeadline()));
            statusCombo.setValue(action.getStatus());
            actionNoteArea.setText(action.getActionNote());
            workerNameField.setText(action.getWorkerName() != null ? action.getWorkerName() : "");
        }
    }

    private void saveAction() {
        if (incidentIdField.getText().trim().isEmpty()) {
            showError("Incident ID is required");
            return;
        }

        int incidentId;
        try {
            incidentId = Integer.parseInt(incidentIdField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Incident ID must be a number");
            return;
        }

        if (workerNameField.getText().trim().isEmpty()) {
            showError("Worker name is required");
            return;
        }
        if (deadlinePicker.getValue() == null) {
            showError("Deadline is required");
            return;
        }
        if (statusCombo.getValue() == null) {
            showError("Status is required");
            return;
        }
        if (actionNoteArea.getText().trim().isEmpty()) {
            showError("Action note is required");
            return;
        }

        try {
            String workerName = workerNameField.getText().trim();

            if (currentAction == null) {
                Action newAction = new Action(
                        0,
                        incidentId,
                        0,
                        actionNoteArea.getText().trim(),
                        deadlinePicker.getValue().toString(),
                        statusCombo.getValue(),
                        "",
                        null,
                        null,
                        workerName
                );
                actionDAO.insertAction(newAction);
            } else {
                currentAction.setWorkerName(workerName);
                currentAction.setActionNote(actionNoteArea.getText().trim());
                currentAction.setDeadline(deadlinePicker.getValue().toString());
                currentAction.setStatus(statusCombo.getValue());

                if ("COMPLETED".equals(statusCombo.getValue()) && currentAction.getCompletedDate() == null) {
                    currentAction.setCompletedDate(LocalDate.now().toString());
                }

                actionDAO.updateAction(currentAction);
            }

            saved = true;
            closeDialog();

        } catch (Exception e) {
            showError("Error saving action: " + e.getMessage());
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
