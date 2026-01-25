package com.sunflower.farm.weather;

import java.time.LocalDateTime;

/**
 * Model class for weather readings
 * Sunflowers need: 6-8 hours sunlight, moderate wind, minimal heavy rain
 */
public class WeatherReading {
    private int id;
    private double windSpeed;        // km/h
    private double rainfall;         // mm
    private double sunlightHours;    // hours
    private String sensorId;
    private LocalDateTime timestamp;
    private String status;           // "NORMAL", "WARNING", "CRITICAL"
    private String weatherCondition; // "Sunny", "Cloudy", "Rainy", "Windy"

    // Default constructor
    public WeatherReading() {
        this.timestamp = LocalDateTime.now();
        this.sensorId = "WEATHER-001";
    }

    // Constructor with values
    public WeatherReading(double windSpeed, double rainfall, double sunlightHours) {
        this();
        this.windSpeed = windSpeed;
        this.rainfall = rainfall;
        this.sunlightHours = sunlightHours;
        this.weatherCondition = determineWeatherCondition();
        this.status = determineStatus();
    }

    // Determine weather condition
    private String determineWeatherCondition() {
        if (rainfall > 10) {
            return "Rainy";
        } else if (windSpeed > 40) {
            return "Windy";
        } else if (sunlightHours < 4) {
            return "Cloudy";
        } else {
            return "Sunny";
        }
    }

    // Determine status based on optimal sunflower conditions
    private String determineStatus() {
        // Optimal: 6-8 hours sunlight, wind < 40 km/h, rainfall < 50mm
        boolean sunlightOk = sunlightHours >= 6 && sunlightHours <= 8;
        boolean windOk = windSpeed < 40;
        boolean rainfallOk = rainfall < 50;

        if (sunlightOk && windOk && rainfallOk) {
            return "NORMAL";
        } else if (sunlightHours < 4 || windSpeed > 60 || rainfall > 100) {
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

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
        this.weatherCondition = determineWeatherCondition();
        this.status = determineStatus();
    }

    public double getRainfall() {
        return rainfall;
    }

    public void setRainfall(double rainfall) {
        this.rainfall = rainfall;
        this.weatherCondition = determineWeatherCondition();
        this.status = determineStatus();
    }

    public double getSunlightHours() {
        return sunlightHours;
    }

    public void setSunlightHours(double sunlightHours) {
        this.sunlightHours = sunlightHours;
        this.weatherCondition = determineWeatherCondition();
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

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    @Override
    public String toString() {
        return "WeatherReading{" +
                "id=" + id +
                ", windSpeed=" + windSpeed +
                ", rainfall=" + rainfall +
                ", sunlightHours=" + sunlightHours +
                ", condition='" + weatherCondition + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}