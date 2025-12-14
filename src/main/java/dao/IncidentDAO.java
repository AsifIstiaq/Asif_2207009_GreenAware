package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Incident;
import utils.DatabaseHelper;

public class IncidentDAO {

    public ObservableList<Incident> getAllIncidents() throws SQLException {
        ObservableList<Incident> incidents = FXCollections.observableArrayList();
        String query = """
            SELECT i.*, w.name as worker_name
            FROM incidents i
            LEFT JOIN workers w ON i.assigned_worker_id = w.id
            ORDER BY i.date DESC
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                incidents.add(new Incident(
                        rs.getInt("id"),
                        rs.getString("incident_type"),
                        rs.getString("location"),
                        rs.getString("date"),
                        rs.getString("severity"),
                        rs.getString("reporter_name"),
                        rs.getString("reporter_contact"),
                        rs.getString("image_path"),
                        rs.getString("status"),
                        rs.getString("description"),
                        rs.getInt("assigned_worker_id"),
                        rs.getString("created_at"),
                        rs.getString("worker_name")
                ));
            }
        }
        return incidents;
    }

    public void insertIncident(Incident incident) throws SQLException {
        String query = """
            INSERT INTO incidents (incident_type, location, date, severity, reporter_name,
            reporter_contact, image_path, status, description, assigned_worker_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, incident.getIncidentType());
            pstmt.setString(2, incident.getLocation());
            pstmt.setString(3, incident.getDate());
            pstmt.setString(4, incident.getSeverity());
            pstmt.setString(5, incident.getReporterName());
            pstmt.setString(6, incident.getReporterContact());
            pstmt.setString(7, incident.getImagePath());
            pstmt.setString(8, incident.getStatus());
            pstmt.setString(9, incident.getDescription());
            pstmt.setInt(10, incident.getAssignedWorkerId());
            pstmt.executeUpdate();
        }
    }

    public void updateIncident(Incident incident) throws SQLException {
        String query = """
            UPDATE incidents SET incident_type = ?, location = ?, date = ?, severity = ?,
            reporter_name = ?, reporter_contact = ?, image_path = ?, status = ?,
            description = ?, assigned_worker_id = ? WHERE id = ?
        """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, incident.getIncidentType());
            pstmt.setString(2, incident.getLocation());
            pstmt.setString(3, incident.getDate());
            pstmt.setString(4, incident.getSeverity());
            pstmt.setString(5, incident.getReporterName());
            pstmt.setString(6, incident.getReporterContact());
            pstmt.setString(7, incident.getImagePath());
            pstmt.setString(8, incident.getStatus());
            pstmt.setString(9, incident.getDescription());
            pstmt.setInt(10, incident.getAssignedWorkerId());
            pstmt.setInt(11, incident.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteIncident(int id) throws SQLException {
        String query = "DELETE FROM incidents WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
