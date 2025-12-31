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
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Worker;
import utils.FirebaseService;

public class WorkerDAO_Firebase {

    private static final String COLLECTION_NAME = "workers";
    private final Firestore db;
    private final CollectionReference workersCollection;

    public WorkerDAO_Firebase() {
        this.db = FirebaseService.getFirestore();
        this.workersCollection = db.collection(COLLECTION_NAME);
    }

    public ObservableList<Worker> getAllWorkers() throws ExecutionException, InterruptedException {
        ObservableList<Worker> workers = FXCollections.observableArrayList();

        ApiFuture<QuerySnapshot> future = workersCollection
                .orderBy("name")
                .get();

        QuerySnapshot snapshot = future.get();

        for (DocumentSnapshot document : snapshot.getDocuments()) {
            Worker worker = documentToWorker(document);
            if (worker != null) {
                workers.add(worker);
            }
        }

        return workers;
    }

    public void insertWorker(Worker worker) throws ExecutionException, InterruptedException {
        // Create a new document reference with auto-generated ID
        DocumentReference docRef = workersCollection.document();
        String workerId = docRef.getId();

        // Prepare worker data
        Map<String, Object> workerData = new HashMap<>();
        workerData.put("id", workerId);  // Store the ID in the document
        workerData.put("name", worker.getName());
        workerData.put("phone", worker.getPhone());
        workerData.put("email", worker.getEmail());
        workerData.put("username", worker.getUsername());
        workerData.put("password", worker.getPassword());
        workerData.put("specialization", worker.getSpecialization());
        workerData.put("status", worker.getStatus());
        workerData.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        ApiFuture<WriteResult> future = docRef.set(workerData);
        future.get();
    }

    public void updateWorker(Worker worker) throws ExecutionException, InterruptedException {
        String workerId = String.valueOf(worker.getId());
        DocumentReference docRef = workersCollection.document(workerId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", worker.getName());
        updates.put("phone", worker.getPhone());
        updates.put("email", worker.getEmail());
        updates.put("username", worker.getUsername());
        updates.put("password", worker.getPassword());
        updates.put("specialization", worker.getSpecialization());
        updates.put("status", worker.getStatus());

        ApiFuture<WriteResult> future = docRef.update(updates);
        future.get();
    }

    public void deleteWorker(int id) throws ExecutionException, InterruptedException {
        String workerId = String.valueOf(id);
        DocumentReference docRef = workersCollection.document(workerId);

        ApiFuture<WriteResult> future = docRef.delete();
        future.get();
    }

    public void updateWorkerStatus(int workerId, String status) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> queryFuture = workersCollection.get();
        QuerySnapshot querySnapshot = queryFuture.get();

        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            String docId = doc.getString("id");
            if (docId != null && Math.abs(docId.hashCode()) == Math.abs(workerId)) {
                DocumentReference docRef = workersCollection.document(doc.getId());
                Map<String, Object> updates = new HashMap<>();
                updates.put("status", status);
                ApiFuture<WriteResult> future = docRef.update(updates);
                future.get();
                return;
            }
        }
    }

    public Worker getWorkerById(int id) throws ExecutionException, InterruptedException {
        String workerId = String.valueOf(id);
        DocumentReference docRef = workersCollection.document(workerId);

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return documentToWorker(document);
        }

        return null;
    }

    public Worker authenticateWorker(String username, String password) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = workersCollection
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .limit(1)
                .get();

        QuerySnapshot snapshot = future.get();

        if (!snapshot.isEmpty()) {
            DocumentSnapshot document = snapshot.getDocuments().get(0);
            return documentToWorker(document);
        }

        return null;
    }

    public boolean emailExists(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = workersCollection
                .whereEqualTo("email", email)
                .limit(1)
                .get();

        QuerySnapshot snapshot = future.get();
        return !snapshot.isEmpty();
    }

    public boolean usernameExists(String username) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = workersCollection
                .whereEqualTo("username", username)
                .limit(1)
                .get();

        QuerySnapshot snapshot = future.get();
        return !snapshot.isEmpty();
    }

    private Worker documentToWorker(DocumentSnapshot document) {
        try {
            String id = document.getString("id");
            String name = document.getString("name");
            String phone = document.getString("phone");
            String email = document.getString("email");
            String username = document.getString("username");
            String password = document.getString("password");
            String specialization = document.getString("specialization");
            String status = document.getString("status");
            String createdAt = document.getString("created_at");

            int workerId = id.hashCode();

            return new Worker(
                    workerId,
                    name,
                    phone,
                    email,
                    username,
                    password,
                    specialization,
                    status,
                    createdAt
            );
        } catch (Exception e) {
            System.err.println("Error converting document to Worker: " + e.getMessage());
            return null;
        }
    }
}
