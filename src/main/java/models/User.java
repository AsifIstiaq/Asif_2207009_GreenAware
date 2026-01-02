package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final StringProperty id;       // Firestore document ID
    private final StringProperty fullName;
    private final StringProperty email;
    private final StringProperty username;
    private final StringProperty phone;
    private final StringProperty createdAt;

    public User(String id, String fullName, String email, String username, String phone, String createdAt) {
        this.id = new SimpleStringProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.username = new SimpleStringProperty(username);
        this.phone = new SimpleStringProperty(phone);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    public String getId() { return id.get(); }
    public void setId(String value) { id.set(value); }

    public String getFullName() { return fullName.get(); }
    public void setFullName(String value) { fullName.set(value); }

    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }

    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }

    public String getPhone() { return phone.get(); }
    public void setPhone(String value) { phone.set(value); }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String value) { createdAt.set(value); }
}
