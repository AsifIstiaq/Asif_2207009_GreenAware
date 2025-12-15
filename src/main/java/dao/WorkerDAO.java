package dao;

import models.Worker;
import utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class WorkerDAO {

    public ObservableList<Worker> getAllWorkers() throws SQLException {
        ObservableList<Worker> workers = FXCollections.observableArrayList();
        String query = "SELECT * FROM workers ORDER BY name";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                workers.add(new Worker(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("status")
                ));
            }
        }
        return workers;
    }
}
