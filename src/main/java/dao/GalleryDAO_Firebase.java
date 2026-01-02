package dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.GalleryItem;
import utils.FirebaseService;

import java.util.concurrent.ExecutionException;

public class GalleryDAO_Firebase {

    private final Firestore db;
    private final CollectionReference reportsCollection;
    private final CollectionReference usersCollection;

    public GalleryDAO_Firebase() {
        this.db = FirebaseService.getFirestore();
        this.reportsCollection = db.collection("reports");
        this.usersCollection = db.collection("users");
    }

    public ObservableList<GalleryItem> getAllImagesWithDetails() throws ExecutionException, InterruptedException {
        ObservableList<GalleryItem> galleryItems = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = reportsCollection
                .orderBy("created_at", com.google.cloud.firestore.Query.Direction.DESCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot reportDoc : snapshot.getDocuments()) {
            String imagePath = reportDoc.getString("image_path");

            if (imagePath != null && !imagePath.isEmpty()) {
                String reportId = reportDoc.getString("id");
                String location = reportDoc.getString("location");
                String dateReported = reportDoc.getString("date_reported");
                String categoryName = reportDoc.getString("category_name");

                String reporterName = reportDoc.getString("reporter_name");
                if (reporterName == null || reporterName.isEmpty()) {
                    reporterName = "Unknown";
                }

                int reportIdInt = reportId != null ? reportId.hashCode() : 0;

                galleryItems.add(new GalleryItem(
                        reportIdInt,
                        imagePath,
                        location,
                        reporterName,
                        dateReported,
                        categoryName
                ));
            }
        }

        return galleryItems;
    }


    public int getImageCount() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reportsCollection.get();
        QuerySnapshot snapshot = future.get();

        int count = 0;
        for (DocumentSnapshot document : snapshot.getDocuments()) {
            String imagePath = document.getString("image_path");
            if (imagePath != null && !imagePath.isEmpty()) {
                count++;
            }
        }

        return count;
    }
}
