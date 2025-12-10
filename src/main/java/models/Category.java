package models;

import javafx.beans.property.*;

public class Category {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final StringProperty colorCode;
    private final StringProperty createdAt;

    public Category() {
        this(0, "", "", "", "");
    }

    public Category(int id, String name, String description, String colorCode, String createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.colorCode = new SimpleStringProperty(colorCode);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public String getColorCode() { return colorCode.get(); }
    public void setColorCode(String value) { colorCode.set(value); }
    public StringProperty colorCodeProperty() { return colorCode; }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String value) { createdAt.set(value); }
    public StringProperty createdAtProperty() { return createdAt; }
}
