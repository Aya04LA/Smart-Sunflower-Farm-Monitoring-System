package com.sunflower.farm;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Random;

/**
 * REST API Resource for Humidity Monitoring
 * Now with database persistence!
 */
@Path("/humidity")
public class HumidityResource {

    private static DatabaseManager db = new DatabaseManager();
    private static Random random = new Random();

    /**
     * GET /humidity/current
     * Returns the current humidity reading (simulated) and saves to DB
     */
    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentReading() {
        // Simulate sensor reading with realistic values for sunflowers
        double soilMoisture = 60 + (random.nextDouble() * 25); // 60-85%
        double airHumidity = 40 + (random.nextDouble() * 35);  // 40-75%

        HumidityReading reading = new HumidityReading(soilMoisture, airHumidity);

        // Save to database
        db.saveHumidityReading(reading);

        // Create alert if status is critical
        if ("CRITICAL".equals(reading.getStatus())) {
            db.createAlert(
                    "HUMIDITY_ALERT",
                    "HIGH",
                    "Humidity levels are critical! Soil: " + reading.getSoilMoisture() + "%, Air: " + reading.getAirHumidity() + "%",
                    reading.getSensorId()
            );
        }

        return Response.ok(reading).build();
    }

    /**
     * GET /humidity/all
     * Returns all recorded readings from database
     */
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReadings() {
        List<HumidityReading> readings = db.getAllHumidityReadings();
        return Response.ok(readings).build();
    }

    /**
     * GET /humidity/status
     * Returns current sensor status from latest DB reading
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        HumidityReading latest = db.getLatestHumidityReading();

        if (latest == null) {
            // No readings yet, create one
            return getCurrentReading();
        }

        String message = switch(latest.getStatus()) {
            case "NORMAL" -> "All humidity levels are optimal for sunflowers";
            case "WARNING" -> "Humidity levels approaching critical thresholds";
            case "CRITICAL" -> "ALERT: Humidity levels outside safe range!";
            default -> "Unknown status";
        };

        return Response.ok(new StatusResponse(
                latest.getStatus(),
                message,
                latest.getSoilMoisture(),
                latest.getAirHumidity()
        )).build();
    }

    /**
     * POST /humidity/reading
     * Manually add a reading and save to DB
     */
    @POST
    @Path("/reading")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(HumidityReading reading) {
        boolean saved = db.saveHumidityReading(reading);

        if (saved) {
            return Response.status(Response.Status.CREATED)
                    .entity(reading)
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MessageResponse("Failed to save reading"))
                    .build();
        }
    }

    /**
     * GET /humidity/history/{count}
     * Get last N readings from database
     */
    @GET
    @Path("/history/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory(@PathParam("count") int count) {
        List<HumidityReading> recent = db.getRecentHumidityReadings(count);
        return Response.ok(recent).build();
    }

    /**
     * DELETE /humidity/clear
     * Clear all readings from database
     */
    @DELETE
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearReadings() {
        int count = db.clearAllReadings();
        return Response.ok(new MessageResponse("Cleared " + count + " readings from database")).build();
    }

    /**
     * GET /humidity/simulate/{minutes}
     * Simulate sensor readings for the last N minutes
     */
    @GET
    @Path("/simulate/{minutes}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response simulateReadings(@PathParam("minutes") int minutes) {
        int count = 0;
        for (int i = minutes; i >= 0; i--) {
            double soilMoisture = 60 + (random.nextDouble() * 25);
            double airHumidity = 40 + (random.nextDouble() * 35);

            HumidityReading reading = new HumidityReading(soilMoisture, airHumidity);
            // Adjust timestamp to simulate historical data
            reading.setTimestamp(reading.getTimestamp().minusMinutes(i));

            if (db.saveHumidityReading(reading)) {
                count++;
            }
        }

        return Response.ok(new MessageResponse("Simulated " + count + " readings")).build();
    }

    // Helper classes for JSON responses
    public static class StatusResponse {
        public String status;
        public String message;
        public double soilMoisture;
        public double airHumidity;

        public StatusResponse(String status, String message, double soilMoisture, double airHumidity) {
            this.status = status;
            this.message = message;
            this.soilMoisture = soilMoisture;
            this.airHumidity = airHumidity;
        }
    }

    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }
}