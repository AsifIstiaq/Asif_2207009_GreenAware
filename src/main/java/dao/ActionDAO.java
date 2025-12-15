package dao;

import models.Action;
import utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ActionDAO {

    public ObservableList<Action> getAllActions() throws SQLException {
        ObservableList<Action> actions = FXCollections.observableArrayList();
        String query = """
            SELECT a.*
            FROM actions a
            ORDER BY a.deadline DESC
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                actions.add(new Action(
                        rs.getInt("id"),
                        rs.getInt("incident_id"),
                        rs.getInt("worker_id"),
                        rs.getString("action_note"),
                        rs.getString("deadline"),
                        rs.getString("status"),
                        rs.getString("resolution_details"),
                        rs.getString("completed_date"),
                        rs.getString("created_at"),
                        rs.getString("worker_name")
                ));
            }
        }
        return actions;
    }

    public ObservableList<Action> getActionsByIncidentId(int incidentId) throws SQLException {
        ObservableList<Action> actions = FXCollections.observableArrayList();
        String query = """
            SELECT a.*
            FROM actions a
            WHERE a.incident_id = ?
            ORDER BY a.created_at DESC
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, incidentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                actions.add(new Action(
                        rs.getInt("id"),
                        rs.getInt("incident_id"),
                        rs.getInt("worker_id"),
                        rs.getString("action_note"),
                        rs.getString("deadline"),
                        rs.getString("status"),
                        rs.getString("resolution_details"),
                        rs.getString("completed_date"),
                        rs.getString("created_at"),
                        rs.getString("worker_name")
                ));
            }
        }
        return actions;
    }

    public void insertAction(Action action) throws SQLException {
        String query = """
            INSERT INTO actions (incident_id, worker_id, worker_name, action_note, deadline, status,
            resolution_details, completed_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, action.getIncidentId());
            pstmt.setInt(2, action.getWorkerId());
            pstmt.setString(3, action.getWorkerName());
            pstmt.setString(4, action.getActionNote());
            pstmt.setString(5, action.getDeadline());
            pstmt.setString(6, action.getStatus());
            pstmt.setString(7, action.getResolutionDetails());
            pstmt.setString(8, action.getCompletedDate());
            pstmt.executeUpdate();
        }
    }

    public void updateAction(Action action) throws SQLException {
        String query = """
            UPDATE actions SET incident_id = ?, worker_id = ?, worker_name = ?, action_note = ?, deadline = ?,
            status = ?, resolution_details = ?, completed_date = ? WHERE id = ?
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, action.getIncidentId());
            pstmt.setInt(2, action.getWorkerId());
            pstmt.setString(3, action.getWorkerName());
            pstmt.setString(4, action.getActionNote());
            pstmt.setString(5, action.getDeadline());
            pstmt.setString(6, action.getStatus());
            pstmt.setString(7, action.getResolutionDetails());
            pstmt.setString(8, action.getCompletedDate());
            pstmt.setInt(9, action.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteAction(int id) throws SQLException {
        String query = "DELETE FROM actions WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
