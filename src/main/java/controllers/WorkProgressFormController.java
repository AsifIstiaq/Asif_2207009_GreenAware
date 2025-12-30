package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import dao.ActionDAO;
import dao.WorkProgressDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Action;
import models.WorkProgress;
import models.Worker;

public class WorkProgressFormController {
    @FXML private Label assignmentIdLabel;
    @FXML private Label locationLabel;
    @FXML private Label deadlineLabel;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextArea descriptionArea;
    @FXML private TextField photoPathField;
    @FXML private ImageView photoPreview;
    @FXML private Label messageLabel;

    private Action currentAction;
    private Worker currentWorker;
    private String selectedPhotoPath;
    private ActionDAO actionDAO = new ActionDAO();
    private WorkProgressDAO progressDAO = new WorkProgressDAO();

    public void setActionAndWorker(Action action, Worker worker) {
        this.currentAction = action;
        this.currentWorker = worker;

        try {
            assignmentIdLabel.setText("#" + action.getId());
            locationLabel.setText(action.getLocation() != null ? action.getLocation() : "N/A");
            deadlineLabel.setText(action.getDeadline());
            statusCombo.setValue(action.getStatus());
        } catch (Exception e) {
            showMessage("Failed to load action details: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBrowsePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) photoPathField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            selectedPhotoPath = selectedFile.getAbsolutePath();
            photoPathField.setText(selectedFile.getName());

            try {
                Image image = new Image(selectedFile.toURI().toString());
                photoPreview.setImage(image);
            } catch (Exception e) {
                showMessage("Failed to load image preview", "red");
            }
        }
    }

    @FXML
    public void handleSubmit() {
        messageLabel.setText("");

        String status = statusCombo.getValue();
        String description = descriptionArea.getText().trim();

        if (status == null || status.isEmpty()) {
            showMessage("Please select a status", "red");
            return;
        }

        if (description.isEmpty()) {
            showMessage("Please enter a description of the work done", "red");
            return;
        }

        if (selectedPhotoPath == null || selectedPhotoPath.isEmpty()) {
            showMessage("Please upload a photo of the work", "red");
            return;
        }

        try {
            File sourceFile = new File(selectedPhotoPath);
            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            Path targetPath = Path.of("db/progress_photos/" + fileName);

            Files.createDirectories(targetPath.getParent());
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String location = currentAction.getLocation() != null ? currentAction.getLocation() : "N/A";

            WorkProgress progress = new WorkProgress();
            progress.setActionId(currentAction.getId());
            progress.setWorkerId(currentWorker.getId());
            progress.setWorkerName(currentWorker.getName());
            progress.setWorkerPhone(currentWorker.getPhone());
            progress.setDescription(description);
            progress.setPhotoPath(targetPath.toString());
            progress.setLocation(location);
            progress.setStatus(status);

            progressDAO.insertProgress(progress);

            currentAction.setStatus(status);
            if (status.equals("COMPLETED")) {
                currentAction.setCompletedDate(java.time.LocalDate.now().toString());
            }
            actionDAO.updateAction(currentAction);

            showMessage("Progress submitted successfully!", "green");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleCancel);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            showMessage("Failed to save photo: " + e.getMessage(), "red");
            e.printStackTrace();
        } catch (Exception e) {
            showMessage("Failed to submit progress: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel() {
        Stage stage = (Stage) assignmentIdLabel.getScene().getWindow();
        stage.close();
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
