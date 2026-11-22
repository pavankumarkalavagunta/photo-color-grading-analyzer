package com.photoanalyzer.dao;

import com.photoanalyzer.model.AnalysisReport;
import com.photoanalyzer.util.DBConnection;
import com.photoanalyzer.util.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe DAO for storing and retrieving AnalysisReports.
 * Leverages JDBC (AnalysisJdbcDAO) as primary persistent store with in-memory caching.
 */
public class AnalysisDAO {

    private static volatile AnalysisDAO instance;
    private final AnalysisJdbcDAO jdbcDAO = new AnalysisJdbcDAO();
    private final Map<String, AnalysisReport> storage = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private Path dataFilePath;

    private AnalysisDAO() {
        // Initialize file fallback
        String userHome = System.getProperty("user.home", ".");
        Path baseDir = Path.of(userHome, ".photoanalyzer");
        try {
            Files.createDirectories(baseDir);
            this.dataFilePath = baseDir.resolve("reports.json");
            loadFromFile();
        } catch (Exception e) {
            this.dataFilePath = null;
        }

        // Pre-load from JDBC database if available
        try {
            List<AnalysisReport> dbReports = jdbcDAO.getAll();
            if (dbReports != null && !dbReports.isEmpty()) {
                for (AnalysisReport r : dbReports) {
                    if (r.getId() != null) {
                        storage.put(r.getId(), r);
                    }
                }
                System.out.println("[AnalysisDAO] Pre-loaded " + dbReports.size() + " reports from JDBC database.");
            }
        } catch (Exception e) {
            System.err.println("[AnalysisDAO] Could not pre-load from JDBC: " + e.getMessage());
        }
    }

    public static AnalysisDAO getInstance() {
        if (instance == null) {
            synchronized (AnalysisDAO.class) {
                if (instance == null) {
                    instance = new AnalysisDAO();
                }
            }
        }
        return instance;
    }

    public void save(AnalysisReport report) {
        if (report == null || report.getId() == null) return;
        rwLock.writeLock().lock();
        try {
            storage.put(report.getId(), report);
            saveToFile();

            // Persist via JDBC PreparedStatement
            boolean jdbcSuccess = jdbcDAO.save(report);
            if (jdbcSuccess) {
                System.out.println("[AnalysisDAO] Report " + report.getId() + " saved to JDBC database.");
            }
        } catch (Exception e) {
            System.err.println("[AnalysisDAO] Note on JDBC save: " + e.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public AnalysisReport getById(String id) {
        if (id == null) return null;
        rwLock.readLock().lock();
        try {
            // Try JDBC first
            try {
                AnalysisReport fromDb = jdbcDAO.getById(id);
                if (fromDb != null) {
                    storage.put(fromDb.getId(), fromDb);
                    return fromDb;
                }
            } catch (Exception ignored) {}

            return storage.get(id);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<AnalysisReport> getAll() {
        rwLock.readLock().lock();
        try {
            // Try fetching from JDBC
            try {
                List<AnalysisReport> fromDb = jdbcDAO.getAll();
                if (fromDb != null && !fromDb.isEmpty()) {
                    for (AnalysisReport r : fromDb) {
                        if (r.getId() != null) storage.put(r.getId(), r);
                    }
                    return fromDb;
                }
            } catch (Exception ignored) {}

            List<AnalysisReport> list = new ArrayList<>(storage.values());
            list.sort((a, b) -> {
                String ca = a.getCreatedAt() != null ? a.getCreatedAt() : "";
                String cb = b.getCreatedAt() != null ? b.getCreatedAt() : "";
                return cb.compareTo(ca);
            });
            return list;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean delete(String id) {
        if (id == null) return false;
        rwLock.writeLock().lock();
        try {
            boolean jdbcDeleted = false;
            try {
                jdbcDeleted = jdbcDAO.delete(id);
            } catch (Exception ignored) {}

            AnalysisReport removed = storage.remove(id);
            saveToFile();
            return (removed != null) || jdbcDeleted;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public int count() {
        try {
            int c = jdbcDAO.count();
            if (c > 0) return c;
        } catch (Exception ignored) {}
        return storage.size();
    }

    private void saveToFile() {
        if (dataFilePath == null) return;
        try {
            List<AnalysisReport> list = new ArrayList<>(storage.values());
            String json = JsonUtil.toJson(list);
            Path temp = dataFilePath.resolveSibling(dataFilePath.getFileName() + ".tmp");
            Files.writeString(temp, json, StandardCharsets.UTF_8);
            Files.move(temp, dataFilePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        if (dataFilePath == null || !Files.exists(dataFilePath)) return;
        try {
            String json = Files.readString(dataFilePath, StandardCharsets.UTF_8);
            Object parsed = JsonUtil.parse(json);
            if (parsed instanceof List) {
                List<Object> list = (List<Object>) parsed;
                for (Object item : list) {
                    if (item instanceof Map) {
                        AnalysisReport report = JsonUtil.mapToReport(JsonUtil.toJson(item));
                        if (report.getId() != null) {
                            storage.put(report.getId(), report);
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
