package controllers;

import dao.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.AnalyticsService;
import services.DataLoadingService;

import java.util.Map;
import java.util.concurrent.Future;

public class DashboardController {

    @FXML private Label totalIncidentsLabel;
    @FXML private Label activeIncidentsLabel;
    @FXML private Label resolvedIncidentsLabel;
    @FXML private PieChart categoryPieChart;
    @FXML private BarChart<String, Number> severityBarChart;
    @FXML private TableView<LocationStat> pollutedAreasTable;
    @FXML private TableColumn<LocationStat, Integer> rankColumn;
    @FXML private TableColumn<LocationStat, String> locationColumn;
    @FXML private TableColumn<LocationStat, Integer> countColumn;
    @FXML private ProgressIndicator loadingIndicator;

    private DataLoadingService dataService;
    private AnalyticsService analyticsService;

    @FXML
    public void initialize() {
        dataService = new DataLoadingService();
        analyticsService = new AnalyticsService();

        setupTables();
        refreshDashboard();
    }

    private void setupTables() {
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
    }

    @FXML
    public void refreshDashboard() {
        loadingIndicator.setVisible(true);

        Future<DataLoadingService.DashboardStats> statsFuture = dataService.loadDashboardStats();

        new Thread(() -> {
            try {
                DataLoadingService.DashboardStats stats = statsFuture.get();

                Platform.runLater(() -> {
                    totalIncidentsLabel.setText(String.valueOf(stats.totalIncidents));
                    activeIncidentsLabel.setText(String.valueOf(stats.activeIncidents));
                    resolvedIncidentsLabel.setText(String.valueOf(stats.resolvedIncidents));

                    loadingIndicator.setVisible(false);
                });

                loadCategoryDistribution();
                loadSeverityDistribution();
                loadPollutedAreas();

            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    showError("Error loading dashboard data: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void loadCategoryDistribution() {
        Future<Map<String, Integer>> future = analyticsService.calculateCategoryDistribution();

        new Thread(() -> {
            try {
                Map<String, Integer> distribution = future.get();

                Platform.runLater(() -> {
                    ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                    for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
                        if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                        }
                    }
                    categoryPieChart.setData(pieData);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadSeverityDistribution() {
        Future<Map<String, Integer>> future = analyticsService.calculateSeverityDistribution();

        new Thread(() -> {
            try {
                Map<String, Integer> distribution = future.get();

                Platform.runLater(() -> {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Severity");

                    for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
                        if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                        }
                    }

                    severityBarChart.getData().clear();
                    severityBarChart.getData().add(series);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadPollutedAreas() {
        Future<java.util.List<AnalyticsService.LocationStats>> future =
                analyticsService.getMostPollutedAreas(5);

        new Thread(() -> {
            try {
                java.util.List<AnalyticsService.LocationStats> areas = future.get();

                Platform.runLater(() -> {
                    ObservableList<LocationStat> data = FXCollections.observableArrayList();
                    int rank = 1;
                    for (AnalyticsService.LocationStats stat : areas) {
                        data.add(new LocationStat(rank++, stat.location, stat.count));
                    }
                    pollutedAreasTable.setItems(data);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Dashboard Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class LocationStat {
        private final int rank;
        private final String location;
        private final int count;

        public LocationStat(int rank, String location, int count) {
            this.rank = rank;
            this.location = location;
            this.count = count;
        }

        public int getRank() { return rank; }
        public String getLocation() { return location; }
        public int getCount() { return count; }
    }
}
