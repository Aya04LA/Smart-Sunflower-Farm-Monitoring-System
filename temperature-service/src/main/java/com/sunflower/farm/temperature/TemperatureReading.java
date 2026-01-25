package com.sunflower.farm.temperature;

import java.time.LocalDateTime;

/**
 * Model class for temperature readings
 * Sunflowers need: Air 20-30°C, Soil 15-25°C
 */
public class TemperatureReading {
    private int id;
    private double airTemperature;   // Celsius
    private double soilTemperature;  // Celsius
    private String sensorId;
    private LocalDateTime timestamp;
    private String status;  // "NORMAL", "WARNING", "CRITICAL"

    // Default constructor
    public TemperatureReading() {
        this.timestamp = LocalDateTime.now();
        this.sensorId = "TEMP-001";
    }

    // Constructor with values
    public TemperatureReading(double airTemperature, double soilTemperature) {
        this();
        this.airTemperature = airTemperature;
        this.soilTemperature = soilTemperature;
        this.status = determineStatus();
    }

    // Determine status based on optimal sunflower conditions
    private String determineStatus() {
        // Optimal: Air 20-30°C, Soil 15-25°C
        if (airTemperature >= 20 && airTemperature <= 30 &&
                soilTemperature >= 15 && soilTemperature <= 25) {
            return "NORMAL";
        } else if (airTemperature < 15 || airTemperature > 35 ||
                soilTemperature < 10 || soilTemperature > 30) {
            return "CRITICAL";
        } else {
            return "WARNING";
        }
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
        this.status = determineStatus();
    }

    public double getSoilTemperature() {
        return soilTemperature;
    }

    public void setSoilTemperature(double soilTemperature) {
        this.soilTemperature = soilTemperature;
        this.status = determineStatus();
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TemperatureReading{" +
                "id=" + id +
                ", airTemp=" + airTemperature +
                ", soilTemp=" + soilTemperature +
                ", sensorId='" + sensorId + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}