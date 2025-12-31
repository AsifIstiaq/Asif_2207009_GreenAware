package utils;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.Firestore;

public class AdminSeeder {

    public static void main(String[] args) {
        try {
            FirebaseService.initialize();

            createDefaultAdmin("admin", "admin123");
            System.out.println("✅ Default admin created successfully in Firebase!");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");
            System.out.println("\n⚠️  IMPORTANT: Change the admin password after first login!");

            FirebaseService.shutdown();
        } catch (Exception e) {
            System.err.println("❌ Error creating admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createDefaultAdmin(String username, String password) throws Exception {
        Firestore db = FirebaseService.getFirestore();
        String passwordHash = hashPassword(password);

        boolean exists = db.collection("admins")
                .whereEqualTo("username", username)
                .get()
                .get()
                .isEmpty();

        if (exists) {
            Map<String, Object> admin = new HashMap<>();
            admin.put("username", username);
            admin.put("password_hash", passwordHash);
            admin.put("email", "admin@greenaware.com");
            admin.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            db.collection("admins").add(admin).get();
            System.out.println("Admin document created in Firebase Firestore");
        } else {
            System.out.println("Admin already exists, skipping creation");
        }
    }

    private static String hashPassword(String password) {
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
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
