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
import models.Action;
import utils.FirebaseService;

public class ActionDAO_Firebase {

    private static final String COLLECTION_NAME = "actions";
    private final Firestore db;
    private final CollectionReference actionsCollection;

    public ActionDAO_Firebase() {
        this.db = FirebaseService.getFirestore();
        this.actionsCollection = db.collection(COLLECTION_NAME);
    }

    public ObservableList<Action> getAllActions() throws ExecutionException, InterruptedException {
        ObservableList<Action> actions = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = actionsCollection
                .orderBy("deadline", Query.Direction.DESCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            Action action = documentToAction(document);
            if (action != null) {
                actions.add(action);
            }
        }

        return actions;
    }

    public ObservableList<Action> getActionsByIncidentId(int incidentId) throws ExecutionException, InterruptedException {
        ObservableList<Action> actions = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = actionsCollection
                .whereEqualTo("incident_id", String.valueOf(incidentId))
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            Action action = documentToAction(document);
            if (action != null) {
                actions.add(action);
            }
        }

        return actions;
    }

    public void insertAction(Action action) throws ExecutionException, InterruptedException {
        DocumentReference docRef = actionsCollection.document();
        String actionId = docRef.getId();

        Map<String, Object> actionData = new HashMap<>();
        actionData.put("id", actionId);
        actionData.put("worker_id", String.valueOf(action.getWorkerId()));
        actionData.put("worker_name", action.getWorkerName());
        actionData.put("action_note", action.getActionNote());
        actionData.put("deadline", action.getDeadline());
        actionData.put("status", action.getStatus());
        actionData.put("resolution_details", action.getResolutionDetails());
        actionData.put("completed_date", action.getCompletedDate());
        actionData.put("location", action.getLocation());
        actionData.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        ApiFuture<WriteResult> future = docRef.set(actionData);
        future.get();
    }

    public boolean updateAction(Action action)
            throws ExecutionException, InterruptedException {

        long actionId = action.getId();

        ApiFuture<QuerySnapshot> queryFuture = actionsCollection
                .whereEqualTo("id", actionId)
                .limit(1)
                .get();

        QuerySnapshot querySnapshot = queryFuture.get();

        if (querySnapshot.isEmpty()) {
            System.err.println("Action not found with ID: " + actionId);
            return false;
        }

        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
        DocumentReference docRef = doc.getReference();

        Map<String, Object> updates = new HashMap<>();
        updates.put("worker_id", action.getWorkerId());
        updates.put("worker_name", action.getWorkerName());
        updates.put("action_note", action.getActionNote());
        updates.put("deadline", action.getDeadline());
        updates.put("status", action.getStatus());
        updates.put("resolution_details", action.getResolutionDetails());
        updates.put("completed_date", action.getCompletedDate());
        updates.put("location", action.getLocation());

        docRef.update(updates).get();

        return true;
    }

    public void deleteAction(int id) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> queryFuture = actionsCollection.get();
        QuerySnapshot querySnapshot = queryFuture.get();

        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            String docId = doc.getString("id");
            if (docId != null && Math.abs(docId.hashCode()) == Math.abs(id)) {
                DocumentReference docRef = actionsCollection.document(doc.getId());
                ApiFuture<WriteResult> future = docRef.delete();
                future.get();
                return;
            }
        }
    }

    public void deleteActionByFirebaseId(String firebaseId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = actionsCollection.document(firebaseId);
        ApiFuture<WriteResult> future = docRef.delete();
        future.get();
    }

    public ObservableList<Action> getActionsByWorkerId(int workerId) throws ExecutionException, InterruptedException {
        ObservableList<Action> actions = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = actionsCollection
                .whereEqualTo("worker_id", String.valueOf(workerId))
                .orderBy("deadline", Query.Direction.ASCENDING)
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            Action action = documentToAction(document);
            if (action != null) {
                actions.add(action);
            }
        }

        return actions;
    }

    private Action documentToAction(DocumentSnapshot document) {
        try {
            String id = document.getString("id");
            String workerId = document.getString("worker_id");
            String workerName = document.getString("worker_name");
            String actionNote = document.getString("action_note");
            String deadline = document.getString("deadline");
            String status = document.getString("status");
            String resolutionDetails = document.getString("resolution_details");
            String completedDate = document.getString("completed_date");
            String location = document.getString("location");
            String createdAt = document.getString("created_at");
            String firebaseId = document.getId(); // Get actual Firebase document ID

            int actionId = id != null ? Math.abs(id.hashCode()) : 0;
            int workerIdInt = workerId != null ? Math.abs(workerId.hashCode()) : 0;

            return new Action(
                    actionId,
                    workerIdInt,
                    actionNote,
                    deadline,
                    status,
                    resolutionDetails,
                    completedDate,
                    createdAt,
                    workerName,
                    location,
                    firebaseId
            );
        } catch (Exception e) {
            System.err.println("Error converting document to Action: " + e.getMessage());
            return null;
        }
    }
}
