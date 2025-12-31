package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.User;
import utils.FirebaseService;

public class UserDAO_Firebase {

    private static final String COLLECTION_NAME = "users";
    private final Firestore firestore;

    public UserDAO_Firebase() {
        this.firestore = FirebaseService.getFirestore();
    }

    public boolean registerUser(String fullName, String email, String username,
                                String password, String phone) {
        try {
            if (usernameExists(username) || emailExists(email)) {
                return false;
            }

            String passwordHash = hashPassword(password);

            Map<String, Object> userData = new HashMap<>();
            userData.put("full_name", fullName);
            userData.put("email", email);
            userData.put("username", username);
            userData.put("password_hash", passwordHash);
            userData.put("phone", phone);
            userData.put("created_at", com.google.cloud.Timestamp.now().toString());

            ApiFuture<DocumentReference> future = firestore.collection(COLLECTION_NAME).add(userData);
            DocumentReference docRef = future.get();

            Map<String, Object> updates = new HashMap<>();
            updates.put("id", docRef.getId());
            docRef.update(updates).get();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User validateUserLogin(String username, String password) {
        try {
            String passwordHash = hashPassword(password);

            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("username", username)
                    .whereEqualTo("password_hash", passwordHash)
                    .limit(1)
                    .get();

            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                return documentToUser(document);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean usernameExists(String username) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("username", username)
                    .limit(1)
                    .get();

            QuerySnapshot querySnapshot = future.get();
            return !querySnapshot.isEmpty();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get();

            QuerySnapshot querySnapshot = future.get();
            return !querySnapshot.isEmpty();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(String userId) {
        try {
            ApiFuture<DocumentSnapshot> future = firestore.collection(COLLECTION_NAME)
                    .document(userId)
                    .get();

            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return documentToUser(document);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            QuerySnapshot querySnapshot = future.get();

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                users.add(documentToUser(document));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    private User documentToUser(DocumentSnapshot document) {
        String firebaseId = document.getId();
        int userId = Math.abs(firebaseId.hashCode());

        return new User(
                userId,
                document.getString("full_name"),
                document.getString("email"),
                document.getString("username"),
                document.getString("phone"),
                document.getString("created_at")
        );
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
