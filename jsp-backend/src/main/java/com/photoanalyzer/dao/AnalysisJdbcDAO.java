package com.photoanalyzer.dao;

import com.photoanalyzer.model.AnalysisReport;
import com.photoanalyzer.util.DBConnection;
import com.photoanalyzer.util.JsonUtil;

import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC DAO Implementation for Photo Color Grading Analyzer.
 * Uses PreparedStatement, ResultSet, and Connection management.
 */
public class AnalysisJdbcDAO {

    public boolean save(AnalysisReport report) {
        if (report == null || report.getId() == null) return false;

        String deleteOldSQL = "DELETE FROM PHOTO_ANALYSIS_REPORTS WHERE ID = ?";
        String insertSQL = "INSERT INTO PHOTO_ANALYSIS_REPORTS ("
                + "ID, FILENAME, WIDTH, HEIGHT, FORMAT, FILE_SIZE, HAS_ALPHA, IS_GRAYSCALE, "
                + "MEAN_BRIGHTNESS, NORM_BRIGHTNESS, BRIGHTNESS_LABEL, CONTRAST_STD_DEV, NORM_CONTRAST, CONTRAST_LABEL, "
                + "MEAN_SATURATION, NORM_SATURATION, SATURATION_LABEL, WARMTH_INDEX, TEMP_LABEL, "
                + "SHARPNESS_SCORE, SHARPNESS_LABEL, NOISE_SCORE, NOISE_LABEL, CLARITY_SCORE, "
                + "KEY_STYLE, EV_STOPS, DYNAMIC_RANGE_LABEL, VIGNETTE_FALLOFF_PCT, VIGNETTE_LABEL, "
                + "HARMONY_TYPE, PROCESSING_TIME, CREATED_AT, REPORT_JSON, IMAGE_DATA_URL"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psDel = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete existing if updating
            psDel = conn.prepareStatement(deleteOldSQL);
            psDel.setString(1, report.getId());
            psDel.executeUpdate();

            // Insert new record
            ps = conn.prepareStatement(insertSQL);
            int idx = 1;
            ps.setString(idx++, report.getId());
            ps.setString(idx++, report.getFilename());
            ps.setInt(idx++, report.getWidth());
            ps.setInt(idx++, report.getHeight());
            ps.setString(idx++, report.getFormat());
            ps.setLong(idx++, report.getFileSize());
            ps.setInt(idx++, report.isHasAlpha() ? 1 : 0);
            ps.setInt(idx++, report.isGrayscale() ? 1 : 0);

            ps.setDouble(idx++, report.getMeanBrightness());
            ps.setDouble(idx++, report.getNormalizedBrightness());
            ps.setString(idx++, report.getBrightnessLabel());

            ps.setDouble(idx++, report.getContrastStdDev());
            ps.setDouble(idx++, report.getNormalizedContrast());
            ps.setString(idx++, report.getContrastLabel());

            ps.setDouble(idx++, report.getMeanSaturation());
            ps.setDouble(idx++, report.getNormalizedSaturation());
            ps.setString(idx++, report.getSaturationLabel());

            ps.setDouble(idx++, report.getWarmthIndex());
            ps.setString(idx++, report.getTemperatureLabel());

            ps.setDouble(idx++, report.getSharpnessScore());
            ps.setString(idx++, report.getSharpnessLabel());

            ps.setDouble(idx++, report.getNoiseScore());
            ps.setString(idx++, report.getNoiseLabel());

            ps.setDouble(idx++, report.getClarityScore());
            ps.setString(idx++, report.getKeyStyle());
            ps.setDouble(idx++, report.getEvStops());
            ps.setString(idx++, report.getDynamicRangeLabel());
            ps.setDouble(idx++, report.getVignetteFalloffPct());
            ps.setString(idx++, report.getVignetteLabel());
            ps.setString(idx++, report.getHarmonyType());
            ps.setDouble(idx++, report.getProcessingTimeSeconds());
            ps.setString(idx++, report.getCreatedAt());

            // Full JSON serialized report
            String reportJson = JsonUtil.toJson(report);
            if (reportJson != null) {
                ps.setCharacterStream(idx++, new StringReader(reportJson), reportJson.length());
            } else {
                ps.setNull(idx++, Types.CLOB);
            }

            // Image Data URL
            String imgUrl = report.getImageDataUrl();
            if (imgUrl != null) {
                ps.setCharacterStream(idx++, new StringReader(imgUrl), imgUrl.length());
            } else {
                ps.setNull(idx++, Types.CLOB);
            }

            ps.executeUpdate();
            conn.commit();
            return true;

        } catch (Exception e) {
            System.err.println("[AnalysisJdbcDAO] Error saving report: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignored) {}
            }
            return false;
        } finally {
            DBConnection.close(psDel, ps, conn);
        }
    }

    public AnalysisReport getById(String id) {
        if (id == null) return null;
        String sql = "SELECT REPORT_JSON, IMAGE_DATA_URL FROM PHOTO_ANALYSIS_REPORTS WHERE ID = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id.trim());
            rs = ps.executeQuery();

            if (rs.next()) {
                String reportJson = readClob(rs, "REPORT_JSON");
                String imgDataUrl = readClob(rs, "IMAGE_DATA_URL");

                if (reportJson != null && !reportJson.isBlank()) {
                    AnalysisReport report = JsonUtil.mapToReport(reportJson);
                    if (imgDataUrl != null && !imgDataUrl.isBlank()) {
                        report.setImageDataUrl(imgDataUrl);
                    }
                    return report;
                }
            }
        } catch (Exception e) {
            System.err.println("[AnalysisJdbcDAO] Error fetching by ID: " + e.getMessage());
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return null;
    }

    public List<AnalysisReport> getAll() {
        List<AnalysisReport> list = new ArrayList<>();
        String sql = "SELECT REPORT_JSON, IMAGE_DATA_URL FROM PHOTO_ANALYSIS_REPORTS ORDER BY CREATED_AT DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String reportJson = readClob(rs, "REPORT_JSON");
                String imgDataUrl = readClob(rs, "IMAGE_DATA_URL");

                if (reportJson != null && !reportJson.isBlank()) {
                    try {
                        AnalysisReport report = JsonUtil.mapToReport(reportJson);
                        if (imgDataUrl != null && !imgDataUrl.isBlank()) {
                            report.setImageDataUrl(imgDataUrl);
                        }
                        list.add(report);
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            System.err.println("[AnalysisJdbcDAO] Error fetching all: " + e.getMessage());
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return list;
    }

    public boolean delete(String id) {
        if (id == null) return false;
        String sql = "DELETE FROM PHOTO_ANALYSIS_REPORTS WHERE ID = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id.trim());
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            System.err.println("[AnalysisJdbcDAO] Error deleting: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM PHOTO_ANALYSIS_REPORTS";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("[AnalysisJdbcDAO] Error counting: " + e.getMessage());
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return 0;
    }

    private String readClob(ResultSet rs, String colName) {
        try {
            Clob clob = rs.getClob(colName);
            if (clob != null) {
                Reader r = clob.getCharacterStream();
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[4096];
                int n;
                while ((n = r.read(buf)) != -1) {
                    sb.append(buf, 0, n);
                }
                return sb.toString();
            }
            return rs.getString(colName);
        } catch (Exception e) {
            try { return rs.getString(colName); } catch (Exception ex) { return null; }
        }
    }
}
