package services;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import dao.ActionDAO;
import dao.IncidentDAO;
import dao.ReportDAO;
import dao.WorkerDAO;
import javafx.collections.ObservableList;
import models.Action;
import models.Incident;
import models.Worker;

public class DataLoadingService {
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;

    private final IncidentDAO incidentDAO;
    private final ActionDAO actionDAO;
    private final ReportDAO reportDAO;
    private final WorkerDAO workerDAO;

    public DataLoadingService() {
        this.executorService = Executors.newFixedThreadPool(4);
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);

        this.workerDAO = new WorkerDAO();
        this.incidentDAO = new IncidentDAO();
        this.actionDAO = new ActionDAO();
        this.reportDAO = new ReportDAO();
    }

    public Future<ObservableList<Worker>> loadWorkers() {
        Callable<ObservableList<Worker>> task = () -> {
            return workerDAO.getAllWorkers();
        };
        return executorService.submit(task);
    }

    public Future<ObservableList<Action>> loadActions() {
        Callable<ObservableList<Action>> task = () -> {
            return actionDAO.getAllActions();
        };
        return executorService.submit(task);
    }

    public Future<ObservableList<Incident>> loadIncidents() {
        Callable<ObservableList<Incident>> task = () -> {
            return incidentDAO.getAllIncidents();
        };
        return executorService.submit(task);
    }
}
