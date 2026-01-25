package com.sunflower.farm.temperature;

import jakarta.jws.WebService;
import java.util.Random;

/**
 * SOAP Web Service Implementation for Temperature Monitoring
 */
@WebService(endpointInterface = "com.sunflower.farm.temperature.ITemperatureService")
public class TemperatureServiceImpl implements ITemperatureService {

    private DatabaseManager db;
    private Random random;
    private TemperatureReading lastReading;

    public TemperatureServiceImpl() {
        this.db = new DatabaseManager();
        this.random = new Random();
        System.out.println("✅ Temperature Service initialized");
    }

    @Override
    public String getCurrentReading() {
        // Simulate realistic temperature readings for sunflowers
        // Air: 20-30°C optimal, Soil: 15-25°C optimal
        double airTemp = 18 + (random.nextDouble() * 15);    // 18-33°C
        double soilTemp = 13 + (random.nextDouble() * 15);   // 13-28°C

        TemperatureReading reading = new TemperatureReading(airTemp, soilTemp);
        this.lastReading = reading;

        // Save to database
        boolean saved = db.saveTemperatureReading(reading);

        // Create alert if critical
        if ("CRITICAL".equals(reading.getStatus()) && saved) {
            db.createAlert(
                    "TEMPERATURE_ALERT",
                    "HIGH",
                    String.format("Temperature critical! Air: %.1f°C, Soil: %.1f°C",
                            airTemp, soilTemp),
                    reading.getSensorId()
            );
        }

        return String.format(
                "{\"sensorId\":\"%s\", \"airTemperature\":%.2f, \"soilTemperature\":%.2f, " +
                        "\"status\":\"%s\", \"timestamp\":\"%s\"}",
                reading.getSensorId(),
                reading.getAirTemperature(),
                reading.getSoilTemperature(),
                reading.getStatus(),
                reading.getTimestamp()
        );
    }

    @Override
    public String getStatus() {
        if (lastReading == null) {
            getCurrentReading();
        }

        String message;
        switch(lastReading.getStatus()) {
            case "NORMAL":
                message = "Temperature optimal for sunflower growth";
                break;
            case "WARNING":
                message = "Temperature approaching critical levels";
                break;
            case "CRITICAL":
                message = "ALERT: Temperature outside safe range!";
                break;
            default:
                message = "Unknown status";
        }

        return String.format(
                "{\"status\":\"%s\", \"message\":\"%s\", \"airTemp\":%.2f, \"soilTemp\":%.2f}",
                lastReading.getStatus(),
                message,
                lastReading.getAirTemperature(),
                lastReading.getSoilTemperature()
        );
    }

    @Override
    public String getHistory(int count) {
        var readings = db.getRecentTemperatureReadings(count);
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < readings.size(); i++) {
            TemperatureReading r = readings.get(i);
            json.append(String.format(
                    "{\"id\":%d, \"airTemp\":%.2f, \"soilTemp\":%.2f, \"status\":\"%s\", \"timestamp\":\"%s\"}",
                    r.getId(), r.getAirTemperature(), r.getSoilTemperature(),
                    r.getStatus(), r.getTimestamp()
            ));
            if (i < readings.size() - 1) json.append(",");
        }

        json.append("]");
        return json.toString();
    }

    @Override
    public double getAirTemperature() {
        if (lastReading == null) {
            getCurrentReading();
        }
        return lastReading.getAirTemperature();
    }

    @Override
    public double getSoilTemperature() {
        if (lastReading == null) {
            getCurrentReading();
        }
        return lastReading.getSoilTemperature();
    }

    @Override
    public String simulateReadings(int count) {
        int saved = 0;
        for (int i = 0; i < count; i++) {
            double airTemp = 18 + (random.nextDouble() * 15);
            double soilTemp = 13 + (random.nextDouble() * 15);

            TemperatureReading reading = new TemperatureReading(airTemp, soilTemp);
            reading.setTimestamp(reading.getTimestamp().minusMinutes(count - i));

            if (db.saveTemperatureReading(reading)) {
                saved++;
            }
        }

        return String.format("{\"message\":\"Simulated %d temperature readings\"}", saved);
    }

    @Override
    public String ping() {
        return "Temperature Service is running! 🌡️";
    }
}