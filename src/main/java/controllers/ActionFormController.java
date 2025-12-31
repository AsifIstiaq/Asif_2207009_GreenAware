package controllers;

import java.time.LocalDate;

import dao.ActionDAO_Firebase;
import dao.WorkerDAO_Firebase;
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
import models.Worker;

public class ActionFormController {

    @FXML private ComboBox<Worker> workerCombo;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField locationField;
    @FXML private TextArea actionNoteArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ActionDAO_Firebase actionDAO;
    private WorkerDAO_Firebase workerDAO;
    private Action currentAction;
    private boolean saved = false;

    @FXML
    public void initialize() {
        actionDAO = new ActionDAO_Firebase();
        workerDAO = new WorkerDAO_Firebase();

        statusCombo.setItems(FXCollections.observableArrayList(
                "PENDING", "IN_PROGRESS", "COMPLETED"
        ));
        statusCombo.setValue("PENDING");

        loadWorkers();

        saveButton.setOnAction(e -> saveAction());
        cancelButton.setOnAction(e -> cancel());

        deadlinePicker.setValue(LocalDate.now().plusDays(1));
    }

    private void loadWorkers() {
        try {
            workerCombo.setItems(FXCollections.observableArrayList(workerDAO.getAllWorkers()));

            workerCombo.setButtonCell(new javafx.scene.control.ListCell<Worker>() {
                @Override
                protected void updateItem(Worker worker, boolean empty) {
                    super.updateItem(worker, empty);
                    if (empty || worker == null) {
                        setText(null);
                    } else {
                        setText(worker.getName() + " - " + worker.getSpecialization());
                    }
                }
            });

            workerCombo.setCellFactory(lv -> new javafx.scene.control.ListCell<Worker>() {
                @Override
                protected void updateItem(Worker worker, boolean empty) {
                    super.updateItem(worker, empty);
                    if (empty || worker == null) {
                        setText(null);
                    } else {
                        setText(worker.getName() + " - " + worker.getSpecialization());
                    }
                }
            });
        } catch (Exception e) {
            showError("Failed to load workers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setAction(Action action) {
        this.currentAction = action;

        if (action != null) {
            deadlinePicker.setValue(LocalDate.parse(action.getDeadline()));
            locationField.setText(action.getLocation());
            statusCombo.setValue(action.getStatus());
            actionNoteArea.setText(action.getActionNote());
            //workerNameField.setText(action.getWorkerName() != null ? action.getWorkerName() : "");
            if (action.getWorkerId() > 0) {
                workerCombo.getItems().stream()
                        .filter(w -> w.getId() == action.getWorkerId())
                        .findFirst()
                        .ifPresent(workerCombo::setValue);
            }
        }
    }

    private void saveAction() {

        Worker selectedWorker = workerCombo.getValue();
        if (selectedWorker == null) {
            showError("Please select a worker");
            return;
        }

        if (locationField.getText().trim().isEmpty()) {
            showError("Location is required");
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

            if (currentAction == null) {
                Action newAction = new Action(
                        0,
                        selectedWorker.getId(),
                        actionNoteArea.getText().trim(),
                        deadlinePicker.getValue().toString(),
                        statusCombo.getValue(),
                        "",
                        null,
                        null,
                        selectedWorker.getName(),
                        locationField.getText().trim(),
                        null
                );
                actionDAO.insertAction(newAction);
            } else {
                currentAction.setWorkerId(selectedWorker.getId());
                currentAction.setWorkerName(selectedWorker.getName());
                currentAction.setLocation(locationField.getText().trim());
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
