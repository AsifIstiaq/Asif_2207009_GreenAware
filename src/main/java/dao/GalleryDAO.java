//package dao;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import models.GalleryItem;
//import utils.DatabaseHelper;
//
//public class GalleryDAO {
//
//    public ObservableList<GalleryItem> getAllImagesWithDetails() throws SQLException {
//        ObservableList<GalleryItem> galleryItems = FXCollections.observableArrayList();
//
//        String query = """
//            SELECT r.id as report_id, r.image_path, r.location,
//                   u.full_name as reporter_name, r.date_reported, r.category_name
//            FROM reports r
//            LEFT JOIN users u ON r.user_id = u.id
//            WHERE r.image_path IS NOT NULL AND r.image_path != ''
//            ORDER BY r.created_at DESC
//        """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                galleryItems.add(new GalleryItem(
//                        rs.getInt("report_id"),
//                        rs.getString("image_path"),
//                        rs.getString("location"),
//                        rs.getString("reporter_name"),
//                        rs.getString("date_reported"),
//                        rs.getString("category_name")
//                ));
//            }
//        }
//
//        return galleryItems;
//    }
//}
