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
//import models.WorkProgress;
//import utils.DatabaseHelper;
//
//public class WorkProgressDAO {
//
//    public ObservableList<WorkProgress> getAllProgress() throws SQLException {
//        ObservableList<WorkProgress> progressList = FXCollections.observableArrayList();
//        String query = "SELECT * FROM work_progress ORDER BY submitted_at DESC";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                progressList.add(new WorkProgress(
//                        rs.getInt("id"),
//                        rs.getInt("action_id"),
//                        rs.getInt("worker_id"),
//                        rs.getString("worker_name"),
//                        rs.getString("worker_phone"),
//                        rs.getString("description"),
//                        rs.getString("photo_path"),
//                        rs.getString("location"),
//                        rs.getString("status"),
//                        rs.getString("submitted_at")
//                ));
//            }
//        }
//        return progressList;
//    }
//
//    public ObservableList<WorkProgress> getProgressByWorkerId(int workerId) throws SQLException {
//        ObservableList<WorkProgress> progressList = FXCollections.observableArrayList();
//        String query = "SELECT * FROM work_progress WHERE worker_id = ? ORDER BY submitted_at DESC";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, workerId);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                progressList.add(new WorkProgress(
//                        rs.getInt("id"),
//                        rs.getInt("action_id"),
//                        rs.getInt("worker_id"),
//                        rs.getString("worker_name"),
//                        rs.getString("worker_phone"),
//                        rs.getString("description"),
//                        rs.getString("photo_path"),
//                        rs.getString("location"),
//                        rs.getString("status"),
//                        rs.getString("submitted_at")
//                ));
//            }
//        }
//        return progressList;
//    }
//
//    public ObservableList<WorkProgress> getProgressByActionId(int actionId) throws SQLException {
//        ObservableList<WorkProgress> progressList = FXCollections.observableArrayList();
//        String query = "SELECT * FROM work_progress WHERE action_id = ? ORDER BY submitted_at DESC";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, actionId);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                progressList.add(new WorkProgress(
//                        rs.getInt("id"),
//                        rs.getInt("action_id"),
//                        rs.getInt("worker_id"),
//                        rs.getString("worker_name"),
//                        rs.getString("worker_phone"),
//                        rs.getString("description"),
//                        rs.getString("photo_path"),
//                        rs.getString("location"),
//                        rs.getString("status"),
//                        rs.getString("submitted_at")
//                ));
//            }
//        }
//        return progressList;
//    }
//
//    public void insertProgress(WorkProgress progress) throws SQLException {
//        String query = "INSERT INTO work_progress (action_id, worker_id, worker_name, worker_phone, description, photo_path, location, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, progress.getActionId());
//            pstmt.setInt(2, progress.getWorkerId());
//            pstmt.setString(3, progress.getWorkerName());
//            pstmt.setString(4, progress.getWorkerPhone());
//            pstmt.setString(5, progress.getDescription());
//            pstmt.setString(6, progress.getPhotoPath());
//            pstmt.setString(7, progress.getLocation());
//            pstmt.setString(8, progress.getStatus());
//            pstmt.executeUpdate();
//        }
//    }
//
//    public void updateProgress(WorkProgress progress) throws SQLException {
//        String query = "UPDATE work_progress SET description = ?, photo_path = ?, status = ? WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, progress.getDescription());
//            pstmt.setString(2, progress.getPhotoPath());
//            pstmt.setString(3, progress.getStatus());
//            pstmt.setInt(4, progress.getId());
//            pstmt.executeUpdate();
//        }
//    }
//
//    public WorkProgress getLatestProgressByActionId(int actionId) throws SQLException {
//        String query = "SELECT * FROM work_progress WHERE action_id = ? ORDER BY submitted_at DESC LIMIT 1";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, actionId);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                return new WorkProgress(
//                        rs.getInt("id"),
//                        rs.getInt("action_id"),
//                        rs.getInt("worker_id"),
//                        rs.getString("worker_name"),
//                        rs.getString("worker_phone"),
//                        rs.getString("description"),
//                        rs.getString("photo_path"),
//                        rs.getString("location"),
//                        rs.getString("status"),
//                        rs.getString("submitted_at")
//                );
//            }
//        }
//        return null;
//    }
//
//    public void deleteProgress(int id) throws SQLException {
//        String query = "DELETE FROM work_progress WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, id);
//            pstmt.executeUpdate();
//        }
//    }
//}
