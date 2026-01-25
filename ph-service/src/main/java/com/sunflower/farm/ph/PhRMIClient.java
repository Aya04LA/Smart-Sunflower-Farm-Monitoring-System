package com.sunflower.farm.ph;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * RMI Client to test the pH Service
 * Run this AFTER starting PhRMIServer
 */
public class PhRMIClient {

    public static void main(String[] args) {
        try {
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("🔌 Connecting to pH RMI Service...");
            System.out.println("════════════════════════════════════════════════════════════");

            // Connect to the RMI registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IPhService phService = (IPhService) registry.lookup("PhService");

            System.out.println("✅ Connected to RMI service!\n");

            // Test 1: Ping
            System.out.println("Test 1: Ping Service");
            System.out.println("Response: " + phService.ping());
            System.out.println();

            // Test 2: Get current reading
            System.out.println("Test 2: Get Current Reading");
            PhReading reading = phService.getCurrentReading();
            System.out.println("Reading: " + reading);
            System.out.println();

            // Test 3: Get pH level
            System.out.println("Test 3: Get pH Level");
            double phLevel = phService.getPhLevel();
            System.out.println("pH Level: " + phLevel);
            System.out.println();

            // Test 4: Get status
            System.out.println("Test 4: Get Status");
            String status = phService.getStatus();
            System.out.println("Status: " + status);
            System.out.println();

            // Test 5: Get soil acidity
            System.out.println("Test 5: Get Soil Acidity");
            String acidity = phService.getSoilAcidity();
            System.out.println("Acidity: " + acidity);
            System.out.println();

            // Test 6: Get recommendation
            System.out.println("Test 6: Get Recommendation");
            String recommendation = phService.getRecommendation();
            System.out.println("Recommendation: " + recommendation);
            System.out.println();

            // Test 7: Simulate readings
            System.out.println("Test 7: Simulate 5 Readings");
            int simulated = phService.simulateReadings(5);
            System.out.println("Simulated " + simulated + " readings");
            System.out.println();

            // Test 8: Get history
            System.out.println("Test 8: Get Last 3 Readings");
            List<PhReading> history = phService.getHistory(3);
            System.out.println("Retrieved " + history.size() + " readings:");
            for (PhReading r : history) {
                System.out.println("  - pH " + String.format("%.2f", r.getPhLevel()) +
                        " [" + r.getStatus() + "] at " + r.getTimestamp());
            }
            System.out.println();

            // Test 9: Get all readings
            System.out.println("Test 9: Get All Readings");
            List<PhReading> allReadings = phService.getAllReadings();
            System.out.println("Total readings in database: " + allReadings.size());
            System.out.println();

            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("✅ All RMI tests completed successfully!");
            System.out.println("════════════════════════════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("❌ Error connecting to RMI service");
            System.err.println("Make sure PhRMIServer is running first!");
            e.printStackTrace();
        }
    }
}