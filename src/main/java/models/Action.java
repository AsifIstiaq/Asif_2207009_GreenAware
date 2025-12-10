package models;

import javafx.beans.property.*;

public class Action {
    private final IntegerProperty id;
    private final IntegerProperty incidentId;
    private final IntegerProperty workerId;
    private final StringProperty actionNote;
    private final StringProperty deadline;
    private final StringProperty status;
    private final StringProperty resolutionDetails;
    private final StringProperty completedDate;
    private final StringProperty createdAt;
    private final StringProperty workerName;

    public Action() {
        this(0, 0, 0, "", "", "PENDING", "", "", "", "");
    }

    public Action(int id, int incidentId, int workerId, String actionNote, String deadline,
                  String status, String resolutionDetails, String completedDate, String createdAt,
                  String workerName) {
        this.id = new SimpleIntegerProperty(id);
        this.incidentId = new SimpleIntegerProperty(incidentId);
        this.workerId = new SimpleIntegerProperty(workerId);
        this.actionNote = new SimpleStringProperty(actionNote);
        this.deadline = new SimpleStringProperty(deadline);
        this.status = new SimpleStringProperty(status);
        this.resolutionDetails = new SimpleStringProperty(resolutionDetails);
        this.completedDate = new SimpleStringProperty(completedDate);
        this.createdAt = new SimpleStringProperty(createdAt);
        this.workerName = new SimpleStringProperty(workerName);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getIncidentId() { return incidentId.get(); }
    public void setIncidentId(int value) { incidentId.set(value); }
    public IntegerProperty incidentIdProperty() { return incidentId; }

    public int getWorkerId() { return workerId.get(); }
    public void setWorkerId(int value) { workerId.set(value); }
    public IntegerProperty workerIdProperty() { return workerId; }

    public String getActionNote() { return actionNote.get(); }
    public void setActionNote(String value) { actionNote.set(value); }
    public StringProperty actionNoteProperty() { return actionNote; }

    public String getDeadline() { return deadline.get(); }
    public void setDeadline(String value) { deadline.set(value); }
    public StringProperty deadlineProperty() { return deadline; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    public String getResolutionDetails() { return resolutionDetails.get(); }
    public void setResolutionDetails(String value) { resolutionDetails.set(value); }
    public StringProperty resolutionDetailsProperty() { return resolutionDetails; }

    public String getCompletedDate() { return completedDate.get(); }
    public void setCompletedDate(String value) { completedDate.set(value); }
    public StringProperty completedDateProperty() { return completedDate; }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String value) { createdAt.set(value); }
    public StringProperty createdAtProperty() { return createdAt; }

    public String getWorkerName() { return workerName.get(); }
    public void setWorkerName(String value) { workerName.set(value); }
    public StringProperty workerNameProperty() { return workerName; }
}
