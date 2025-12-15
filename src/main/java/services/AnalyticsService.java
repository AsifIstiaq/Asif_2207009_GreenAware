package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import dao.ReportDAO;
import javafx.collections.ObservableList;
import models.Report;

public class AnalyticsService {
    private final ExecutorService executorService;
    private final ReportDAO reportDAO;

    public AnalyticsService() {
        this.executorService = Executors.newFixedThreadPool(2);
        this.reportDAO = new ReportDAO();
    }

    public Future<Map<String, Integer>> calculateCategoryDistribution() {
        Callable<Map<String, Integer>> task = () -> {
            ObservableList<Report> reports = reportDAO.getAllReports();
            Map<String, Integer> distribution = new HashMap<>();

            for (Report report : reports) {
                String categoryName = report.getCategoryName();
                if (categoryName != null && !categoryName.isEmpty()) {
                    distribution.put(categoryName, distribution.getOrDefault(categoryName, 0) + 1);
                }
            }

            return distribution;
        };
        return executorService.submit(task);
    }

    public Future<Map<String, Integer>> calculateSeverityDistribution() {
        Callable<Map<String, Integer>> task = () -> {
            ObservableList<Report> reports = reportDAO.getAllReports();
            Map<String, Integer> distribution = new HashMap<>();

            for (Report report : reports) {
                String severity = report.getSeverity();
                if (severity != null && !severity.isEmpty()) {
                    distribution.put(severity, distribution.getOrDefault(severity, 0) + 1);
                }
            }

            return distribution;
        };
        return executorService.submit(task);
    }

    public Future<List<LocationStats>> getMostPollutedAreas(int topN) {
        Callable<List<LocationStats>> task = () -> {
            ObservableList<Report> reports = reportDAO.getAllReports();
            Map<String, Integer> locationCounts = new HashMap<>();

            for (Report report : reports) {
                String location = report.getLocation();
                if (location != null && !location.isEmpty()) {
                    locationCounts.put(location, locationCounts.getOrDefault(location, 0) + 1);
                }
            }

            return locationCounts.entrySet().stream()
                    .map(entry -> new LocationStats(entry.getKey(), entry.getValue()))
                    .sorted((a, b) -> Integer.compare(b.count, a.count))
                    .limit(topN)
                    .collect(Collectors.toList());
        };
        return executorService.submit(task);
    }

    public static class LocationStats {
        public String location;
        public int count;

        public LocationStats(String location, int count) {
            this.location = location;
            this.count = count;
        }
    }
}
