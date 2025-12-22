package services;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dao.ActionDAO;
import dao.ReportDAO;
import javafx.collections.ObservableList;
import models.Action;

public class DataLoadingService {
    private final ExecutorService executorService;

    private final ActionDAO actionDAO;
    private final ReportDAO reportDAO;

    public DataLoadingService() {
        this.executorService = Executors.newFixedThreadPool(4);
        this.actionDAO = new ActionDAO();
        this.reportDAO = new ReportDAO();
    }

    public Future<ObservableList<Action>> loadActions() {
        Callable<ObservableList<Action>> task = () -> {
            return actionDAO.getAllActions();
        };
        return executorService.submit(task);
    }

    public Future<DashboardStats> loadDashboardStats() {
        Callable<DashboardStats> task = () -> {
            DashboardStats stats = new DashboardStats();
            int pending = reportDAO.getPendingReportCount();
            int inProgress = reportDAO.getInProgressReportCount();
            int resolved = reportDAO.getResolvedReportCount();
            stats.totalIncidents = pending + inProgress + resolved;
            stats.activeIncidents = pending + inProgress;
            stats.resolvedIncidents = resolved;
            return stats;
        };
        return executorService.submit(task);
    }

    public static class DashboardStats {
        public int totalIncidents;
        public int activeIncidents;
        public int resolvedIncidents;
    }
}
