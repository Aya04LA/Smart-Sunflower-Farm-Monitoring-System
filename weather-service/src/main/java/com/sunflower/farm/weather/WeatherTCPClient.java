package com.sunflower.farm.weather;

import java.io.*;
import java.net.*;

/**
 * TCP Socket Client to test the Weather Service
 * Run this AFTER starting WeatherTCPServer
 */
public class WeatherTCPClient {

    private static final String HOST = "localhost";
    private static final int PORT = 9090;

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("🔌 Connecting to Weather TCP Server...");
        System.out.println("════════════════════════════════════════════════════════════");

        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("✅ Connected to server at " + HOST + ":" + PORT + "\n");

            // Test 1: Ping
            System.out.println("Test 1: Ping Server");
            sendCommand(out, in, "PING");
            System.out.println();

            // Test 2: Get current reading
            System.out.println("Test 2: Get Current Reading");
            sendCommand(out, in, "CURRENT");
            System.out.println();

            // Test 3: Get status
            System.out.println("Test 3: Get Status");
            sendCommand(out, in, "STATUS");
            System.out.println();

            // Test 4: Simulate readings
            System.out.println("Test 4: Simulate 5 Readings");
            sendCommand(out, in, "SIMULATE:5");
            System.out.println();

            // Test 5: Get history
            System.out.println("Test 5: Get Last 3 Readings");
            sendCommand(out, in, "HISTORY:3");
            System.out.println();

            // Test 6: Get forecast
            System.out.println("Test 6: Get Weather Forecast");
            sendCommand(out, in, "FORECAST");
            System.out.println();

            // Test 7: Get all readings
            System.out.println("Test 7: Get All Readings");
            sendCommand(out, in, "ALL");
            System.out.println();

            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("✅ All TCP tests completed successfully!");
            System.out.println("════════════════════════════════════════════════════════════");

        } catch (IOException e) {
            System.err.println("❌ Connection error: " + e.getMessage());
            System.err.println("Make sure WeatherTCPServer is running first!");
            e.printStackTrace();
        }
    }

    /**
     * Send a command and receive response
     */
    private static void sendCommand(PrintWriter out, BufferedReader in, String command) {
        try {
            System.out.println("📤 Sending: " + command);
            out.println(command);

            String response = in.readLine();
            System.out.println("📨 Response: " + formatJson(response));

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Simple JSON formatting for readability
     */
    private static String formatJson(String json) {
        if (json == null) return "null";

        // Basic formatting - replace commas and braces with newlines for readability
        return json
                .replace("{", "{\n  ")
                .replace(",", ",\n  ")
                .replace("}", "\n}")
                .replace("[", "[\n    ")
                .replace("]", "\n  ]");
    }
}