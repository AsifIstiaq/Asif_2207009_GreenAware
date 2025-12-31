package utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseService {

    private static Firestore firestore;
    private static boolean initialized = false;

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            firestore = FirestoreClient.getFirestore();
            initialized = true;

            System.out.println("Firebase initialized successfully");

        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }

    public static Firestore getFirestore() {
        if (!initialized) {
            initialize();
        }
        return firestore;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void shutdown() {
        if (initialized && FirebaseApp.getApps() != null && !FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.getInstance().delete();
            initialized = false;
            System.out.println("Firebase shutdown successfully");
        }
    }
}
