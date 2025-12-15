package controllers;

import dao.ReportDAO;
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
    private final ReportDAO reportDAO = new ReportDAO();

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
}
