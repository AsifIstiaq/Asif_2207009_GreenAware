package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Report {
    private final IntegerProperty id;
    private final IntegerProperty userId;
    private final StringProperty categoryName;
    private final StringProperty incidentType;
    private final StringProperty location;
    private final StringProperty dateReported;
    private final StringProperty severity;
    private final StringProperty description;
    private final StringProperty imagePath;
    private final StringProperty status;
    private final StringProperty reporterName;
    private final StringProperty createdAt;
    private final StringProperty finalPhotoPath;
    private final StringProperty firebaseId;

    public Report(int id, int userId, String categoryName, String incidentType, String location, String dateReported,
                  String severity, String description, String imagePath, String finalPhotoPath, String status,
                  String reporterName, String createdAt, String firebaseId) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.incidentType = new SimpleStringProperty(incidentType);
        this.location = new SimpleStringProperty(location);
        this.dateReported = new SimpleStringProperty(dateReported);
        this.severity = new SimpleStringProperty(severity);
        this.description = new SimpleStringProperty(description);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.finalPhotoPath = new SimpleStringProperty(finalPhotoPath);
        this.status = new SimpleStringProperty(status);
        this.reporterName = new SimpleStringProperty(reporterName);
        this.createdAt = new SimpleStringProperty(createdAt);
        this.firebaseId = new SimpleStringProperty(firebaseId);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }
    public String getCategoryName() { return categoryName.get(); }
    public StringProperty categoryNameProperty() { return categoryName; }
    public String getLocation() { return location.get(); }
    public void setLocation(String value) { location.set(value); }
    public StringProperty locationProperty() { return location; }
    public StringProperty dateReportedProperty() { return dateReported; }
    public String getSeverity() { return severity.get(); }
    public StringProperty severityProperty() { return severity; }
    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }
    public StringProperty reporterNameProperty() { return reporterName; }
    public StringProperty createdAtProperty() { return createdAt; }
    public String getFinalPhotoPath() { return finalPhotoPath.get(); }
    public void setFinalPhotoPath(String value) { finalPhotoPath.set(value); }
    public StringProperty finalPhotoPathProperty() { return finalPhotoPath; }
    public String getImagePath() { return imagePath.get(); }
    public void setImagePath(String value) { imagePath.set(value); }
    public StringProperty imagePathProperty() { return imagePath; }
    public String getDateReported() { return dateReported.get(); }
    public void setDateReported(String value) { dateReported.set(value); }
}
