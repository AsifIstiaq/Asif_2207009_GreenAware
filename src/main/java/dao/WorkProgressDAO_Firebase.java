package dao;

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
import models.WorkProgress;
import utils.FirebaseService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WorkProgressDAO_Firebase {

    private static final String COLLECTION_NAME = "work_progress";
    private final Firestore db;
    private final CollectionReference workProgressCollection;

    public WorkProgressDAO_Firebase() {
        this.db = FirebaseService.getFirestore();
        this.workProgressCollection = db.collection(COLLECTION_NAME);
    }

    public ObservableList<WorkProgress> getAllProgress() throws ExecutionException, InterruptedException {
        ObservableList<WorkProgress> progressList = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = workProgressCollection
                .orderBy("submitted_at", Query.Direction.DESCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            WorkProgress progress = documentToWorkProgress(document);
            if (progress != null) {
                progressList.add(progress);
            }
        }

        return progressList;
    }

    public ObservableList<WorkProgress> getProgressByWorkerId(int workerId) throws ExecutionException, InterruptedException {
        ObservableList<WorkProgress> progressList = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = workProgressCollection
                .whereEqualTo("worker_id", String.valueOf(workerId))
                .orderBy("submitted_at", Query.Direction.DESCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            WorkProgress progress = documentToWorkProgress(document);
            if (progress != null) {
                progressList.add(progress);
            }
        }

        return progressList;
    }

    public ObservableList<WorkProgress> getProgressByActionId(int actionId) throws ExecutionException, InterruptedException {
        ObservableList<WorkProgress> progressList = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = workProgressCollection
                .whereEqualTo("action_id", String.valueOf(actionId))
                .orderBy("submitted_at", Query.Direction.DESCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            WorkProgress progress = documentToWorkProgress(document);
            if (progress != null) {
                progressList.add(progress);
            }
        }

        return progressList;
    }

    public void insertProgress(WorkProgress progress) throws ExecutionException, InterruptedException {
        DocumentReference docRef = workProgressCollection.document();
        String progressId = docRef.getId();

        Map<String, Object> progressData = new HashMap<>();
        progressData.put("id", progressId);
        progressData.put("action_id", String.valueOf(progress.getActionId()));
        progressData.put("worker_id", String.valueOf(progress.getWorkerId()));
        progressData.put("worker_name", progress.getWorkerName());
        progressData.put("worker_phone", progress.getWorkerPhone());
        progressData.put("description", progress.getDescription());
        progressData.put("photo_path", progress.getPhotoPath());
        progressData.put("location", progress.getLocation());
        progressData.put("status", progress.getStatus());
        progressData.put("submitted_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        ApiFuture<WriteResult> future = docRef.set(progressData);
        future.get();
    }

    public void updateProgress(WorkProgress progress) throws ExecutionException, InterruptedException {
        String progressId = String.valueOf(progress.getId());
        DocumentReference docRef = workProgressCollection.document(progressId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("description", progress.getDescription());
        updates.put("photo_path", progress.getPhotoPath());
        updates.put("status", progress.getStatus());

        ApiFuture<WriteResult> future = docRef.update(updates);
        future.get();
    }

    public WorkProgress getLatestProgressByActionId(int actionId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = workProgressCollection
                .whereEqualTo("action_id", String.valueOf(actionId))
                .orderBy("submitted_at", Query.Direction.DESCENDING)
                .limit(1)
                .get();

        QuerySnapshot snapshot = future.get();

        if (!snapshot.isEmpty()) {
            DocumentSnapshot document = snapshot.getDocuments().get(0);
            return documentToWorkProgress(document);
        }

        return null;
    }

    public void deleteProgress(int id) throws ExecutionException, InterruptedException {
        String progressId = String.valueOf(id);
        DocumentReference docRef = workProgressCollection.document(progressId);

        ApiFuture<WriteResult> future = docRef.delete();
        future.get();
    }

    private WorkProgress documentToWorkProgress(DocumentSnapshot document) {
        try {
            String id = document.getString("id");
            String actionId = document.getString("action_id");
            String workerId = document.getString("worker_id");
            String workerName = document.getString("worker_name");
            String workerPhone = document.getString("worker_phone");
            String description = document.getString("description");
            String photoPath = document.getString("photo_path");
            String location = document.getString("location");
            String status = document.getString("status");
            String submittedAt = document.getString("submitted_at");

            int progressId = id != null ? id.hashCode() : 0;
            int actionIdInt = actionId != null ? actionId.hashCode() : 0;
            int workerIdInt = workerId != null ? workerId.hashCode() : 0;

            return new WorkProgress(
                    progressId,
                    actionIdInt,
                    workerIdInt,
                    workerName,
                    workerPhone,
                    description,
                    photoPath,
                    location,
                    status,
                    submittedAt
            );
        } catch (Exception e) {
            System.err.println("Error converting document to WorkProgress: " + e.getMessage());
            return null;
        }
    }
}
