package dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import utils.FirebaseService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class AdminDAO_Firebase {

    private static final String COLLECTION_NAME = "admins";
    private final Firestore db;
    private final CollectionReference adminsCollection;

    public AdminDAO_Firebase() {
        this.db = FirebaseService.getFirestore();
        this.adminsCollection = db.collection(COLLECTION_NAME);
    }

    public boolean validateAdminLogin(String username, String password) throws ExecutionException, InterruptedException {
        String passwordHash = hashPassword(password);

        ApiFuture<QuerySnapshot> future = adminsCollection
                .whereEqualTo("username", username)
                .whereEqualTo("password_hash", passwordHash)
                .limit(1)
                .get();

        QuerySnapshot snapshot = future.get();
        return !snapshot.isEmpty();
    }

    public int getAdminId(String username) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = adminsCollection
                .whereEqualTo("username", username)
                .limit(1)
                .get();

        QuerySnapshot snapshot = future.get();

        if (!snapshot.isEmpty()) {
            DocumentSnapshot document = snapshot.getDocuments().get(0);
            String id = document.getString("id");
            // Convert String ID to Integer using hashCode
            return id != null ? id.hashCode() : -1;
        }

        return -1;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
