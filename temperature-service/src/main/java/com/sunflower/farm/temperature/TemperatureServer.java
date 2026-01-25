package com.sunflower.farm.temperature;

import jakarta.xml.ws.Endpoint;

/**
 * SOAP Web Service Server for Temperature Monitoring
 * This publishes the SOAP service on port 8080
 */
public class TemperatureServer {

    private static final String URL = "http://localhost:8085/temperature";

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("🌡️  Sunflower Farm - Temperature Monitoring Service (SOAP)");
        System.out.println("════════════════════════════════════════════════════════════");

        try {
            // Publish the SOAP service
            Endpoint endpoint = Endpoint.publish(URL, new TemperatureServiceImpl());

            System.out.println("✅ SOAP Service started successfully!");
            System.out.println("📍 WSDL URL: " + URL + "?wsdl");
            System.out.println("📍 Endpoint: " + URL);
            System.out.println();
            System.out.println("Available SOAP Operations:");
            System.out.println("  • getCurrentReading()    - Get current temperature");
            System.out.println("  • getStatus()            - Get sensor status");
            System.out.println("  • getAirTemperature()    - Get air temperature only");
            System.out.println("  • getSoilTemperature()   - Get soil temperature only");
            System.out.println("  • getHistory(count)      - Get last N readings");
            System.out.println("  • simulateReadings(count) - Generate test data");
            System.out.println("  • ping()                 - Health check");
            System.out.println();
            System.out.println("📊 Optimal Sunflower Temperature:");
            System.out.println("  • Air Temperature: 20-30°C");
            System.out.println("  • Soil Temperature: 15-25°C");
            System.out.println();
            System.out.println("💡 Test with SoapUI or create a SOAP client");
            System.out.println("Press CTRL+C to stop the server...");
            System.out.println("════════════════════════════════════════════════════════════");

            // Keep server running
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("❌ Failed to start SOAP service");
            e.printStackTrace();
        }
    }
}