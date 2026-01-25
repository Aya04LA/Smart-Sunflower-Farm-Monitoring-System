package com.sunflower.farm;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.io.IOException;
import java.net.URI;

/**
 * Main server class for the Humidity Monitoring Service
 * This starts the REST API server on port 8081
 */
public class HumidityServer {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8081/api/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources
     */
    public static HttpServer startServer() {
        // Create a resource config that scans for JAX-RS resources and providers
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.sunflower.farm")
                .register(JacksonFeature.class);  // Enable JSON support

        // Create and start a new instance of grizzly http server
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method to start the server
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("🌻 Sunflower Farm - Humidity Monitoring Service");
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("✅ Server started successfully!");
        System.out.println("📍 Base URL: " + BASE_URI);
        System.out.println();
        System.out.println("Available Endpoints:");
        System.out.println("  GET  " + BASE_URI + "humidity/current      - Get current reading");
        System.out.println("  GET  " + BASE_URI + "humidity/all          - Get all readings");
        System.out.println("  GET  " + BASE_URI + "humidity/status       - Get sensor status");
        System.out.println("  GET  " + BASE_URI + "humidity/history/{n}  - Get last N readings");
        System.out.println("  POST " + BASE_URI + "humidity/reading      - Add manual reading");
        System.out.println("  DELETE " + BASE_URI + "humidity/clear      - Clear all readings");
        System.out.println();
        System.out.println("📊 Optimal Sunflower Conditions:");
        System.out.println("  • Soil Moisture: 60-80%");
        System.out.println("  • Air Humidity: 40-70%");
        System.out.println();
        System.out.println("Press CTRL+C to stop the server...");
        System.out.println("════════════════════════════════════════════════════════════");

        // Keep server running
        System.in.read();
        server.shutdown();
    }
}