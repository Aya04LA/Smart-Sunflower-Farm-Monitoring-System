package com.sunflower.farm.weather;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * TCP Socket Server for Weather Monitoring
 * This is raw socket programming - the lowest level!
 */
public class WeatherTCPServer {

    private static final int PORT = 9090;
    private static DatabaseManager db;
    private static Random random = new Random();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();  // Removed pretty printing for TCP streaming

    public static void main(String[] args) {
        db = new DatabaseManager();

        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("🌤️  Sunflower Farm - Weather Station (TCP Socket)");
        System.out.println("════════════════════════════════════════════════════════════");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ TCP Server started successfully!");
            System.out.println("📍 Listening on port: " + PORT);
            System.out.println();
            System.out.println("Available Commands (send via TCP client):");
            System.out.println("  • CURRENT     - Get current weather reading");
            System.out.println("  • STATUS      - Get weather status");
            System.out.println("  • HISTORY:N   - Get last N readings (e.g., HISTORY:5)");
            System.out.println("  • ALL         - Get all readings");
            System.out.println("  • SIMULATE:N  - Simulate N readings (e.g., SIMULATE:10)");
            System.out.println("  • FORECAST    - Get weather forecast recommendation");
            System.out.println("  • PING        - Health check");
            System.out.println();
            System.out.println("📊 Optimal Sunflower Weather:");
            System.out.println("  • Sunlight: 6-8 hours/day");
            System.out.println("  • Wind: < 40 km/h");
            System.out.println("  • Rainfall: < 50 mm");
            System.out.println();
            System.out.println("Waiting for client connections...");
            System.out.println("════════════════════════════════════════════════════════════");

            // Accept multiple clients
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n🔌 New client connected: " + clientSocket.getInetAddress());

                // Handle each client in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle individual client connection
     */
    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String command;
            while ((command = in.readLine()) != null) {
                System.out.println("📨 Received command: " + command);
                String response = processCommand(command);
                out.println(response);
                System.out.println("📤 Sent response");
            }
        } catch (IOException e) {
            System.err.println("❌ Client handler error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("🔌 Client disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Process client commands
     */
    private static String processCommand(String command) {
        command = command.trim().toUpperCase();

        try {
            if (command.equals("CURRENT")) {
                return handleCurrentReading();
            } else if (command.equals("STATUS")) {
                return handleStatus();
            } else if (command.startsWith("HISTORY:")) {
                int count = Integer.parseInt(command.split(":")[1]);
                return handleHistory(count);
            } else if (command.equals("ALL")) {
                return handleAllReadings();
            } else if (command.startsWith("SIMULATE:")) {
                int count = Integer.parseInt(command.split(":")[1]);
                return handleSimulate(count);
            } else if (command.equals("FORECAST")) {
                return handleForecast();
            } else if (command.equals("PING")) {
                return gson.toJson(new Response("SUCCESS", "Weather Station is running! 🌤️"));
            } else {
                return gson.toJson(new Response("ERROR", "Unknown command: " + command));
            }
        } catch (Exception e) {
            return gson.toJson(new Response("ERROR", "Error processing command: " + e.getMessage()));
        }
    }

    private static String handleCurrentReading() {
        // Simulate weather data
        double windSpeed = 10 + (random.nextDouble() * 40);      // 10-50 km/h
        double rainfall = random.nextDouble() * 80;              // 0-80 mm
        double sunlightHours = 4 + (random.nextDouble() * 6);   // 4-10 hours

        WeatherReading reading = new WeatherReading(windSpeed, rainfall, sunlightHours);
        db.saveWeatherReading(reading);

        // Create alert if critical
        if ("CRITICAL".equals(reading.getStatus())) {
            db.createAlert("WEATHER_ALERT", "HIGH",
                    String.format("Critical weather! Wind: %.1f km/h, Rain: %.1f mm, Sun: %.1f hrs",
                            windSpeed, rainfall, sunlightHours),
                    reading.getSensorId()
            );
        }

        return gson.toJson(new Response("SUCCESS", "Current reading retrieved", reading));
    }

    private static String handleStatus() {
        List<WeatherReading> recent = db.getRecentWeatherReadings(1);
        if (recent.isEmpty()) {
            return handleCurrentReading();
        }

        WeatherReading latest = recent.get(0);
        String message = switch(latest.getStatus()) {
            case "NORMAL" -> "Weather conditions optimal for sunflowers";
            case "WARNING" -> "Weather conditions need monitoring";
            case "CRITICAL" -> "ALERT: Severe weather conditions!";
            default -> "Unknown status";
        };

        StatusResponse status = new StatusResponse(
                latest.getStatus(),
                message,
                latest.getWeatherCondition(),
                latest.getWindSpeed(),
                latest.getRainfall(),
                latest.getSunlightHours()
        );

        return gson.toJson(new Response("SUCCESS", message, status));
    }

    private static String handleHistory(int count) {
        List<WeatherReading> readings = db.getRecentWeatherReadings(count);
        return gson.toJson(new Response("SUCCESS", "Retrieved " + readings.size() + " readings", readings));
    }

    private static String handleAllReadings() {
        List<WeatherReading> readings = db.getAllWeatherReadings();
        return gson.toJson(new Response("SUCCESS", "Retrieved all readings", readings));
    }

    private static String handleSimulate(int count) {
        int saved = 0;
        for (int i = 0; i < count; i++) {
            double windSpeed = 10 + (random.nextDouble() * 40);
            double rainfall = random.nextDouble() * 80;
            double sunlightHours = 4 + (random.nextDouble() * 6);

            WeatherReading reading = new WeatherReading(windSpeed, rainfall, sunlightHours);
            reading.setTimestamp(reading.getTimestamp().minusHours(count - i));

            if (db.saveWeatherReading(reading)) {
                saved++;
            }
        }
        return gson.toJson(new Response("SUCCESS", "Simulated " + saved + " readings"));
    }

    private static String handleForecast() {
        List<WeatherReading> recent = db.getRecentWeatherReadings(3);
        if (recent.isEmpty()) {
            return gson.toJson(new Response("SUCCESS", "No data for forecast. Generating current reading..."));
        }

        double avgSunlight = recent.stream().mapToDouble(WeatherReading::getSunlightHours).average().orElse(0);
        double avgWind = recent.stream().mapToDouble(WeatherReading::getWindSpeed).average().orElse(0);
        double avgRain = recent.stream().mapToDouble(WeatherReading::getRainfall).average().orElse(0);

        String forecast = String.format(
                "Based on recent data:\n" +
                        "- Average Sunlight: %.1f hours (Target: 6-8 hours)\n" +
                        "- Average Wind: %.1f km/h (Target: < 40 km/h)\n" +
                        "- Average Rainfall: %.1f mm (Target: < 50 mm)\n\n",
                avgSunlight, avgWind, avgRain
        );

        if (avgSunlight < 6) {
            forecast += "⚠️ Low sunlight exposure. Consider field positioning.\n";
        }
        if (avgWind > 40) {
            forecast += "⚠️ High wind speeds. Install windbreaks to protect plants.\n";
        }
        if (avgRain > 50) {
            forecast += "⚠️ Excessive rainfall. Ensure proper drainage.\n";
        }
        if (avgSunlight >= 6 && avgSunlight <= 8 && avgWind < 40 && avgRain < 50) {
            forecast += "✅ Weather conditions are ideal for sunflowers!";
        }

        return gson.toJson(new Response("SUCCESS", forecast));
    }

    // Response classes
    static class Response {
        String status;
        String message;
        Object data;

        Response(String status, String message) {
            this.status = status;
            this.message = message;
        }

        Response(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
    }

    static class StatusResponse {
        String status;
        String message;
        String condition;
        double windSpeed;
        double rainfall;
        double sunlightHours;

        StatusResponse(String status, String message, String condition,
                       double windSpeed, double rainfall, double sunlightHours) {
            this.status = status;
            this.message = message;
            this.condition = condition;
            this.windSpeed = windSpeed;
            this.rainfall = rainfall;
            this.sunlightHours = sunlightHours;
        }
    }
}