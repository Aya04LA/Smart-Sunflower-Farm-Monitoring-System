package com.sunflower.farm;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database manager for handling all database operations
 */
public class DatabaseManager {

    // Database connection parameters
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3307/sunflower_farm");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "root");

    private Connection connection;

    /**
     * Constructor - establishes database connection
     */
    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }

    /**
     * Save a humidity reading to the database
     */
    public boolean saveHumidityReading(HumidityReading reading) {
        String sql = "INSERT INTO humidity_readings (sensor_id, soil_moisture, air_humidity, status, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, reading.getSensorId());
            stmt.setDouble(2, reading.getSoilMoisture());
            stmt.setDouble(3, reading.getAirHumidity());
            stmt.setString(4, reading.getStatus());
            stmt.setTimestamp(5, Timestamp.valueOf(reading.getTimestamp()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reading.setId(generatedKeys.getInt(1));
                }
                System.out.println("💾 Saved reading to database: " + reading.getStatus());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to save reading to database");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all humidity readings from database
     */
    public List<HumidityReading> getAllHumidityReadings() {
        List<HumidityReading> readings = new ArrayList<>();
        String sql = "SELECT * FROM humidity_readings ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HumidityReading reading = new HumidityReading();
                reading.setId(rs.getInt("id"));
                reading.setSensorId(rs.getString("sensor_id"));
                reading.setSoilMoisture(rs.getDouble("soil_moisture"));
                reading.setAirHumidity(rs.getDouble("air_humidity"));
                reading.setStatus(rs.getString("status"));
                reading.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                readings.add(reading);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve readings from database");
            e.printStackTrace();
        }

        return readings;
    }

    /**
     * Get last N humidity readings
     */
    public List<HumidityReading> getRecentHumidityReadings(int count) {
        List<HumidityReading> readings = new ArrayList<>();
        String sql = "SELECT * FROM humidity_readings ORDER BY timestamp DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, count);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HumidityReading reading = new HumidityReading();
                reading.setId(rs.getInt("id"));
                reading.setSensorId(rs.getString("sensor_id"));
                reading.setSoilMoisture(rs.getDouble("soil_moisture"));
                reading.setAirHumidity(rs.getDouble("air_humidity"));
                reading.setStatus(rs.getString("status"));
                reading.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                readings.add(reading);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve recent readings");
            e.printStackTrace();
        }

        return readings;
    }

    /**
     * Get latest humidity reading
     */
    public HumidityReading getLatestHumidityReading() {
        String sql = "SELECT * FROM humidity_readings ORDER BY timestamp DESC LIMIT 1";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                HumidityReading reading = new HumidityReading();
                reading.setId(rs.getInt("id"));
                reading.setSensorId(rs.getString("sensor_id"));
                reading.setSoilMoisture(rs.getDouble("soil_moisture"));
                reading.setAirHumidity(rs.getDouble("air_humidity"));
                reading.setStatus(rs.getString("status"));
                reading.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                return reading;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve latest reading");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create an alert in the database
     */
    public boolean createAlert(String alertType, String severity, String message, String sensorId) {
        String sql = "INSERT INTO alerts (alert_type, severity, message, sensor_id, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, alertType);
            stmt.setString(2, severity);
            stmt.setString(3, message);
            stmt.setString(4, sensorId);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Failed to create alert");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete all humidity readings (for testing)
     */
    public int clearAllReadings() {
        String sql = "DELETE FROM humidity_readings";
        try (Statement stmt = connection.createStatement()) {
            int deleted = stmt.executeUpdate(sql);
            System.out.println("🗑️ Cleared " + deleted + " readings from database");
            return deleted;
        } catch (SQLException e) {
            System.err.println("❌ Failed to clear readings");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to close database connection");
            e.printStackTrace();
        }
    }

    /**
     * Test database connection
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}