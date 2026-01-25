package com.sunflower.farm.ph;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model class for pH readings
 * Sunflowers need: pH 6.0-7.5 (slightly acidic to neutral)
 *
 * IMPORTANT: Must implement Serializable for RMI!
 */
public class PhReading implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private double phLevel;
    private String sensorId;
    private LocalDateTime timestamp;
    private String status;  // "NORMAL", "WARNING", "CRITICAL"
    private String soilType; // "Sandy", "Loamy", "Clay"

    // Default constructor
    public PhReading() {
        this.timestamp = LocalDateTime.now();
        this.sensorId = "PH-001";
        this.soilType = "Loamy";
    }

    // Constructor with values
    public PhReading(double phLevel, String soilType) {
        this();
        this.phLevel = phLevel;
        this.soilType = soilType;
        this.status = determineStatus();
    }

    // Determine status based on optimal sunflower conditions
    private String determineStatus() {
        // Optimal: pH 6.0-7.5
        if (phLevel >= 6.0 && phLevel <= 7.5) {
            return "NORMAL";
        } else if (phLevel < 5.5 || phLevel > 8.0) {
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

    public double getPhLevel() {
        return phLevel;
    }

    public void setPhLevel(double phLevel) {
        this.phLevel = phLevel;
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

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    @Override
    public String toString() {
        return "PhReading{" +
                "id=" + id +
                ", phLevel=" + phLevel +
                ", sensorId='" + sensorId + '\'' +
                ", status='" + status + '\'' +
                ", soilType='" + soilType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}