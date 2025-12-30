package controllers;

import java.io.File;
import java.util.Optional;

import dao.WorkProgressDAO;
import dao.WorkerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.WorkProgress;
import models.Worker;

public class WorkersController {
    @FXML private Label totalWorkersLabel;
    @FXML private Label availableLabel;
    @FXML private Label totalProgressLabel;

    @FXML private TableView<Worker> workersTable;
    @FXML private TableColumn<Worker, Integer> workerIdColumn;
    @FXML private TableColumn<Worker, String> workerNameColumn;
    @FXML private TableColumn<Worker, String> workerPhoneColumn;
    @FXML private TableColumn<Worker, String> workerEmailColumn;
    @FXML private TableColumn<Worker, String> workerSpecializationColumn;
    @FXML private TableColumn<Worker, String> workerStatusColumn;

    @FXML private TableView<WorkProgress> progressTable;
    @FXML private TableColumn<WorkProgress, Integer> progressIdColumn;
    @FXML private TableColumn<WorkProgress, String> progressWorkerColumn;
    @FXML private TableColumn<WorkProgress, String> progressPhoneColumn;
    @FXML private TableColumn<WorkProgress, String> progressLocationColumn;
    @FXML private TableColumn<WorkProgress, String> progressStatusColumn;
    @FXML private TableColumn<WorkProgress, String> progressDateColumn;

    private WorkerDAO workerDAO = new WorkerDAO();
    private WorkProgressDAO progressDAO = new WorkProgressDAO();
    private ObservableList<Worker> workers = FXCollections.observableArrayList();
    private ObservableList<WorkProgress> progressReports = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        workerIdColumn.setCellValueFactory(
                cell -> cell.getValue().idProperty().asObject()
        );

        workerNameColumn.setCellValueFactory(
                cell -> cell.getValue().nameProperty()
        );

        workerPhoneColumn.setCellValueFactory(
                cell -> cell.getValue().phoneProperty()
        );

        workerEmailColumn.setCellValueFactory(
                cell -> cell.getValue().emailProperty()
        );

        workerSpecializationColumn.setCellValueFactory(
                cell -> cell.getValue().specializationProperty()
        );

        workerStatusColumn.setCellValueFactory(
                cell -> cell.getValue().statusProperty()
        );

        workersTable.setItems(workers);


        progressIdColumn.setCellValueFactory(
                cell -> cell.getValue().idProperty().asObject()
        );

        progressWorkerColumn.setCellValueFactory(
                cell -> cell.getValue().workerNameProperty()
        );

        progressPhoneColumn.setCellValueFactory(
                cell -> cell.getValue().workerPhoneProperty()
        );

        progressLocationColumn.setCellValueFactory(
                cell -> cell.getValue().locationProperty()
        );

        progressStatusColumn.setCellValueFactory(
                cell -> cell.getValue().statusProperty()
        );

        progressDateColumn.setCellValueFactory(
                cell -> cell.getValue().submittedAtProperty()
        );

        progressTable.setItems(progressReports);

        loadData();
    }


    private void loadData() {
        try {
            workers.clear();
            workers.addAll(workerDAO.getAllWorkers());

            progressReports.clear();
            progressReports.addAll(progressDAO.getAllProgress());

            updateStats();
        } catch (Exception e) {
            showError("Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStats() {
        totalWorkersLabel.setText(String.valueOf(workers.size()));
        long available = workers.stream().filter(w -> w.getStatus().equals("AVAILABLE")).count();
        availableLabel.setText(String.valueOf(available));
        totalProgressLabel.setText(String.valueOf(progressReports.size()));
    }

    @FXML
    public void handleRefresh() {
        loadData();
    }

    @FXML
    public void handleViewWorkerDetails() {
        Worker selected = workersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a worker to view details");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Worker Details");
        alert.setHeaderText(selected.getName());
        alert.setContentText(
                "ID: " + selected.getId() + "\n" +
                        "Email: " + selected.getEmail() + "\n" +
                        "Phone: " + selected.getPhone() + "\n" +
                        "Specialization: " + selected.getSpecialization() + "\n" +
                        "Status: " + selected.getStatus() + "\n" +
                        "Created: " + selected.getCreatedAt()
        );
        alert.showAndWait();
    }

    @FXML
    public void handleViewProgressDetails() {
        WorkProgress selected = progressTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a progress report to view details");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Work Progress Details");
        dialog.setHeaderText("Progress Report #" + selected.getId());

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(15);
        content.setPadding(new javafx.geometry.Insets(20));

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        grid.add(new Label("Worker:"), 0, 0);
        grid.add(new Label(selected.getWorkerName()), 1, 0);

        grid.add(new Label("Phone:"), 0, 1);
        grid.add(new Label(selected.getWorkerPhone()), 1, 1);

        grid.add(new Label("Location:"), 0, 2);
        grid.add(new Label(selected.getLocation()), 1, 2);

        grid.add(new Label("Status:"), 0, 3);
        grid.add(new Label(selected.getStatus()), 1, 3);

        grid.add(new Label("Submitted:"), 0, 4);
        grid.add(new Label(selected.getSubmittedAt()), 1, 4);

        grid.add(new Label("Description:"), 0, 5);
        TextArea descArea = new TextArea(selected.getDescription());
        descArea.setWrapText(true);
        descArea.setEditable(false);
        descArea.setPrefRowCount(3);
        grid.add(descArea, 1, 5);

        content.getChildren().add(grid);

        if (selected.getPhotoPath() != null && !selected.getPhotoPath().isEmpty()) {
            File photoFile = new File(selected.getPhotoPath());
            if (photoFile.exists()) {
                ImageView imageView = new ImageView();
                try {
                    Image image = new Image(photoFile.toURI().toString());
                    imageView.setImage(image);
                    imageView.setFitWidth(400);
                    imageView.setPreserveRatio(true);
                    content.getChildren().add(new Label("Photo:"));
                    content.getChildren().add(imageView);
                } catch (Exception e) {
                    content.getChildren().add(new Label("Failed to load photo"));
                }
            }
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    @FXML
    public void handleFilterProgress() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("ALL", "ALL", "PENDING", "IN_PROGRESS", "COMPLETED");
        dialog.setTitle("Filter Progress");
        dialog.setHeaderText("Filter by Status");
        dialog.setContentText("Select status:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(status -> {
            try {
                if (status.equals("ALL")) {
                    progressReports.clear();
                    progressReports.addAll(progressDAO.getAllProgress());
                } else {
                    ObservableList<WorkProgress> allProgress = FXCollections.observableArrayList(progressDAO.getAllProgress());
                    progressReports.clear();
                    progressReports.addAll(allProgress.stream()
                            .filter(p -> p.getStatus().equals(status))
                            .toList());
                }
            } catch (Exception e) {
                showError("Failed to filter: " + e.getMessage());
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
