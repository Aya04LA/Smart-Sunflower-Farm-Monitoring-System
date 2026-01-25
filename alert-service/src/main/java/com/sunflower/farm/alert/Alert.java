package com.sunflower.farm.alert;

import java.time.LocalDateTime;

/**
 * Alert model for Kafka messages
 */
public class Alert {
    private String id;
    private String alertType;     // "HUMIDITY", "TEMPERATURE", "PH", "WEATHER"
    private String severity;      // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    private String message;
    private String sensorId;
    private double value;         // The value that triggered the alert
    private String recommendation;
    private LocalDateTime timestamp;
    private boolean resolved;

    public Alert() {
        this.timestamp = LocalDateTime.now();
        this.resolved = false;
    }

    public Alert(String alertType, String severity, String message, String sensorId, double value) {
        this();
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
        this.sensorId = sensorId;
        this.value = value;
        this.id = generateId();
    }

    private String generateId() {
        return alertType + "-" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id='" + id + '\'' +
                ", type='" + alertType + '\'' +
                ", severity='" + severity + '\'' +
                ", message='" + message + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}