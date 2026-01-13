package controllers;

import dao.ActionDAO_Firebase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import models.Action;
import models.Worker;
import utils.SceneUtil;

public class WorkerDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalAssignmentsLabel;
    @FXML private Label pendingLabel;
    @FXML private Label inProgressLabel;
    @FXML private Label completedLabel;
    @FXML private TableView<Action> assignmentsTable;
    @FXML private TableColumn<Action, Integer> idColumn;
    @FXML private TableColumn<Action, String> locationColumn;
    @FXML private TableColumn<Action, String> actionNoteColumn;
    @FXML private TableColumn<Action, String> deadlineColumn;
    @FXML private TableColumn<Action, String> statusColumn;

    private Worker currentWorker;
    private ActionDAO_Firebase actionDAO = new ActionDAO_Firebase();
    private ObservableList<Action> assignments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    locationColumn.setCellValueFactory(
            cell -> cell.getValue().locationProperty()
    );

    actionNoteColumn.setCellValueFactory(
            cell -> cell.getValue().actionNoteProperty()
    );

    deadlineColumn.setCellValueFactory(
            cell -> cell.getValue().deadlineProperty()
    );

    statusColumn.setCellValueFactory(
            cell -> cell.getValue().statusProperty()
    );

    assignmentsTable.setItems(assignments);
    }

    public void setWorker(Worker worker) {
        this.currentWorker = worker;
        welcomeLabel.setText("Welcome, " + worker.getName() + "!");
        loadAssignments();
        updateStats();
    }

    private void loadAssignments() {
        try {
            assignments.clear();
            assignments.addAll(actionDAO.getActionsByWorkerId(currentWorker.getId()));
        } catch (Exception e) {
            showError("Failed to load assignments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStats() {
        int total = assignments.size();
        long pending = assignments.stream().filter(a -> a.getStatus().equals("PENDING")).count();
        long inProgress = assignments.stream().filter(a -> a.getStatus().equals("IN_PROGRESS")).count();
        long completed = assignments.stream().filter(a -> a.getStatus().equals("COMPLETED")).count();

        totalAssignmentsLabel.setText(String.valueOf(total));
        pendingLabel.setText(String.valueOf(pending));
        inProgressLabel.setText(String.valueOf(inProgress));
        completedLabel.setText(String.valueOf(completed));
    }

    @FXML
    public void handleViewDetails() {
        Action selected = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an assignment to view details");
            return;
        }

        try {
            String location = selected.getLocation();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Assignment Details");
            alert.setContentText(
                    "Location: " + (location != null ? location : "N/A") + "\n" +
                            "Description: " + selected.getActionNote() + "\n" +
                            "Deadline: " + selected.getDeadline() + "\n" +
                            "Status: " + selected.getStatus()
            );
            alert.showAndWait();
        } catch (Exception e) {
            showError("Failed to load details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSubmitProgress() {
        Action selected = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an assignment to submit progress");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/greenaware/work_progress_form.fxml"));
            Parent root = loader.load();

            WorkProgressFormController controller = loader.getController();
            controller.setActionAndWorker(selected, currentWorker);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Submit Work Progress");
            stage.showAndWait();

            loadAssignments();
            updateStats();
        } catch (Exception e) {
            showError("Failed to open progress form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRefresh() {
        loadAssignments();
        updateStats();
    }

    @FXML
    public void handleLogout() {
        SceneUtil.switchRoot(welcomeLabel, "worker_login.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
