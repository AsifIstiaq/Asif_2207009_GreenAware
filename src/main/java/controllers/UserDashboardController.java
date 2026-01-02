package controllers;

import dao.ReportDAO_Firebase;
import javafx.application.Platform;
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
import models.Report;
import models.User;
import utils.Session;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalReportsLabel;
    @FXML private Label pendingLabel;
    @FXML private Label inProgressLabel;
    @FXML private Label resolvedLabel;
    @FXML private TableView<Report> reportsTable;
    @FXML private TableColumn<Report, Integer> idColumn;
    @FXML private TableColumn<Report, String> categoryColumn;
    @FXML private TableColumn<Report, String> locationColumn;
    @FXML private TableColumn<Report, String> dateColumn;
    @FXML private TableColumn<Report, String> severityColumn;
    @FXML private TableColumn<Report, String> statusColumn;
    @FXML private TableColumn<Report, String> submittedColumn;

    private User currentUser;
    private final ReportDAO_Firebase reportDAO = new ReportDAO_Firebase();

    @FXML
    public void initialize() {
        setupTable();

        currentUser = Session.getUser();
        if (currentUser == null) {
            showError("User session missing. Please log in again.");
            return;
        }

        welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
        loadDashboardData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(
                cell -> cell.getValue().idProperty().asObject()
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

    private void loadDashboardData() {
        if (currentUser == null) {
            Platform.runLater(() ->
                    showError("User not set. Please log in again.")
            );
            return;
        }

        new Thread(() -> {
            try {
                ObservableList<Report> reports = reportDAO.getReportsByUserId(currentUser.getId());

                int total = reports.size();
                int pending = (int) reports.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
                int inProgress = (int) reports.stream().filter(r -> "IN_PROGRESS".equals(r.getStatus())).count();
                int resolved = (int) reports.stream().filter(r -> "RESOLVED".equals(r.getStatus())).count();

                Platform.runLater(() -> {
                    totalReportsLabel.setText(String.valueOf(total));
                    pendingLabel.setText(String.valueOf(pending));
                    inProgressLabel.setText(String.valueOf(inProgress));
                    resolvedLabel.setText(String.valueOf(resolved));
                    reportsTable.setItems(reports);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error loading dashboard data: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void handleNewReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/greenaware/user_report_form.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) reportsTable.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Error loading report form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRefresh() {
        loadDashboardData();
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
    public void handleLogout() {
        try {
            Session.clear();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/greenaware/home.fxml"));
            Stage stage = (Stage) reportsTable.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("GreenAware - Community Waste & Pollution Reporting Manager");
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

        if (report.getImagePath() != null || report.getFinalPhotoPath() != null) {
            content.getChildren().add(new javafx.scene.control.Separator());
            Label photosLabel = new Label("Photos:");
            photosLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            content.getChildren().add(photosLabel);

            javafx.scene.layout.HBox photosBox = new javafx.scene.layout.HBox(20);
            photosBox.setAlignment(javafx.geometry.Pos.CENTER);

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
}
