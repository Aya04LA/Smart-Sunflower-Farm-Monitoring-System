package com.sunflower.farm.temperature;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;

/**
 * SOAP Web Service Interface for Temperature Monitoring
 * This defines the contract for the SOAP service
 */
@WebService
@SOAPBinding(style = Style.RPC)
public interface ITemperatureService {

    /**
     * Get current temperature reading
     */
    @WebMethod
    String getCurrentReading();

    /**
     * Get temperature status
     */
    @WebMethod
    String getStatus();

    /**
     * Get last N readings
     */
    @WebMethod
    String getHistory(int count);

    /**
     * Get air temperature only
     */
    @WebMethod
    double getAirTemperature();

    /**
     * Get soil temperature only
     */
    @WebMethod
    double getSoilTemperature();

    /**
     * Simulate multiple readings for testing
     */
    @WebMethod
    String simulateReadings(int count);

    /**
     * Health check
     */
    @WebMethod
    String ping();
}