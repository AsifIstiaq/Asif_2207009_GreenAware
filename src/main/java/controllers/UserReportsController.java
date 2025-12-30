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
//        updateSelectedReportStatus("RESOLVED");
        Report selectedReport = reportsTable.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showWarning("Please select a report first");
            return;
        }

        // Open dialog to select final photo from work progress
        showSelectFinalPhotoDialog(selectedReport);
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
    public void handleViewDetails() {
        Report selectedReport = reportsTable.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showError("Please select a report to view details");
            return;
        }

        showReportDetailsDialog(selectedReport);
    }

    @FXML
    public void handleRefresh() {
        loadDashboardData();
    }

    private void showSelectFinalPhotoDialog(Report report) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Completed Work Photo");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Set initial directory to progress photos if exists
        java.io.File progressPhotosDir = new java.io.File("db/progress_photos");
        if (progressPhotosDir.exists()) {
            fileChooser.setInitialDirectory(progressPhotosDir);
        }

        java.io.File selectedFile = fileChooser.showOpenDialog(reportsTable.getScene().getWindow());
        if (selectedFile != null) {
            new Thread(() -> {
                try {
                    // Update final photo path
                    reportDAO.updateFinalPhoto(report.getId(), selectedFile.getAbsolutePath());
                    // Update status to resolved
                    reportDAO.updateReportStatus(report.getId(), "RESOLVED");

                    Platform.runLater(() -> {
                        showSuccess("Report marked as resolved with completion photo!");
                        loadDashboardData();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Error updating report: " + e.getMessage()));
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void showReportDetailsDialog(Report report) {
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Report Details");
        dialog.setHeaderText("Report #" + report.getId() + " - " + report.getStatus());

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(15);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        // Report information
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        grid.add(new Label("Category:"), 0, 0);
        grid.add(new Label(report.getCategoryName()), 1, 0);

        grid.add(new Label("Location:"), 0, 1);
        grid.add(new Label(report.getLocation()), 1, 1);

        grid.add(new Label("Date Reported:"), 0, 2);
        grid.add(new Label(report.getDateReported()), 1, 2);

        grid.add(new Label("Severity:"), 0, 3);
        grid.add(new Label(report.getSeverity()), 1, 3);

        grid.add(new Label("Status:"), 0, 4);
        Label statusLabel = new Label(report.getStatus());
        statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                (report.getStatus().equals("RESOLVED") ? "#27ae60" :
                        report.getStatus().equals("IN_PROGRESS") ? "#f39c12" : "#e74c3c") + ";");
        grid.add(statusLabel, 1, 4);

        content.getChildren().add(grid);

        // Photos section
        if (report.getImagePath() != null || report.getFinalPhotoPath() != null) {
            content.getChildren().add(new javafx.scene.control.Separator());
            Label photosLabel = new Label("Photos:");
            photosLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            content.getChildren().add(photosLabel);

            javafx.scene.layout.HBox photosBox = new javafx.scene.layout.HBox(20);
            photosBox.setAlignment(javafx.geometry.Pos.CENTER);

            // Before photo
            if (report.getImagePath() != null && !report.getImagePath().isEmpty()) {
                javafx.scene.layout.VBox beforeBox = new javafx.scene.layout.VBox(10);
                beforeBox.setAlignment(javafx.geometry.Pos.CENTER);

                Label beforeLabel = new Label("BEFORE");
                beforeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

                javafx.scene.image.ImageView beforeImage = new javafx.scene.image.ImageView();
                try {
                    java.io.File imageFile = new java.io.File(report.getImagePath());
                    if (imageFile.exists()) {
                        javafx.scene.image.Image image = new javafx.scene.image.Image(imageFile.toURI().toString());
                        beforeImage.setImage(image);
                        beforeImage.setFitWidth(300);
                        beforeImage.setPreserveRatio(true);
                        beforeBox.getChildren().addAll(beforeLabel, beforeImage);
                        photosBox.getChildren().add(beforeBox);
                    }
                } catch (Exception e) {
                    beforeBox.getChildren().add(new Label("Failed to load before photo"));
                }
            }

            // After photo (if resolved)
            if (report.getFinalPhotoPath() != null && !report.getFinalPhotoPath().isEmpty()) {
                javafx.scene.layout.VBox afterBox = new javafx.scene.layout.VBox(10);
                afterBox.setAlignment(javafx.geometry.Pos.CENTER);

                Label afterLabel = new Label("AFTER");
                afterLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #27ae60;");

                javafx.scene.image.ImageView afterImage = new javafx.scene.image.ImageView();
                try {
                    java.io.File imageFile = new java.io.File(report.getFinalPhotoPath());
                    if (imageFile.exists()) {
                        javafx.scene.image.Image image = new javafx.scene.image.Image(imageFile.toURI().toString());
                        afterImage.setImage(image);
                        afterImage.setFitWidth(300);
                        afterImage.setPreserveRatio(true);
                        afterBox.getChildren().addAll(afterLabel, afterImage);
                        photosBox.getChildren().add(afterBox);
                    }
                } catch (Exception e) {
                    afterBox.getChildren().add(new Label("Failed to load after photo"));
                }
            }

            content.getChildren().add(photosBox);
        }

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
        dialog.getDialogPane().setPrefWidth(700);
        dialog.showAndWait();
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
