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
//import models.Worker;
//import utils.DatabaseHelper;
//
//public class WorkerDAO {
//
//    public ObservableList<Worker> getAllWorkers() throws SQLException {
//        ObservableList<Worker> workers = FXCollections.observableArrayList();
//        String query = "SELECT * FROM workers ORDER BY name";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                workers.add(new Worker(
//                        rs.getInt("id"),
//                        rs.getString("name"),
//                        rs.getString("phone"),
//                        rs.getString("email"),
//                        rs.getString("username"),
//                        rs.getString("password"),
//                        rs.getString("specialization"),
//                        rs.getString("status"),
//                        rs.getString("created_at")
//                ));
//            }
//        }
//        return workers;
//    }
//
//    public void insertWorker(Worker worker) throws SQLException {
//        String query = "INSERT INTO workers (name, phone, email, username, password, specialization, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, worker.getName());
//            pstmt.setString(2, worker.getPhone());
//            pstmt.setString(3, worker.getEmail());
//            pstmt.setString(4, worker.getUsername());
//            pstmt.setString(5, worker.getPassword());
//            pstmt.setString(6, worker.getSpecialization());
//            pstmt.setString(7, worker.getStatus());
//            pstmt.executeUpdate();
//        }
//    }
//
//    public void updateWorker(Worker worker) throws SQLException {
//        String query = "UPDATE workers SET name = ?, phone = ?, email = ?, username = ?, password = ?, specialization = ?, status = ? WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, worker.getName());
//            pstmt.setString(2, worker.getPhone());
//            pstmt.setString(3, worker.getEmail());
//            pstmt.setString(4, worker.getUsername());
//            pstmt.setString(5, worker.getPassword());
//            pstmt.setString(6, worker.getSpecialization());
//            pstmt.setString(7, worker.getStatus());
//            pstmt.setInt(8, worker.getId());
//            pstmt.executeUpdate();
//        }
//    }
//
//    public void deleteWorker(int id) throws SQLException {
//        String query = "DELETE FROM workers WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, id);
//            pstmt.executeUpdate();
//        }
//    }
//
//    public Worker getWorkerById(int id) throws SQLException {
//        String query = "SELECT * FROM workers WHERE id = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setInt(1, id);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                return new Worker(
//                        rs.getInt("id"),
//                        rs.getString("name"),
//                        rs.getString("phone"),
//                        rs.getString("email"),
//                        rs.getString("username"),
//                        rs.getString("password"),
//                        rs.getString("specialization"),
//                        rs.getString("status"),
//                        rs.getString("created_at")
//                );
//            }
//        }
//        return null;
//    }
//
//    public Worker authenticateWorker(String username, String password) throws SQLException {
//        String query = "SELECT * FROM workers WHERE username = ? AND password = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, username);
//            pstmt.setString(2, password);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                return new Worker(
//                        rs.getInt("id"),
//                        rs.getString("name"),
//                        rs.getString("phone"),
//                        rs.getString("email"),
//                        rs.getString("username"),
//                        rs.getString("password"),
//                        rs.getString("specialization"),
//                        rs.getString("status"),
//                        rs.getString("created_at")
//                );
//            }
//        }
//        return null;
//    }
//
//    public boolean emailExists(String email) throws SQLException {
//        String query = "SELECT COUNT(*) FROM workers WHERE email = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, email);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                return rs.getInt(1) > 0;
//            }
//        }
//        return false;
//    }
//
//    public boolean usernameExists(String username) throws SQLException {
//        String query = "SELECT COUNT(*) FROM workers WHERE username = ?";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, username);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                return rs.getInt(1) > 0;
//            }
//        }
//        return false;
//    }
//}
