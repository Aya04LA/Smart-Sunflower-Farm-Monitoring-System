package com.sunflower.farm.ph;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * RMI Server for pH Monitoring Service
 * This registers the service in the RMI registry on port 1099
 */
public class PhRMIServer {

    public static final int RMI_PORT = 1099;
    public static final String SERVICE_NAME = "PhService";

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("🧪 Sunflower Farm - pH Monitoring Service (Java RMI)");
        System.out.println("════════════════════════════════════════════════════════════");

        try {
            // Create the RMI registry
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            System.out.println("✅ RMI Registry created on port " + RMI_PORT);

            // Create and bind the service
            IPhService phService = new PhServiceImpl();
            registry.rebind(SERVICE_NAME, phService);

            System.out.println("✅ pH Service registered successfully!");
            System.out.println("📍 Service Name: " + SERVICE_NAME);
            System.out.println("📍 RMI Port: " + RMI_PORT);
            System.out.println();
            System.out.println("Available RMI Methods:");
            System.out.println("  • getCurrentReading()      - Get current pH reading");
            System.out.println("  • getPhLevel()             - Get pH level only");
            System.out.println("  • getStatus()              - Get sensor status");
            System.out.println("  • getHistory(count)        - Get last N readings");
            System.out.println("  • getAllReadings()         - Get all readings");
            System.out.println("  • simulateReadings(count)  - Generate test data");
            System.out.println("  • getSoilAcidity()         - Check acidity level");
            System.out.println("  • getRecommendation()      - Get pH recommendations");
            System.out.println("  • ping()                   - Health check");
            System.out.println();
            System.out.println("📊 Optimal Sunflower pH:");
            System.out.println("  • pH Range: 6.0-7.5 (slightly acidic to neutral)");
            System.out.println();
            System.out.println("💡 Use PhRMIClient to test the service");
            System.out.println("Press CTRL+C to stop the server...");
            System.out.println("════════════════════════════════════════════════════════════");

            // Keep server running
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("❌ Failed to start RMI service");
            e.printStackTrace();
        }
    }
}