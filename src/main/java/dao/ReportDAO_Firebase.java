package dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Report;
import utils.FirebaseService;

public class ReportDAO_Firebase {

    private static final String COLLECTION_NAME = "reports";
    private final Firestore db;
    private final CollectionReference reportsCollection;
    private final CollectionReference usersCollection;
    private final CollectionReference categoriesCollection;

    public ReportDAO_Firebase() {
        this.db = FirebaseService.getFirestore();
        this.reportsCollection = db.collection(COLLECTION_NAME);
        this.usersCollection = db.collection("users");
        this.categoriesCollection = db.collection("report_categories");
    }

    public void addReport(String userId, String categoryName, String incidentType, String location,
                          String dateReported, String severity, String description, String imagePath)
            throws ExecutionException, InterruptedException {

        String reporterName = "Unknown";
        if (userId != null && !userId.isEmpty()) {
            DocumentSnapshot userDoc = usersCollection.document(userId).get().get();
            if (userDoc.exists()) {
                String name = userDoc.getString("full_name");
                if (name != null && !name.isEmpty()) {
                    reporterName = name;
                }
            }
        }

        DocumentReference docRef = reportsCollection.document();
        String reportId = docRef.getId();

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("id", reportId);
        reportData.put("user_id", userId);
        reportData.put("reporter_name", reporterName);
        reportData.put("category_name", categoryName);
        reportData.put("incident_type", incidentType);
        reportData.put("location", location);
        reportData.put("date_reported", dateReported);
        reportData.put("severity", severity);
        reportData.put("description", description);
        reportData.put("image_path", imagePath);
        reportData.put("final_photo_path", null);
        reportData.put("status", "PENDING");
        reportData.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        docRef.set(reportData).get();
    }


    public ObservableList<String> getAllCategories() throws ExecutionException, InterruptedException {
        ObservableList<String> categories = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = categoriesCollection
                .orderBy("name")
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            String name = document.getString("name");
            if (name != null) {
                categories.add(name);
            }
        }

        return categories;
    }

    public ObservableList<Report> getReportsByUserId(String userId) throws ExecutionException, InterruptedException {
        ObservableList<Report> reports = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = reportsCollection
                .whereEqualTo("user_id", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            Report report = documentToReport(document);
            if (report != null) {
                reports.add(report);
            }
        }

        return reports;
    }

    public ObservableList<Report> getAllReports() throws ExecutionException, InterruptedException {
        ObservableList<Report> reports = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = reportsCollection
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            Report report = documentToReport(document);
            if (report != null) {
                reports.add(report);
            }
        }

        return reports;
    }

    public void updateReportStatusByFirebaseId(String firebaseId, String status) throws ExecutionException, InterruptedException {
        DocumentReference docRef = reportsCollection.document(firebaseId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);

        ApiFuture<WriteResult> future = docRef.update(updates);
        future.get();
    }

    public void updateReportStatus(int reportId, String status) throws ExecutionException, InterruptedException {
        String reportIdStr = String.valueOf(reportId);
        ApiFuture<QuerySnapshot> queryFuture = reportsCollection
                .whereEqualTo("id", reportIdStr)
                .limit(1)
                .get();

        QuerySnapshot querySnapshot = queryFuture.get();
        if (querySnapshot.isEmpty()) {
            ApiFuture<QuerySnapshot> allReportsFuture = reportsCollection.get();
            QuerySnapshot allReports = allReportsFuture.get();

            for (DocumentSnapshot doc : allReports.getDocuments()) {
                String storedId = doc.getString("id");
                if (storedId != null && storedId.hashCode() == reportId) {
                    DocumentReference docRef = reportsCollection.document(doc.getId());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("status", status);
                    ApiFuture<WriteResult> future = docRef.update(updates);
                    future.get();
                    return;
                }
            }
            throw new IllegalArgumentException("Report not found with ID: " + reportIdStr);
        }

        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
        DocumentReference docRef = reportsCollection.document(doc.getId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);

        ApiFuture<WriteResult> future = docRef.update(updates);
        future.get();
    }

    public Report getReportById(int reportId) throws ExecutionException, InterruptedException {
        String reportIdStr = String.valueOf(reportId);
        DocumentReference docRef = reportsCollection.document(reportIdStr);

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return documentToReport(document);
        }

        return null;
    }

    public int getReportCountByUserId(int userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reportsCollection
                .whereEqualTo("user_id", String.valueOf(userId))
                .get();

        QuerySnapshot snapshot = future.get();
        return snapshot.size();
    }

    public int getPendingReportCount() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reportsCollection
                .whereEqualTo("status", "PENDING")
                .get();

        QuerySnapshot snapshot = future.get();
        return snapshot.size();
    }

    public int getInProgressReportCount() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reportsCollection
                .whereEqualTo("status", "IN_PROGRESS")
                .get();

        QuerySnapshot snapshot = future.get();
        return snapshot.size();
    }

    public int getResolvedReportCount() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reportsCollection
                .whereEqualTo("status", "RESOLVED")
                .get();

        QuerySnapshot snapshot = future.get();
        return snapshot.size();
    }

    public void deleteReport(int reportId) throws ExecutionException, InterruptedException {
        String reportIdStr = String.valueOf(reportId);
        DocumentReference docRef = reportsCollection.document(reportIdStr);

        ApiFuture<WriteResult> future = docRef.delete();
        future.get();
    }

    public void updateFinalPhoto(int reportId, String finalPhotoPath) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> queryFuture = reportsCollection.get();
        QuerySnapshot allReports = queryFuture.get();

        for (DocumentSnapshot doc : allReports.getDocuments()) {
            String storedId = doc.getString("id");
            if (storedId != null && storedId.hashCode() == reportId) {
                DocumentReference docRef = reportsCollection.document(doc.getId());
                Map<String, Object> updates = new HashMap<>();
                updates.put("final_photo_path", finalPhotoPath);
                ApiFuture<WriteResult> future = docRef.update(updates);
                future.get();
                return;
            }
        }
        throw new IllegalArgumentException("Report not found with ID: " + reportId);
    }

private Report documentToReport(DocumentSnapshot document) {
    try {
        String id = document.getString("id");
        String userId = document.getString("user_id");
        String categoryName = document.getString("category_name");
        String incidentType = document.getString("incident_type");
        String location = document.getString("location");
        String dateReported = document.getString("date_reported");
        String severity = document.getString("severity");
        String description = document.getString("description");
        String imagePath = document.getString("image_path");
        String finalPhotoPath = document.getString("final_photo_path");
        String status = document.getString("status");
        String createdAt = document.getString("created_at");

        String reporterName = document.getString("reporter_name");
        if (reporterName == null || reporterName.isEmpty()) {
            reporterName = "Unknown";
        }

        int reportId = id != null ? id.hashCode() : 0;
        int userIdInt = userId != null ? userId.hashCode() : 0;

        return new Report(
                reportId,
                userIdInt,
                categoryName,
                incidentType,
                location,
                dateReported,
                severity,
                description,
                imagePath,
                finalPhotoPath,
                status,
                reporterName,
                createdAt,
                id
        );
    } catch (Exception e) {
        System.err.println("Error converting document to Report: " + e.getMessage());
        return null;
    }
}

}
