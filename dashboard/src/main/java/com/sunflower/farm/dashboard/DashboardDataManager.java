package com.sunflower.farm.dashboard;

import java.sql.*;
import java.util.*;

/**
 * Data manager for dashboard - fetches data from all sensors
 */
public class DashboardDataManager {

    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3307/sunflower_farm");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "root");

    private Connection connection;

    public DashboardDataManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        data.put("humidity", getLatestHumidity());
        data.put("temperature", getLatestTemperature());
        data.put("ph", getLatestPh());
        data.put("weather", getLatestWeather());
        data.put("alerts", getRecentAlerts(5));
        data.put("stats", getSystemStats());

        return data;
    }

    private Map<String, Object> getLatestHumidity() {
        String sql = "SELECT * FROM humidity_readings ORDER BY timestamp DESC LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("soilMoisture", rs.getDouble("soil_moisture"));
                data.put("airHumidity", rs.getDouble("air_humidity"));
                data.put("status", rs.getString("status"));
                data.put("timestamp", rs.getTimestamp("timestamp").toString());
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> getLatestTemperature() {
        String sql = "SELECT * FROM temperature_readings ORDER BY timestamp DESC LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("airTemp", rs.getDouble("air_temperature"));
                data.put("soilTemp", rs.getDouble("soil_temperature"));
                data.put("status", rs.getString("status"));
                data.put("timestamp", rs.getTimestamp("timestamp").toString());
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> getLatestPh() {
        String sql = "SELECT * FROM ph_readings ORDER BY timestamp DESC LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("phLevel", rs.getDouble("ph_level"));
                data.put("status", rs.getString("status"));
                data.put("timestamp", rs.getTimestamp("timestamp").toString());
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> getLatestWeather() {
        String sql = "SELECT * FROM weather_readings ORDER BY timestamp DESC LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("windSpeed", rs.getDouble("wind_speed"));
                data.put("rainfall", rs.getDouble("rainfall"));
                data.put("sunlightHours", rs.getDouble("sunlight_hours"));
                data.put("timestamp", rs.getTimestamp("timestamp").toString());
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, Object>> getRecentAlerts(int count) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        String sql = "SELECT * FROM alerts ORDER BY timestamp DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, count);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("alertType", rs.getString("alert_type"));
                alert.put("severity", rs.getString("severity"));
                alert.put("message", rs.getString("message"));
                alert.put("timestamp", rs.getTimestamp("timestamp").toString());
                alert.put("resolved", rs.getBoolean("resolved"));
                alerts.add(alert);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alerts;
    }

    private Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM humidity_readings");
            if (rs.next()) stats.put("totalHumidityReadings", rs.getInt("count"));

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM temperature_readings");
            if (rs.next()) stats.put("totalTemperatureReadings", rs.getInt("count"));

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM ph_readings");
            if (rs.next()) stats.put("totalPhReadings", rs.getInt("count"));

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM weather_readings");
            if (rs.next()) stats.put("totalWeatherReadings", rs.getInt("count"));

            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM alerts WHERE resolved = false");
            if (rs.next()) stats.put("unresolvedAlerts", rs.getInt("count"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    public List<Map<String, Object>> getHumidityHistory(int hours) {
        return getHistory("humidity_readings", "soil_moisture", "air_humidity", hours);
    }

    public List<Map<String, Object>> getTemperatureHistory(int hours) {
        return getHistory("temperature_readings", "air_temperature", "soil_temperature", hours);
    }

    private List<Map<String, Object>> getHistory(String table, String field1, String field2, int hours) {
        List<Map<String, Object>> history = new ArrayList<>();
        String sql = "SELECT * FROM " + table +
                " WHERE timestamp > DATE_SUB(NOW(), INTERVAL ? HOUR) ORDER BY timestamp ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, hours);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> reading = new HashMap<>();
                reading.put("value1", rs.getDouble(field1));
                reading.put("value2", rs.getDouble(field2));
                reading.put("timestamp", rs.getTimestamp("timestamp").toString());
                history.add(reading);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}