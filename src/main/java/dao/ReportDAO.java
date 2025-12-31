//package dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import models.Report;
//import utils.DatabaseHelper;
//
//public class ReportDAO {
//
//    public void addReport(int userId, String categoryName, String incidentType, String location, String dateReported,
//                          String severity, String description, String imagePath) throws SQLException {
//        String query = """
//            INSERT INTO reports (user_id, category_name, incident_type, location, date_reported,
//            severity, description, image_path, status)
//            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, userId);
//            pstmt.setString(2, categoryName);
//            pstmt.setString(3, incidentType);
//            pstmt.setString(4, location);
//            pstmt.setString(5, dateReported);
//            pstmt.setString(6, severity);
//            pstmt.setString(7, description);
//            pstmt.setString(8, imagePath);
//            pstmt.executeUpdate();
//        }
//    }
//
//    public ObservableList<String> getAllCategories() throws SQLException {
//        ObservableList<String> categories = FXCollections.observableArrayList();
//        String query = "SELECT name FROM report_categories ORDER BY name";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                categories.add(rs.getString("name"));
//            }
//        }
//        return categories;
//    }
//
//    public ObservableList<Report> getReportsByUserId(int userId) throws SQLException {
//        ObservableList<Report> reports = FXCollections.observableArrayList();
//        String query = """
//            SELECT r.*, u.full_name as reporter_name
//            FROM reports r
//            LEFT JOIN users u ON r.user_id = u.id
//            WHERE r.user_id = ?
//            ORDER BY r.created_at DESC
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, userId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    reports.add(new Report(
//                            rs.getInt("id"),
//                            rs.getInt("user_id"),
//                            rs.getString("category_name"),
//                            rs.getString("incident_type"),
//                            rs.getString("location"),
//                            rs.getString("date_reported"),
//                            rs.getString("severity"),
//                            rs.getString("description"),
//                            rs.getString("image_path"),
//                            rs.getString("final_photo_path"),
//                            rs.getString("status"),
//                            rs.getString("reporter_name"),
//                            rs.getString("created_at")
//                    ));
//                }
//            }
//        }
//        return reports;
//    }
//
//    public ObservableList<Report> getAllReports() throws SQLException {
//        ObservableList<Report> reports = FXCollections.observableArrayList();
//        String query = """
//            SELECT r.*, u.full_name as reporter_name
//            FROM reports r
//            LEFT JOIN users u ON r.user_id = u.id
//            ORDER BY r.created_at DESC
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                reports.add(new Report(
//                        rs.getInt("id"),
//                        rs.getInt("user_id"),
//                        rs.getString("category_name"),
//                        rs.getString("incident_type"),
//                        rs.getString("location"),
//                        rs.getString("date_reported"),
//                        rs.getString("severity"),
//                        rs.getString("description"),
//                        rs.getString("image_path"),
//                        rs.getString("final_photo_path"),
//                        rs.getString("status"),
//                        rs.getString("reporter_name"),
//                        rs.getString("created_at")
//                ));
//            }
//        }
//        return reports;
//    }
//
//    public void updateReportStatus(int reportId, String status) throws SQLException {
//        String query = "UPDATE reports SET status = ? WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, status);
//            pstmt.setInt(2, reportId);
//            pstmt.executeUpdate();
//        }
//    }
//
//    public int getPendingReportCount() throws SQLException {
//        String query = "SELECT COUNT(*) FROM reports WHERE status = 'PENDING'";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            if (rs.next()) {
//                return rs.getInt(1);
//            }
//        }
//        return 0;
//    }
//
//    public int getInProgressReportCount() throws SQLException {
//        String query = "SELECT COUNT(*) FROM reports WHERE status = 'IN_PROGRESS'";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            if (rs.next()) {
//                return rs.getInt(1);
//            }
//        }
//        return 0;
//    }
//
//    public int getResolvedReportCount() throws SQLException {
//        String query = "SELECT COUNT(*) FROM reports WHERE status = 'RESOLVED'";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            if (rs.next()) {
//                return rs.getInt(1);
//            }
//        }
//        return 0;
//    }
//
//    public Report getReportById(int reportId) throws SQLException {
//        String query = """
//            SELECT r.*, u.full_name as reporter_name
//            FROM reports r
//            LEFT JOIN users u ON r.user_id = u.id
//            WHERE r.id = ?
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, reportId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return new Report(
//                            rs.getInt("id"),
//                            rs.getInt("user_id"),
//                            rs.getString("category_name"),
//                            rs.getString("incident_type"),
//                            rs.getString("location"),
//                            rs.getString("date_reported"),
//                            rs.getString("severity"),
//                            rs.getString("description"),
//                            rs.getString("image_path"),
//                            rs.getString("final_photo_path"),
//                            rs.getString("status"),
//                            rs.getString("reporter_name"),
//                            rs.getString("created_at")
//                    );
//                }
//            }
//        }
//        return null;
//    }
//
//    public void updateFinalPhoto(int reportId, String finalPhotoPath) throws SQLException {
//        String query = "UPDATE reports SET final_photo_path = ? WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, finalPhotoPath);
//            pstmt.setInt(2, reportId);
//            pstmt.executeUpdate();
//        }
//    }
//}
