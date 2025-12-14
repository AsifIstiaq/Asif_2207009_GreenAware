package controllers;

import java.util.Optional;
import java.util.concurrent.Future;

import dao.IncidentDAO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Incident;
import services.DataLoadingService;

public class IncidentController {

    @FXML private TableView<Incident> incidentsTable;
    @FXML private TableColumn<Incident, Integer> idColumn;
    @FXML private TableColumn<Incident, String> typeColumn;
    @FXML private TableColumn<Incident, String> locationColumn;
    @FXML private TableColumn<Incident, String> dateColumn;
    @FXML private TableColumn<Incident, String> severityColumn;
    @FXML private TableColumn<Incident, String> statusColumn;
    @FXML private TableColumn<Incident, String> categoryColumn;
    @FXML private TableColumn<Incident, String> reporterColumn;

    @FXML private Label recordCountLabel;

    private DataLoadingService dataService;
    private IncidentDAO incidentDAO;

    private ObservableList<Incident> allIncidents;

    @FXML
    public void initialize() {
        dataService = new DataLoadingService();
        incidentDAO = new IncidentDAO();

        setupTable();
        loadData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(
                cell -> cell.getValue().idProperty().asObject()
        );

        typeColumn.setCellValueFactory(
                cell -> cell.getValue().incidentTypeProperty()
        );

        locationColumn.setCellValueFactory(
                cell -> cell.getValue().locationProperty()
        );

        dateColumn.setCellValueFactory(
                cell -> cell.getValue().dateProperty()
        );

        severityColumn.setCellValueFactory(
                cell -> cell.getValue().severityProperty()
        );

        statusColumn.setCellValueFactory(
                cell -> cell.getValue().statusProperty()
        );

        reporterColumn.setCellValueFactory(
                cell -> cell.getValue().reporterNameProperty()
        );

    }

    private void loadData() {
        Future<ObservableList<Incident>> incidentsFuture = dataService.loadIncidents();

        new Thread(() -> {
            try {
                allIncidents = incidentsFuture.get();

                Platform.runLater(() -> {
                    incidentsTable.setItems(allIncidents);
                    updateRecordCount();
                });

            } catch (Exception e) {
                Platform.runLater(() -> showError("Error loading incidents: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void addIncident() {
        showIncidentDialog(null);
    }

    @FXML
    public void editIncident() {
        Incident selected = incidentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an incident to edit");
            return;
        }
        showIncidentDialog(selected);
    }

    @FXML
    public void deleteIncident() {
        Incident selected = incidentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an incident to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Incident");
        confirm.setContentText("Are you sure you want to delete this incident?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    incidentDAO.deleteIncident(selected.getId());
                    Platform.runLater(() -> {
                        allIncidents.remove(selected);
                        showSuccess("Incident deleted successfully");
                        updateRecordCount();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Error deleting incident: " + e.getMessage()));
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void showIncidentDialog(Incident incident) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/greenaware/incident_form.fxml")
            );
            javafx.scene.Parent root = loader.load();

            IncidentFormController controller = loader.getController();
            controller.setIncident(incident);

            Stage dialog = new Stage();
            dialog.setTitle(incident == null ? "Add Incident" : "Edit Incident");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new javafx.scene.Scene(root));

            dialog.getScene().getStylesheets().add(
                    getClass().getResource("/styles.css").toExternalForm()
            );

            dialog.showAndWait();

            if (controller.isSaved()) {
                loadData();
                showSuccess(incident == null ? "Incident created successfully" : "Incident updated successfully");
            }

        } catch (Exception e) {
            showError("Error opening incident form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateRecordCount() {
        recordCountLabel.setText("Total: " + incidentsTable.getItems().size() + " incidents");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Incident Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Selection Required");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Operation Successful");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
