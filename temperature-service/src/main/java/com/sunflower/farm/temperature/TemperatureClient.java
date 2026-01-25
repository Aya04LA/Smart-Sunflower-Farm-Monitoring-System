package com.sunflower.farm.temperature;

import jakarta.xml.ws.Service;
import javax.xml.namespace.QName;
import java.net.URL;

/**
 * Simple SOAP client to test the Temperature Service
 * Run this AFTER starting the TemperatureServer
 */
public class TemperatureClient {

    public static void main(String[] args) {
        try {
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("🔌 Connecting to Temperature SOAP Service...");
            System.out.println("════════════════════════════════════════════════════════════");

            // Connect to the SOAP service
            URL wsdlURL = new URL("http://localhost:8085/temperature?wsdl");
            QName qname = new QName("http://temperature.farm.sunflower.com/", "TemperatureServiceImplService");

            Service service = Service.create(wsdlURL, qname);
            ITemperatureService temperatureService = service.getPort(ITemperatureService.class);

            System.out.println("✅ Connected to SOAP service!\n");

            // Test 1: Ping
            System.out.println("Test 1: Ping Service");
            System.out.println("Response: " + temperatureService.ping());
            System.out.println();

            // Test 2: Get current reading
            System.out.println("Test 2: Get Current Reading");
            String currentReading = temperatureService.getCurrentReading();
            System.out.println("Reading: " + currentReading);
            System.out.println();

            // Test 3: Get air temperature
            System.out.println("Test 3: Get Air Temperature");
            double airTemp = temperatureService.getAirTemperature();
            System.out.println("Air Temperature: " + airTemp + "°C");
            System.out.println();

            // Test 4: Get soil temperature
            System.out.println("Test 4: Get Soil Temperature");
            double soilTemp = temperatureService.getSoilTemperature();
            System.out.println("Soil Temperature: " + soilTemp + "°C");
            System.out.println();

            // Test 5: Get status
            System.out.println("Test 5: Get Status");
            String status = temperatureService.getStatus();
            System.out.println("Status: " + status);
            System.out.println();

            // Test 6: Simulate readings
            System.out.println("Test 6: Simulate 5 Readings");
            String simResult = temperatureService.simulateReadings(5);
            System.out.println("Result: " + simResult);
            System.out.println();

            // Test 7: Get history
            System.out.println("Test 7: Get Last 3 Readings");
            String history = temperatureService.getHistory(3);
            System.out.println("History: " + history);
            System.out.println();

            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("✅ All tests completed successfully!");
            System.out.println("════════════════════════════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("❌ Error connecting to SOAP service");
            System.err.println("Make sure TemperatureServer is running first!");
            e.printStackTrace();
        }
    }
}