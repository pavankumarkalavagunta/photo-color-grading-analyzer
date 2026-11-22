package com.photoanalyzer.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * JDBC Connection Manager for Photo Color Grading Analyzer.
 * Supports Oracle Database 10g/11g/19c XE and MySQL via standard JDBC.
 */
public class DBConnection {

    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String url = "jdbc:oracle:thin:@localhost:1521:XE";
    private static String user = "wildlife";
    private static String password = "wildlife";

    static {
        // Load configuration from db.properties if present on classpath
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                driver = props.getProperty("db.driver", driver);
                url = props.getProperty("db.url", url);
                user = props.getProperty("db.user", user);
                password = props.getProperty("db.password", password);
            }
        } catch (Exception ignored) {
        }

        // Register JDBC driver
        try {
            Class.forName(driver);
            System.out.println("[DBConnection] JDBC Driver registered: " + driver);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] JDBC Driver not found: " + driver + " - " + e.getMessage());
        }

        // Auto-initialize schema
        initDatabase();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("[DBConnection] Connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static void initDatabase() {
        String createTableSQL = "CREATE TABLE PHOTO_ANALYSIS_REPORTS ("
                + "ID VARCHAR2(64) PRIMARY KEY, "
                + "FILENAME VARCHAR2(255), "
                + "WIDTH NUMBER, "
                + "HEIGHT NUMBER, "
                + "FORMAT VARCHAR2(32), "
                + "FILE_SIZE NUMBER, "
                + "HAS_ALPHA NUMBER(1), "
                + "IS_GRAYSCALE NUMBER(1), "
                + "MEAN_BRIGHTNESS NUMBER, "
                + "NORM_BRIGHTNESS NUMBER, "
                + "BRIGHTNESS_LABEL VARCHAR2(64), "
                + "CONTRAST_STD_DEV NUMBER, "
                + "NORM_CONTRAST NUMBER, "
                + "CONTRAST_LABEL VARCHAR2(64), "
                + "MEAN_SATURATION NUMBER, "
                + "NORM_SATURATION NUMBER, "
                + "SATURATION_LABEL VARCHAR2(64), "
                + "WARMTH_INDEX NUMBER, "
                + "TEMP_LABEL VARCHAR2(64), "
                + "SHARPNESS_SCORE NUMBER, "
                + "SHARPNESS_LABEL VARCHAR2(64), "
                + "NOISE_SCORE NUMBER, "
                + "NOISE_LABEL VARCHAR2(64), "
                + "CLARITY_SCORE NUMBER, "
                + "KEY_STYLE VARCHAR2(64), "
                + "EV_STOPS NUMBER, "
                + "DYNAMIC_RANGE_LABEL VARCHAR2(64), "
                + "VIGNETTE_FALLOFF_PCT NUMBER, "
                + "VIGNETTE_LABEL VARCHAR2(64), "
                + "HARMONY_TYPE VARCHAR2(64), "
                + "PROCESSING_TIME NUMBER, "
                + "CREATED_AT VARCHAR2(64), "
                + "REPORT_JSON CLOB, "
                + "IMAGE_DATA_URL CLOB"
                + ")";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("[DBConnection] Table PHOTO_ANALYSIS_REPORTS created successfully.");
        } catch (SQLException e) {
            // ORA-00955: name is already used by an existing object (Table already exists)
            if (e.getErrorCode() == 955 || (e.getMessage() != null && e.getMessage().contains("ORA-00955"))) {
                System.out.println("[DBConnection] Table PHOTO_ANALYSIS_REPORTS already exists.");
            } else {
                System.err.println("[DBConnection] Note on table initialization: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Could not initialize database schema: " + e.getMessage());
        }
    }

    public static void close(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
