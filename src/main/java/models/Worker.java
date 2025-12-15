package models;

import javafx.beans.property.*;

public class Worker {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty status;

    public Worker() {
        this(0, "", "AVAILABLE");
    }

    public Worker(int id, String name, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
}
