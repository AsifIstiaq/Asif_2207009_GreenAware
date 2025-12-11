package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminSeeder {

    public static void main(String[] args) {
        try {
            createDefaultAdmin("admin", "admin123");
        } catch (Exception e) {
            System.err.println("❌ Error creating admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createDefaultAdmin(String username, String password) throws Exception {
        String query = "INSERT OR IGNORE INTO admins (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }
    }
}
