package com.sunflower.farm.ph;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI Remote Interface for pH Monitoring Service
 *
 * IMPORTANT:
 * - Must extend Remote
 * - All methods must throw RemoteException
 * - All parameters and return types must be Serializable
 */
public interface IPhService extends Remote {

    /**
     * Get current pH reading
     */
    PhReading getCurrentReading() throws RemoteException;

    /**
     * Get pH level only (simple return)
     */
    double getPhLevel() throws RemoteException;

    /**
     * Get sensor status
     */
    String getStatus() throws RemoteException;

    /**
     * Get last N readings
     */
    List<PhReading> getHistory(int count) throws RemoteException;

    /**
     * Get all readings
     */
    List<PhReading> getAllReadings() throws RemoteException;

    /**
     * Simulate multiple readings for testing
     */
    int simulateReadings(int count) throws RemoteException;

    /**
     * Check soil acidity level
     * Returns: "Acidic", "Neutral", or "Alkaline"
     */
    String getSoilAcidity() throws RemoteException;

    /**
     * Get recommendation based on pH level
     */
    String getRecommendation() throws RemoteException;

    /**
     * Health check
     */
    String ping() throws RemoteException;
}