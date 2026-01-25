package com.sunflower.farm.weather;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Interactive TCP Client - Send commands manually to the server
 * Fun way to test the TCP socket server!
 */
public class WeatherInteractiveClient {

    private static final String HOST = "localhost";
    private static final int PORT = 9090;

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("🌤️  Weather Station - Interactive TCP Client");
        System.out.println("════════════════════════════════════════════════════════════");

        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("✅ Connected to server at " + HOST + ":" + PORT);
            System.out.println();
            System.out.println("Available Commands:");
            System.out.println("  • CURRENT      - Get current weather reading");
            System.out.println("  • STATUS       - Get weather status");
            System.out.println("  • HISTORY:N    - Get last N readings (e.g., HISTORY:5)");
            System.out.println("  • ALL          - Get all readings");
            System.out.println("  • SIMULATE:N   - Simulate N readings (e.g., SIMULATE:10)");
            System.out.println("  • FORECAST     - Get weather forecast");
            System.out.println("  • PING         - Health check");
            System.out.println("  • EXIT         - Quit client");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println();

            String command;
            while (true) {
                System.out.print("Enter command: ");
                command = scanner.nextLine().trim();

                if (command.equalsIgnoreCase("EXIT")) {
                    System.out.println("👋 Disconnecting...");
                    break;
                }

                if (command.isEmpty()) {
                    continue;
                }

                // Send command
                out.println(command);

                // Receive response
                String response = in.readLine();
                System.out.println("\n📨 Response:");
                System.out.println(formatJson(response));
                System.out.println();
            }

            System.out.println("✅ Connection closed");

        } catch (IOException e) {
            System.err.println("❌ Connection error: " + e.getMessage());
            System.err.println("Make sure WeatherTCPServer is running first!");
            e.printStackTrace();
        }
    }

    private static String formatJson(String json) {
        if (json == null) return "null";

        return json
                .replace("{", "{\n  ")
                .replace(",", ",\n  ")
                .replace("}", "\n}")
                .replace("[", "[\n    ")
                .replace("]", "\n  ]");
    }
}