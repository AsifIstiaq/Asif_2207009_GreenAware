package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GalleryItem {
    private final IntegerProperty reportId;
    private final StringProperty imagePath;
    private final StringProperty location;
    private final StringProperty reporterName;
    private final StringProperty dateReported;
    private final StringProperty categoryName;

    public GalleryItem(int reportId, String imagePath, String location, String reporterName,
                       String dateReported, String categoryName) {
        this.reportId = new SimpleIntegerProperty(reportId);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.location = new SimpleStringProperty(location);
        this.reporterName = new SimpleStringProperty(reporterName);
        this.dateReported = new SimpleStringProperty(dateReported);
        this.categoryName = new SimpleStringProperty(categoryName);
    }

    public String getImagePath() { return imagePath.get(); }
    public String getLocation() { return location.get(); }
    public void setLocation(String value) { location.set(value); }
    public String getReporterName() { return reporterName.get(); }
    public String getDateReported() { return dateReported.get(); }
    public String getCategoryName() { return categoryName.get(); }
}
