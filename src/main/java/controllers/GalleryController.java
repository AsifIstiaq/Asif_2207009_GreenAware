package controllers;

import java.io.File;

import dao.GalleryDAO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.GalleryItem;

public class GalleryController {

    @FXML private Label imageCountLabel;
    @FXML private Label statusLabel;
    @FXML private VBox galleryContainer;

    private final GalleryDAO galleryDAO = new GalleryDAO();

    @FXML
    public void initialize() {
        loadGallery();
    }

    @FXML
    public void handleRefresh() {
        loadGallery();
    }

    private void loadGallery() {
        setStatus("Loading gallery...");

        new Thread(() -> {
            try {
                ObservableList<GalleryItem> galleryItems = galleryDAO.getAllImagesWithDetails();
                int imageCount = galleryItems.size();

                Platform.runLater(() -> {
                    imageCountLabel.setText("Total Images: " + imageCount);

                    galleryContainer.getChildren().clear();

                    if (imageCount == 0) {
                        Label emptyMessage = new Label("No images have been submitted yet.");
                        emptyMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
                        emptyMessage.setAlignment(Pos.CENTER);
                        VBox.setMargin(emptyMessage, new Insets(50, 0, 0, 0));
                        galleryContainer.getChildren().add(emptyMessage);
                        setStatus("Gallery is empty");
                    } else {
                        for (GalleryItem item : galleryItems) {
                            galleryContainer.getChildren().add(createGalleryItemCard(item));
                        }
                        setStatus("Gallery loaded successfully - " + imageCount + " images");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Error loading gallery: " + e.getMessage());
                    setStatus("Error loading gallery");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private VBox createGalleryItemCard(GalleryItem item) {
        VBox card = new VBox(10);
        card.getStyleClass().add("section-card");
        card.setPadding(new Insets(15));
        card.setMaxWidth(Double.MAX_VALUE);

        VBox imageContainer = new VBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        imageContainer.setPrefHeight(300);
        imageContainer.setMaxHeight(300);

        try {
            String imagePath = item.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);

                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString(), 600, 280, true, true);
                    ImageView imageView = new ImageView(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(280);
                    imageView.setFitWidth(600);
                    imageContainer.getChildren().add(imageView);
                } else {
                    Label noImageLabel = new Label("🖼️ Image not found");
                    noImageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #95a5a6;");
                    Label pathLabel = new Label("Path: " + imagePath);
                    pathLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
                    pathLabel.setWrapText(true);
                    imageContainer.getChildren().addAll(noImageLabel, pathLabel);
                }
            } else {
                Label noImageLabel = new Label("🖼️ No image path");
                noImageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #95a5a6;");
                imageContainer.getChildren().add(noImageLabel);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("⚠️ Error loading image");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c;");
            imageContainer.getChildren().add(errorLabel);
        }

        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        VBox reporterBox = new VBox(5);
        reporterBox.setAlignment(Pos.CENTER_LEFT);
        Label reporterTitle = new Label("👤 Reporter:");
        reporterTitle.setFont(Font.font("System Bold", 13));
        reporterTitle.setStyle("-fx-text-fill: #34495e;");
        Label reporterName = new Label(item.getReporterName() != null ? item.getReporterName() : "Unknown");
        reporterName.setFont(Font.font(14));
        reporterName.setStyle("-fx-text-fill: #2c3e50;");
        reporterBox.getChildren().addAll(reporterTitle, reporterName);

        VBox locationBox = new VBox(5);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        Label locationTitle = new Label("📍 Location:");
        locationTitle.setFont(Font.font("System Bold", 13));
        locationTitle.setStyle("-fx-text-fill: #34495e;");
        Label locationText = new Label(item.getLocation() != null ? item.getLocation() : "Unknown");
        locationText.setFont(Font.font(14));
        locationText.setStyle("-fx-text-fill: #2c3e50;");
        locationText.setWrapText(true);
        locationBox.getChildren().addAll(locationTitle, locationText);

        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        Label dateLabel = new Label("📅 Date: " + (item.getDateReported() != null ? item.getDateReported() : "Unknown"));
        dateLabel.setFont(Font.font(12));
        dateLabel.setStyle("-fx-text-fill: #7f8c8d;");
        Label categoryLabel = new Label("🏷️ Category: " + (item.getCategoryName() != null ? item.getCategoryName() : "Unknown"));
        categoryLabel.setFont(Font.font(12));
        categoryLabel.setStyle("-fx-text-fill: #7f8c8d;");
        detailsBox.getChildren().addAll(dateLabel, categoryLabel);

        infoBox.getChildren().addAll(reporterBox, locationBox, detailsBox);
        HBox.setHgrow(locationBox, javafx.scene.layout.Priority.ALWAYS);

        card.getChildren().addAll(imageContainer, infoBox);

        return card;
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
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
