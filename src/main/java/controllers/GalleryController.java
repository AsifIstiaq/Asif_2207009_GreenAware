package controllers;

import java.io.File;

import dao.GalleryDAO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
                ObservableList<GalleryItem> items =
                        galleryDAO.getAllImagesWithDetails();

                Platform.runLater(() -> updateUI(items));

            } catch (Exception e) {
                Platform.runLater(() -> showError(e.getMessage()));
            }
        }).start();
    }

    private void updateUI(ObservableList<GalleryItem> items) {
        galleryContainer.getChildren().clear();
        imageCountLabel.setText("Total Images: " + items.size());

        if (items.isEmpty()) {
            Label emptyLabel = new Label("No images submitted yet.");
            emptyLabel.getStyleClass().add("gallery-empty");
            emptyLabel.setAlignment(Pos.CENTER);
            galleryContainer.getChildren().add(emptyLabel);
            setStatus("Gallery empty");
            return;
        }

        items.forEach(item ->
                galleryContainer.getChildren().add(createGalleryCard(item))
        );

        setStatus("Gallery loaded successfully");
    }

    private VBox createGalleryCard(GalleryItem item) {
        VBox card = new VBox();
        card.getStyleClass().add("gallery-card");

        VBox imageBox = new VBox();
        imageBox.getStyleClass().add("gallery-image-box");

        ImageView imageView = new ImageView();
        imageView.getStyleClass().add("gallery-image");

       if (item.getImagePath() != null) {
           File file = new File(item.getImagePath());
           if (file.exists()) {
               Image image = new Image(file.toURI().toString());
               imageView = new ImageView(image);

               imageView.setFitWidth(600);
               imageView.setFitHeight(300);
               imageView.setPreserveRatio(true);
               imageView.setSmooth(true);
               imageView.setCache(true);

               imageBox.getChildren().add(imageView);
            } else {
                imageBox.getChildren().add(createInfoLabel("Image not found"));
            }
        } else {
            imageBox.getChildren().add(createInfoLabel("No image available"));
        }

        HBox infoBox = new HBox();
        infoBox.getStyleClass().add("gallery-info-box");

        infoBox.getChildren().addAll(
                createInfoSection("Reporter", item.getReporterName()),
                createInfoSection("Location", item.getLocation()),
                createInfoSection("Date", item.getDateReported()),
                createInfoSection("Category", item.getCategoryName())
        );

        card.getChildren().addAll(imageBox, infoBox);
        return card;
    }

    private VBox createInfoSection(String title, String value) {
        VBox box = new VBox();
        box.getStyleClass().add("gallery-info-section");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("gallery-info-title");

        Label valueLabel = new Label(value != null ? value : "Unknown");
        valueLabel.getStyleClass().add("gallery-info-value");

        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }

    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("gallery-placeholder");
        return label;
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }
}
