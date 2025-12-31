//package dao;
//
//import java.sql.*;
//import models.User;
//import utils.DatabaseHelper;
//
//public class UserDAO {
//
//    public boolean registerUser(String fullName, String email, String username,
//                                String password, String phone) throws SQLException {
//        String query = """
//            INSERT INTO users (full_name, email, username, password, phone)
//            VALUES (?, ?, ?, ?, ?)
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, fullName);
//            pstmt.setString(2, email);
//            pstmt.setString(3, username);
//            pstmt.setString(4, password);
//            pstmt.setString(5, phone);
//            pstmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            if (e.getMessage().contains("UNIQUE constraint failed")) {
//                return false;
//            }
//            throw e;
//        }
//    }
//
//    public User validateUserLogin(String username, String password) throws SQLException {
//        String query = """
//            SELECT id, full_name, email, username, phone, created_at
//            FROM users
//            WHERE username = ? AND password = ?
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, username);
//            pstmt.setString(2, password);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return new User(
//                            rs.getInt("id"),
//                            rs.getString("full_name"),
//                            rs.getString("email"),
//                            rs.getString("username"),
//                            rs.getString("phone"),
//                            rs.getString("created_at")
//                    );
//                }
//            }
//        }
//        return null;
//    }
//
//    public boolean usernameExists(String username) throws SQLException {
//        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, username);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                return rs.next() && rs.getInt(1) > 0;
//            }
//        }
//    }
//
//    public boolean emailExists(String email) throws SQLException {
//        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, email);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                return rs.next() && rs.getInt(1) > 0;
//            }
//        }
//    }
//}
