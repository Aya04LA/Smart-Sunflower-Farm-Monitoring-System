package com.sunflower.farm.temperature;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database manager for temperature service
 */
public class DatabaseManager {

    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3307/sunflower_farm");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "root");
    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected (Temperature Service)");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }

    public boolean saveTemperatureReading(TemperatureReading reading) {
        String sql = "INSERT INTO temperature_readings (sensor_id, air_temperature, soil_temperature, status, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, reading.getSensorId());
            stmt.setDouble(2, reading.getAirTemperature());
            stmt.setDouble(3, reading.getSoilTemperature());
            stmt.setString(4, reading.getStatus());
            stmt.setTimestamp(5, Timestamp.valueOf(reading.getTimestamp()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reading.setId(generatedKeys.getInt(1));
                }
                System.out.println("💾 Temperature reading saved: " + reading.getStatus());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to save temperature reading");
            e.printStackTrace();
        }
        return false;
    }

    public List<TemperatureReading> getRecentTemperatureReadings(int count) {
        List<TemperatureReading> readings = new ArrayList<>();
        String sql = "SELECT * FROM temperature_readings ORDER BY timestamp DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, count);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TemperatureReading reading = new TemperatureReading();
                reading.setId(rs.getInt("id"));
                reading.setSensorId(rs.getString("sensor_id"));
                reading.setAirTemperature(rs.getDouble("air_temperature"));
                reading.setSoilTemperature(rs.getDouble("soil_temperature"));
                reading.setStatus(rs.getString("status"));
                reading.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                readings.add(reading);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve temperature readings");
            e.printStackTrace();
        }

        return readings;
    }

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
}