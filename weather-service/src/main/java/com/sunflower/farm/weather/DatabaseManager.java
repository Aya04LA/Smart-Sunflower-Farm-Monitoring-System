package com.sunflower.farm.weather;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database manager for weather service
 */
public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3307/sunflower_farm";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("🔄 Attempting to connect to database...");
            System.out.println("   URL: " + URL);
            System.out.println("   User: " + USER);

            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (this.connection != null && !this.connection.isClosed()) {
                System.out.println("✅ Database connected (Weather Service)");
            } else {
                System.err.println("❌ Connection is null or closed!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
            System.err.println("   Make sure mysql-connector-java is in your dependencies");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("   Check your password, database name, and MySQL server status");
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public boolean saveWeatherReading(WeatherReading reading) {
        String sql = "INSERT INTO weather_readings (sensor_id, wind_speed, rainfall, sunlight_hours, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, reading.getSensorId());
            stmt.setDouble(2, reading.getWindSpeed());
            stmt.setDouble(3, reading.getRainfall());
            stmt.setDouble(4, reading.getSunlightHours());
            stmt.setTimestamp(5, Timestamp.valueOf(reading.getTimestamp()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reading.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to save weather reading");
            e.printStackTrace();
        }
        return false;
    }

    public List<WeatherReading> getRecentWeatherReadings(int count) {
        List<WeatherReading> readings = new ArrayList<>();
        String sql = "SELECT * FROM weather_readings ORDER BY timestamp DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, count);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                WeatherReading reading = new WeatherReading();
                reading.setId(rs.getInt("id"));
                reading.setSensorId(rs.getString("sensor_id"));
                reading.setWindSpeed(rs.getDouble("wind_speed"));
                reading.setRainfall(rs.getDouble("rainfall"));
                reading.setSunlightHours(rs.getDouble("sunlight_hours"));
                reading.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                readings.add(reading);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve weather readings");
            e.printStackTrace();
        }

        return readings;
    }

    public List<WeatherReading> getAllWeatherReadings() {
        List<WeatherReading> readings = new ArrayList<>();
        String sql = "SELECT * FROM weather_readings ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                WeatherReading reading = new WeatherReading();
                reading.setId(rs.getInt("id"));
                reading.setSensorId(rs.getString("sensor_id"));
                reading.setWindSpeed(rs.getDouble("wind_speed"));
                reading.setRainfall(rs.getDouble("rainfall"));
                reading.setSunlightHours(rs.getDouble("sunlight_hours"));
                reading.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                readings.add(reading);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve all weather readings");
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