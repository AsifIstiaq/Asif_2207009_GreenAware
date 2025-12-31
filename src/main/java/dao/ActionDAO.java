//package dao;
//
//import models.Action;
//import utils.DatabaseHelper;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//
//import java.sql.*;
//
//public class ActionDAO {
//
//    public ObservableList<Action> getAllActions() throws SQLException {
//        ObservableList<Action> actions = FXCollections.observableArrayList();
//        String query = """
//            SELECT a.*
//            FROM actions a
//            ORDER BY a.deadline DESC
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                actions.add(new Action(
//                        rs.getInt("id"),
//                        rs.getInt("worker_id"),
//                        rs.getString("action_note"),
//                        rs.getString("deadline"),
//                        rs.getString("status"),
//                        rs.getString("resolution_details"),
//                        rs.getString("completed_date"),
//                        rs.getString("created_at"),
//                        rs.getString("worker_name"),
//                        rs.getString("location")
//                ));
//            }
//        }
//        return actions;
//    }
//
//    public void insertAction(Action action) throws SQLException {
//        String query = """
//            INSERT INTO actions (worker_id, worker_name, action_note, deadline, status,
//            resolution_details, completed_date, location)
//            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, action.getWorkerId());
//            pstmt.setString(2, action.getWorkerName());
//            pstmt.setString(3, action.getActionNote());
//            pstmt.setString(4, action.getDeadline());
//            pstmt.setString(5, action.getStatus());
//            pstmt.setString(6, action.getResolutionDetails());
//            pstmt.setString(7, action.getCompletedDate());
//            pstmt.setString(8, action.getLocation());
//            pstmt.executeUpdate();
//        }
//    }
//
//    public void updateAction(Action action) throws SQLException {
//        String query = """
//            UPDATE actions SET worker_id = ?, worker_name = ?, action_note = ?, deadline = ?,
//            status = ?, resolution_details = ?, completed_date = ?, location = ? WHERE id = ?
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, action.getWorkerId());
//            pstmt.setString(2, action.getWorkerName());
//            pstmt.setString(3, action.getActionNote());
//            pstmt.setString(4, action.getDeadline());
//            pstmt.setString(5, action.getStatus());
//            pstmt.setString(6, action.getResolutionDetails());
//            pstmt.setString(7, action.getCompletedDate());
//            pstmt.setString(8, action.getLocation());
//            pstmt.setInt(9, action.getId());
//            pstmt.executeUpdate();
//        }
//    }
//
//    public void deleteAction(int id) throws SQLException {
//        String query = "DELETE FROM actions WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, id);
//            pstmt.executeUpdate();
//        }
//    }
//
//    public ObservableList<Action> getActionsByWorkerId(int workerId) throws SQLException {
//        ObservableList<Action> actions = FXCollections.observableArrayList();
//        String query = """
//            SELECT a.*
//            FROM actions a
//            WHERE a.worker_id = ?
//            ORDER BY a.deadline ASC
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, workerId);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                actions.add(new Action(
//                        rs.getInt("id"),
//                        rs.getInt("worker_id"),
//                        rs.getString("action_note"),
//                        rs.getString("deadline"),
//                        rs.getString("status"),
//                        rs.getString("resolution_details"),
//                        rs.getString("completed_date"),
//                        rs.getString("created_at"),
//                        rs.getString("worker_name"),
//                        rs.getString("location")
//                ));
//            }
//        }
//        return actions;
//    }
//
//    public String getIncidentLocation(int incidentId) throws SQLException {
//        String query = "SELECT location FROM incidents WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, incidentId);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                return rs.getString("location");
//            }
//        }
//        return "Unknown";
//    }
//}
