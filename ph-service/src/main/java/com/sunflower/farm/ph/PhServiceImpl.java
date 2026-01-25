package com.sunflower.farm.ph;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Random;

/**
 * RMI Service Implementation for pH Monitoring
 *
 * IMPORTANT: Must extend UnicastRemoteObject for RMI
 */
public class PhServiceImpl extends UnicastRemoteObject implements IPhService {

    private DatabaseManager db;
    private Random random;
    private PhReading lastReading;
    private static final String[] SOIL_TYPES = {"Sandy", "Loamy", "Clay", "Silty"};

    /**
     * Constructor must throw RemoteException
     */
    public PhServiceImpl() throws RemoteException {
        super();
        this.db = new DatabaseManager();
        this.random = new Random();
        System.out.println("✅ pH Service initialized");
    }

    @Override
    public PhReading getCurrentReading() throws RemoteException {
        // Simulate realistic pH readings for sunflowers
        // Optimal: 6.0-7.5
        double phLevel = 5.5 + (random.nextDouble() * 2.5); // 5.5-8.0
        String soilType = SOIL_TYPES[random.nextInt(SOIL_TYPES.length)];

        PhReading reading = new PhReading(phLevel, soilType);
        this.lastReading = reading;

        // Save to database
        boolean saved = db.savePhReading(reading);

        // Create alert if critical
        if ("CRITICAL".equals(reading.getStatus()) && saved) {
            db.createAlert(
                    "PH_ALERT",
                    "HIGH",
                    String.format("pH level critical! Current: %.2f (Optimal: 6.0-7.5)", phLevel),
                    reading.getSensorId()
            );
            System.out.println("⚠️ CRITICAL pH ALERT: " + phLevel);
        }

        System.out.println("📊 pH Reading: " + phLevel + " - " + reading.getStatus());
        return reading;
    }

    @Override
    public double getPhLevel() throws RemoteException {
        if (lastReading == null) {
            getCurrentReading();
        }
        return lastReading.getPhLevel();
    }

    @Override
    public String getStatus() throws RemoteException {
        if (lastReading == null) {
            getCurrentReading();
        }

        String message;
        switch(lastReading.getStatus()) {
            case "NORMAL":
                message = "pH level optimal for sunflower growth (6.0-7.5)";
                break;
            case "WARNING":
                message = "pH level approaching critical range";
                break;
            case "CRITICAL":
                message = "ALERT: pH level outside safe range! Immediate action needed";
                break;
            default:
                message = "Unknown status";
        }

        return String.format("Status: %s - %s (Current pH: %.2f)",
                lastReading.getStatus(), message, lastReading.getPhLevel());
    }

    @Override
    public List<PhReading> getHistory(int count) throws RemoteException {
        return db.getRecentPhReadings(count);
    }

    @Override
    public List<PhReading> getAllReadings() throws RemoteException {
        return db.getAllPhReadings();
    }

    @Override
    public int simulateReadings(int count) throws RemoteException {
        int saved = 0;
        System.out.println("🔄 Simulating " + count + " pH readings...");

        for (int i = 0; i < count; i++) {
            double phLevel = 5.5 + (random.nextDouble() * 2.5);
            String soilType = SOIL_TYPES[random.nextInt(SOIL_TYPES.length)];

            PhReading reading = new PhReading(phLevel, soilType);
            reading.setTimestamp(reading.getTimestamp().minusMinutes(count - i));

            if (db.savePhReading(reading)) {
                saved++;
            }
        }

        System.out.println("✅ Simulated " + saved + " readings");
        return saved;
    }

    @Override
    public String getSoilAcidity() throws RemoteException {
        if (lastReading == null) {
            getCurrentReading();
        }

        double ph = lastReading.getPhLevel();
        if (ph < 6.5) {
            return "Acidic (pH " + String.format("%.2f", ph) + ")";
        } else if (ph > 7.5) {
            return "Alkaline (pH " + String.format("%.2f", ph) + ")";
        } else {
            return "Neutral (pH " + String.format("%.2f", ph) + ")";
        }
    }

    @Override
    public String getRecommendation() throws RemoteException {
        if (lastReading == null) {
            getCurrentReading();
        }

        double ph = lastReading.getPhLevel();

        if (ph < 5.5) {
            return "⚠️ Soil too acidic! Add lime (calcium carbonate) to raise pH. Target: 6.0-7.5";
        } else if (ph < 6.0) {
            return "⚡ Slightly acidic. Consider adding lime gradually. Monitor closely.";
        } else if (ph >= 6.0 && ph <= 7.5) {
            return "✅ Perfect pH for sunflowers! Maintain current soil management.";
        } else if (ph <= 8.0) {
            return "⚡ Slightly alkaline. Add sulfur or organic matter to lower pH.";
        } else {
            return "⚠️ Soil too alkaline! Add sulfur or acidic fertilizers. Target: 6.0-7.5";
        }
    }

    @Override
    public String ping() throws RemoteException {
        return "pH Monitoring Service is running! 🧪 Current pH: " +
                (lastReading != null ? String.format("%.2f", lastReading.getPhLevel()) : "N/A");
    }
}