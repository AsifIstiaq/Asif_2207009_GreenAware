package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Worker {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty phone;
    private final StringProperty email;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty specialization;
    private final StringProperty status;
    private final StringProperty createdAt;

    public Worker() {
        this(0, "", "", "", "", "", "", "AVAILABLE", "");
    }

    public Worker(int id, String name, String phone, String email, String username, String password, String specialization, String status, String createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
        this.email = new SimpleStringProperty(email);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.specialization = new SimpleStringProperty(specialization);
        this.status = new SimpleStringProperty(status);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getPhone() { return phone.get(); }
    public void setPhone(String value) { phone.set(value); }
    public StringProperty phoneProperty() { return phone; }

    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }

    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }
    public StringProperty usernameProperty() { return username; }

    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value); }
    public StringProperty passwordProperty() { return password; }

    public String getSpecialization() { return specialization.get(); }
    public void setSpecialization(String value) { specialization.set(value); }
    public StringProperty specializationProperty() { return specialization; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String value) { createdAt.set(value); }
    public StringProperty createdAtProperty() { return createdAt; }
}
