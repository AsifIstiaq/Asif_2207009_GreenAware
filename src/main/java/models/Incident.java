package models;

import javafx.beans.property.*;

public class Incident {
    private final IntegerProperty id;
    private final StringProperty incidentType;
    private final StringProperty location;
    private final StringProperty date;
    private final StringProperty severity;
    private final StringProperty reporterName;
    private final StringProperty reporterContact;
    private final StringProperty imagePath;
    private final StringProperty status;
    private final StringProperty description;
    private final IntegerProperty categoryId;
    private final IntegerProperty assignedWorkerId;
    private final StringProperty createdAt;
    private final StringProperty categoryName;
    private final StringProperty workerName;

    public Incident() {
        this(0, "", "", "", "", "", "", "", "PENDING", "", 0, 0, "", "", "");
    }

    public Incident(int id, String incidentType, String location, String date, String severity,
                    String reporterName, String reporterContact, String imagePath, String status,
                    String description, int categoryId, int assignedWorkerId, String createdAt,
                    String categoryName, String workerName) {
        this.id = new SimpleIntegerProperty(id);
        this.incidentType = new SimpleStringProperty(incidentType);
        this.location = new SimpleStringProperty(location);
        this.date = new SimpleStringProperty(date);
        this.severity = new SimpleStringProperty(severity);
        this.reporterName = new SimpleStringProperty(reporterName);
        this.reporterContact = new SimpleStringProperty(reporterContact);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.status = new SimpleStringProperty(status);
        this.description = new SimpleStringProperty(description);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.assignedWorkerId = new SimpleIntegerProperty(assignedWorkerId);
        this.createdAt = new SimpleStringProperty(createdAt);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.workerName = new SimpleStringProperty(workerName);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getIncidentType() { return incidentType.get(); }
    public void setIncidentType(String value) { incidentType.set(value); }
    public StringProperty incidentTypeProperty() { return incidentType; }

    public String getLocation() { return location.get(); }
    public void setLocation(String value) { location.set(value); }
    public StringProperty locationProperty() { return location; }

    public String getDate() { return date.get(); }
    public void setDate(String value) { date.set(value); }
    public StringProperty dateProperty() { return date; }

    public String getSeverity() { return severity.get(); }
    public void setSeverity(String value) { severity.set(value); }
    public StringProperty severityProperty() { return severity; }

    public String getReporterName() { return reporterName.get(); }
    public void setReporterName(String value) { reporterName.set(value); }
    public StringProperty reporterNameProperty() { return reporterName; }

    public String getReporterContact() { return reporterContact.get(); }
    public void setReporterContact(String value) { reporterContact.set(value); }
    public StringProperty reporterContactProperty() { return reporterContact; }

    public String getImagePath() { return imagePath.get(); }
    public void setImagePath(String value) { imagePath.set(value); }
    public StringProperty imagePathProperty() { return imagePath; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public int getCategoryId() { return categoryId.get(); }
    public void setCategoryId(int value) { categoryId.set(value); }
    public IntegerProperty categoryIdProperty() { return categoryId; }

    public int getAssignedWorkerId() { return assignedWorkerId.get(); }
    public void setAssignedWorkerId(int value) { assignedWorkerId.set(value); }
    public IntegerProperty assignedWorkerIdProperty() { return assignedWorkerId; }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String value) { createdAt.set(value); }
    public StringProperty createdAtProperty() { return createdAt; }

    public String getCategoryName() { return categoryName.get(); }
    public void setCategoryName(String value) { categoryName.set(value); }
    public StringProperty categoryNameProperty() { return categoryName; }

    public String getWorkerName() { return workerName.get(); }
    public void setWorkerName(String value) { workerName.set(value); }
    public StringProperty workerNameProperty() { return workerName; }
}
