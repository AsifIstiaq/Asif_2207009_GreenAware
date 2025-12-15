package controllers;

import dao.ReportDAO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Report;

public class UserReportsController {

    @FXML private Label totalReportsLabel;
    @FXML private Label pendingLabel;
    @FXML private Label inProgressLabel;
    @FXML private Label resolvedLabel;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private TableView<Report> reportsTable;
    @FXML private TableColumn<Report, Integer> idColumn;
    @FXML private TableColumn<Report, String> reporterColumn;
    @FXML private TableColumn<Report, String> categoryColumn;
    @FXML private TableColumn<Report, String> locationColumn;
    @FXML private TableColumn<Report, String> dateColumn;
    @FXML private TableColumn<Report, String> severityColumn;
    @FXML private TableColumn<Report, String> statusColumn;
    @FXML private TableColumn<Report, String> submittedColumn;

    private final ReportDAO reportDAO = new ReportDAO();
    private ObservableList<Report> allReports;

    @FXML
    public void initialize() {
        setupTable();
        setupStatusFilter();
        loadDashboardData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(
                cell -> cell.getValue().idProperty().asObject()
        );

        reporterColumn.setCellValueFactory(
                cell -> cell.getValue().reporterNameProperty()
        );

        categoryColumn.setCellValueFactory(
                cell -> cell.getValue().categoryNameProperty()
        );

        locationColumn.setCellValueFactory(
                cell -> cell.getValue().locationProperty()
        );

        dateColumn.setCellValueFactory(
                cell -> cell.getValue().dateReportedProperty()
        );

        severityColumn.setCellValueFactory(
                cell -> cell.getValue().severityProperty()
        );

        statusColumn.setCellValueFactory(
                cell -> cell.getValue().statusProperty()
        );

        submittedColumn.setCellValueFactory(
                cell -> cell.getValue().createdAtProperty()
        );
    }

    private void setupStatusFilter() {
        statusFilterCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "ALL", "PENDING", "IN_PROGRESS", "RESOLVED"
        ));
        statusFilterCombo.setValue("ALL");
        statusFilterCombo.setOnAction(e -> applyFilter());
    }

    private void loadDashboardData() {
        new Thread(() -> {
            try {
                allReports = reportDAO.getAllReports();

                int pending = reportDAO.getPendingReportCount();
                int inProgress = reportDAO.getInProgressReportCount();
                int resolved = reportDAO.getResolvedReportCount();
                int total = pending + inProgress + resolved;

                Platform.runLater(() -> {
                    totalReportsLabel.setText(String.valueOf(total));
                    pendingLabel.setText(String.valueOf(pending));
                    inProgressLabel.setText(String.valueOf(inProgress));
                    resolvedLabel.setText(String.valueOf(resolved));
                    reportsTable.setItems(allReports);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error loading dashboard data: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    private void applyFilter() {
        String filter = statusFilterCombo.getValue();
        if (filter.equals("ALL")) {
            reportsTable.setItems(allReports);
        } else {
            ObservableList<Report> filtered = javafx.collections.FXCollections.observableArrayList();
            for (Report report : allReports) {
                if (report.getStatus().equals(filter)) {
                    filtered.add(report);
                }
            }
            reportsTable.setItems(filtered);
        }
    }

    @FXML
    public void handleMarkInProgress() {
        updateSelectedReportStatus("IN_PROGRESS");
    }

    @FXML
    public void handleMarkResolved() {
        updateSelectedReportStatus("RESOLVED");
    }

    @FXML
    public void handleMarkPending() {
        updateSelectedReportStatus("PENDING");
    }

    private void updateSelectedReportStatus(String newStatus) {
        Report selectedReport = reportsTable.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showWarning("Please select a report first");
            return;
        }

        new Thread(() -> {
            try {
                reportDAO.updateReportStatus(selectedReport.getId(), newStatus);
                Platform.runLater(() -> {
                    showSuccess("Report status updated to: " + newStatus);
                    loadDashboardData();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error updating status: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void handleRefresh() {
        loadDashboardData();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
