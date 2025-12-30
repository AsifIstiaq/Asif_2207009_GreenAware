package controllers;

import java.util.Optional;
import java.util.concurrent.Future;

import dao.ActionDAO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Action;
import services.DataLoadingService;

public class ActionController {

    @FXML private TableView<Action> actionsTable;
    @FXML private TableColumn<Action, Integer> idColumn;
    @FXML private TableColumn<Action, String> workerColumn;
    @FXML private TableColumn<Action, String> locationColumn;
    @FXML private TableColumn<Action, String> actionNoteColumn;
    @FXML private TableColumn<Action, String> deadlineColumn;
    @FXML private TableColumn<Action, String> statusColumn;
    @FXML private TableColumn<Action, String> completedDateColumn;
    @FXML private Label statusLabel;

    private DataLoadingService dataService;
    private ActionDAO actionDAO;

    @FXML
    public void initialize() {
        dataService = new DataLoadingService();
        actionDAO = new ActionDAO();

        setupTable();
        refreshActions();
    }

    private void setupTable() {
            idColumn.setCellValueFactory(
                    cell -> cell.getValue().idProperty().asObject()
            );

            workerColumn.setCellValueFactory(
                    cell -> cell.getValue().workerNameProperty()
            );

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

            completedDateColumn.setCellValueFactory(
                    cell -> cell.getValue().completedDateProperty()
            );
    }

    @FXML
    public void refreshActions() {
        Future<ObservableList<Action>> future = dataService.loadActions();

        new Thread(() -> {
            try {
                ObservableList<Action> actions = future.get();
                Platform.runLater(() -> {
                    actionsTable.setItems(actions);
                    updateStatus();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error loading actions: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void addAction() {
        showActionDialog(null);
    }

    @FXML
    public void editAction() {
        Action selected = actionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an action to edit");
            return;
        }
        showActionDialog(selected);
    }

    private void showActionDialog(Action action) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/greenaware/action_form.fxml")
            );
            javafx.scene.Parent root = loader.load();

            ActionFormController controller = loader.getController();
            controller.setAction(action);

            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.setTitle(action == null ? "Add Action" : "Edit Action");
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.setScene(new javafx.scene.Scene(root));

            dialog.getScene().getStylesheets().add(
                    getClass().getResource("/styles.css").toExternalForm()
            );

            dialog.showAndWait();

            if (controller.isSaved()) {
                refreshActions();
                showSuccess(action == null ? "Action created successfully" : "Action updated successfully");
            }

        } catch (Exception e) {
            showError("Error opening action form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void markCompleted() {
        Action selected = actionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an action to mark as completed");
            return;
        }

        selected.setStatus("COMPLETED");
        selected.setCompletedDate(java.time.LocalDate.now().toString());

        new Thread(() -> {
            try {
                actionDAO.updateAction(selected);
                Platform.runLater(() -> {
                    actionsTable.refresh();
                    showSuccess("Action marked as completed");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error updating action: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void deleteAction() {
        Action selected = actionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an action to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete this action?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    actionDAO.deleteAction(selected.getId());
                    Platform.runLater(() -> {
                        actionsTable.getItems().remove(selected);
                        showSuccess("Action deleted");
                        updateStatus();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Error: " + e.getMessage()));
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void updateStatus() {
        statusLabel.setText("Total: " + actionsTable.getItems().size() + " actions");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
