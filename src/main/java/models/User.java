package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final IntegerProperty id;
    private final StringProperty fullName;
    private final StringProperty email;
    private final StringProperty username;
    private final StringProperty phone;
    private final StringProperty createdAt;

    public User(int id, String fullName, String email, String username, String phone, String createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.username = new SimpleStringProperty(username);
        this.phone = new SimpleStringProperty(phone);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public String getFullName() { return fullName.get(); }
    public String getEmail() { return email.get(); }
    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }
}
