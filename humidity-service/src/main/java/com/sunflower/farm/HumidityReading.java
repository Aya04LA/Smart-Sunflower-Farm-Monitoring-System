package com.sunflower.farm;

import java.time.LocalDateTime;

/**
 * Model class representing a humidity reading from the sensor
 */
public class HumidityReading {
    private int id;
    private double soilMoisture;  // percentage (0-100)
    private double airHumidity;   // percentage (0-100)
    private String sensorId;
    private LocalDateTime timestamp;
    private String status;  // "NORMAL", "WARNING", "CRITICAL"

    // Default constructor
    public HumidityReading() {
        this.timestamp = LocalDateTime.now();
        this.sensorId = "HUMIDITY-001";
    }

    // Constructor with values
    public HumidityReading(double soilMoisture, double airHumidity) {
        this();
        this.soilMoisture = soilMoisture;
        this.airHumidity = airHumidity;
        this.status = determineStatus();
    }

    // Determine status based on optimal sunflower conditions
    private String determineStatus() {
        // Optimal: Soil 60-80%, Air 40-70%
        if (soilMoisture >= 60 && soilMoisture <= 80 &&
                airHumidity >= 40 && airHumidity <= 70) {
            return "NORMAL";
        } else if (soilMoisture < 50 || soilMoisture > 85 ||
                airHumidity < 30 || airHumidity > 80) {
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

    public double getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(double soilMoisture) {
        this.soilMoisture = soilMoisture;
        this.status = determineStatus();
    }

    public double getAirHumidity() {
        return airHumidity;
    }

    public void setAirHumidity(double airHumidity) {
        this.airHumidity = airHumidity;
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
        return "HumidityReading{" +
                "id=" + id +
                ", soilMoisture=" + soilMoisture +
                ", airHumidity=" + airHumidity +
                ", sensorId='" + sensorId + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}