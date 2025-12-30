package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WorkProgress {
    private final IntegerProperty id;
    private final IntegerProperty actionId;
    private final IntegerProperty workerId;
    private final StringProperty workerName;
    private final StringProperty workerPhone;
    private final StringProperty description;
    private final StringProperty photoPath;
    private final StringProperty location;
    private final StringProperty status;
    private final StringProperty submittedAt;

    public WorkProgress() {
        this(0, 0, 0, "", "", "", "", "", "PENDING", "");
    }

    public WorkProgress(int id, int actionId, int workerId, String workerName, String workerPhone,
                        String description, String photoPath, String location, String status, String submittedAt) {
        this.id = new SimpleIntegerProperty(id);
        this.actionId = new SimpleIntegerProperty(actionId);
        this.workerId = new SimpleIntegerProperty(workerId);
        this.workerName = new SimpleStringProperty(workerName);
        this.workerPhone = new SimpleStringProperty(workerPhone);
        this.description = new SimpleStringProperty(description);
        this.photoPath = new SimpleStringProperty(photoPath);
        this.location = new SimpleStringProperty(location);
        this.status = new SimpleStringProperty(status);
        this.submittedAt = new SimpleStringProperty(submittedAt);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getActionId() { return actionId.get(); }
    public void setActionId(int value) { actionId.set(value); }
    public IntegerProperty actionIdProperty() { return actionId; }

    public int getWorkerId() { return workerId.get(); }
    public void setWorkerId(int value) { workerId.set(value); }
    public IntegerProperty workerIdProperty() { return workerId; }

    public String getWorkerName() { return workerName.get(); }
    public void setWorkerName(String value) { workerName.set(value); }
    public StringProperty workerNameProperty() { return workerName; }

    public String getWorkerPhone() { return workerPhone.get(); }
    public void setWorkerPhone(String value) { workerPhone.set(value); }
    public StringProperty workerPhoneProperty() { return workerPhone; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public String getPhotoPath() { return photoPath.get(); }
    public void setPhotoPath(String value) { photoPath.set(value); }
    public StringProperty photoPathProperty() { return photoPath; }

    public String getLocation() { return location.get(); }
    public void setLocation(String value) { location.set(value); }
    public StringProperty locationProperty() { return location; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    public String getSubmittedAt() { return submittedAt.get(); }
    public void setSubmittedAt(String value) { submittedAt.set(value); }
    public StringProperty submittedAtProperty() { return submittedAt; }
}
