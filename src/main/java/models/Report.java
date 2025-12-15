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

    public Report(int id, int userId, String categoryName, String incidentType, String location, String dateReported,
                  String severity, String description, String imagePath, String status,
                  String reporterName, String createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.incidentType = new SimpleStringProperty(incidentType);
        this.location = new SimpleStringProperty(location);
        this.dateReported = new SimpleStringProperty(dateReported);
        this.severity = new SimpleStringProperty(severity);
        this.description = new SimpleStringProperty(description);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.status = new SimpleStringProperty(status);
        this.reporterName = new SimpleStringProperty(reporterName);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }

    public String getCategoryName() { return categoryName.get(); }
    public void setCategoryName(String value) { categoryName.set(value); }
    public StringProperty categoryNameProperty() { return categoryName; }

    public String getIncidentType() { return incidentType.get(); }
    public void setIncidentType(String value) { incidentType.set(value); }
    public StringProperty incidentTypeProperty() { return incidentType; }

    public String getLocation() { return location.get(); }
    public void setLocation(String value) { location.set(value); }
    public StringProperty locationProperty() { return location; }

    public String getDateReported() { return dateReported.get(); }
    public void setDateReported(String value) { dateReported.set(value); }
    public StringProperty dateReportedProperty() { return dateReported; }

    public String getSeverity() { return severity.get(); }
    public void setSeverity(String value) { severity.set(value); }
    public StringProperty severityProperty() { return severity; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public String getImagePath() { return imagePath.get(); }
    public void setImagePath(String value) { imagePath.set(value); }
    public StringProperty imagePathProperty() { return imagePath; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    public String getReporterName() { return reporterName.get(); }
    public void setReporterName(String value) { reporterName.set(value); }
    public StringProperty reporterNameProperty() { return reporterName; }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String value) { createdAt.set(value); }
    public StringProperty createdAtProperty() { return createdAt; }
}
