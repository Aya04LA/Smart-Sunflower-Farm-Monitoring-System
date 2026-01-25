package com.sunflower.farm.alert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Properties;

/**
 * Kafka Consumer - Receives and processes alerts from Kafka topic
 * This is like the dashboard that monitors all alerts
 */
public class AlertConsumer {


    private static final String TOPIC = "sunflower-alerts";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "alert-monitoring-group";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("📡 Sunflower Farm - Alert Consumer (Kafka)");
        System.out.println("════════════════════════════════════════════════════════════");

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));

            System.out.println("✅ Connected to Kafka at " + BOOTSTRAP_SERVERS);
            System.out.println("📍 Subscribed to topic: " + TOPIC);
            System.out.println("👥 Consumer group: " + GROUP_ID);
            System.out.println();
            System.out.println("🎧 Listening for alerts... (Press Ctrl+C to stop)");
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println();

            int alertCount = 0;

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    alertCount++;
                    processAlert(record, alertCount);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Consumer error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processAlert(ConsumerRecord<String, String> record, int count) {
        try {
            Alert alert = gson.fromJson(record.value(), Alert.class);

            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║ 🚨 ALERT #" + count + " RECEIVED");
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            System.out.println("║ ID:          " + alert.getId());
            System.out.println("║ Type:        " + alert.getAlertType());
            System.out.println("║ Severity:    " + getSeverityEmoji(alert.getSeverity()) + " " + alert.getSeverity());
            System.out.println("║ Sensor:      " + alert.getSensorId());
            System.out.println("║ Value:       " + String.format("%.2f", alert.getValue()));
            System.out.println("║ Message:     " + alert.getMessage());

            if (alert.getRecommendation() != null) {
                System.out.println("║ Action:      " + alert.getRecommendation());
            }

            System.out.println("║ Timestamp:   " + alert.getTimestamp());
            System.out.println("║ Kafka Info:  Partition=" + record.partition() + ", Offset=" + record.offset());
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();

            // Here you could:
            // - Save to database
            // - Send email/SMS notification
            // - Trigger automated responses
            // - Update dashboard

        } catch (Exception e) {
            System.err.println("❌ Error processing alert: " + e.getMessage());
        }
    }

    private static String getSeverityEmoji(String severity) {
        return switch (severity.toUpperCase()) {
            case "LOW" -> "ℹ️";
            case "MEDIUM" -> "⚠️";
            case "HIGH" -> "🔴";
            case "CRITICAL" -> "🚨";
            default -> "❓";
        };
    }
}