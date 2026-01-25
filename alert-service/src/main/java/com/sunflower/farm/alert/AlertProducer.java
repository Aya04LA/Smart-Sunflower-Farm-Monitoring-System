package com.sunflower.farm.alert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Scanner;

/**
 * Kafka Producer - Sends alerts to Kafka topic
 * This simulates the sensors sending alerts
 */
public class AlertProducer {

    private static final String TOPIC = "sunflower-alerts";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("🚨 Sunflower Farm - Alert Producer (Kafka)");
        System.out.println("════════════════════════════════════════════════════════════");

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("✅ Connected to Kafka at " + BOOTSTRAP_SERVERS);
            System.out.println("📍 Topic: " + TOPIC);
            System.out.println();
            System.out.println("Alert Types:");
            System.out.println("  1. HUMIDITY_CRITICAL  - Soil moisture too low/high");
            System.out.println("  2. TEMPERATURE_HIGH   - Temperature above safe range");
            System.out.println("  3. PH_CRITICAL        - pH outside optimal range");
            System.out.println("  4. WEATHER_SEVERE     - Dangerous weather conditions");
            System.out.println("  5. AUTO_GENERATE      - Generate random alerts");
            System.out.println("  6. EXIT               - Quit");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println();

            while (true) {
                System.out.print("Select option (1-6): ");
                String choice = scanner.nextLine().trim();

                if (choice.equals("6")) {
                    System.out.println("👋 Shutting down producer...");
                    break;
                }

                Alert alert = null;

                switch (choice) {
                    case "1":
                        alert = new Alert("HUMIDITY", "CRITICAL",
                                "Soil moisture critically low at 35%!", "HUMIDITY-001", 35.0);
                        alert.setRecommendation("Immediate irrigation required!");
                        break;
                    case "2":
                        alert = new Alert("TEMPERATURE", "HIGH",
                                "Air temperature exceeds 35°C!", "TEMP-001", 35.2);
                        alert.setRecommendation("Provide shade or increase ventilation");
                        break;
                    case "3":
                        alert = new Alert("PH", "CRITICAL",
                                "Soil pH at 5.2 - too acidic!", "PH-001", 5.2);
                        alert.setRecommendation("Add lime to raise pH to 6.0-7.5 range");
                        break;
                    case "4":
                        alert = new Alert("WEATHER", "HIGH",
                                "Severe wind speeds at 65 km/h!", "WEATHER-001", 65.0);
                        alert.setRecommendation("Secure plants and check for damage");
                        break;
                    case "5":
                        // Generate 5 random alerts
                        System.out.println("🔄 Generating 5 random alerts...");
                        for (int i = 0; i < 5; i++) {
                            alert = generateRandomAlert();
                            sendAlert(producer, alert);
                            Thread.sleep(500); // Small delay between alerts
                        }
                        System.out.println("✅ Sent 5 alerts!\n");
                        continue;
                    default:
                        System.out.println("❌ Invalid option\n");
                        continue;
                }

                if (alert != null) {
                    sendAlert(producer, alert);
                    System.out.println("✅ Alert sent!\n");
                }
            }

            System.out.println("✅ Producer shut down successfully");

        } catch (Exception e) {
            System.err.println("❌ Producer error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendAlert(KafkaProducer<String, String> producer, Alert alert) {
        String json = gson.toJson(alert);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, alert.getId(), json);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("❌ Failed to send alert: " + exception.getMessage());
            } else {
                System.out.println("📤 Alert sent to partition " + metadata.partition() +
                        " at offset " + metadata.offset());
            }
        });
    }

    private static Alert generateRandomAlert() {
        String[] types = {"HUMIDITY", "TEMPERATURE", "PH", "WEATHER"};
        String[] severities = {"MEDIUM", "HIGH", "CRITICAL"};

        String type = types[(int)(Math.random() * types.length)];
        String severity = severities[(int)(Math.random() * severities.length)];

        Alert alert = new Alert(type, severity,
                "Random alert for testing", type + "-001", Math.random() * 100);

        return alert;
    }
}