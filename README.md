# 🌻 Smart Sunflower Farm Monitoring System

A distributed IoT system for real-time agricultural monitoring using multiple communication technologies.

## 📋 Overview

This project implements a complete distributed system for monitoring sunflower farm conditions. It demonstrates the use of 5 different distributed computing technologies working together to collect, process, and visualize sensor data in real-time.

## 🎯 Features

- **Real-time Sensor Monitoring**: Humidity, Temperature, pH, and Weather data collection
- **Multi-Technology Architecture**: REST, SOAP, RMI, TCP Sockets, and Kafka
- **Alert System**: Automated alerts when conditions fall outside optimal ranges
- **Web Dashboard**: Beautiful, responsive UI with auto-refresh and live data visualization
- **Data Persistence**: MySQL database for historical data storage and analysis

## 🏗️ Architecture

The system consists of 5 independent distributed services:

| Service | Technology | Port | Purpose |
|---------|-----------|------|---------|
| Humidity Service | JAX-RS (REST) | 8081 | Soil & air humidity monitoring |
| Temperature Service | JAX-WS (SOAP) | 8085 | Air & soil temperature monitoring |
| pH Service | Java RMI | 1099 | Soil pH level monitoring |
| Weather Service | TCP Sockets | 9090 | Wind, rain, and sunlight monitoring |
| Alert System | Apache Kafka | 9092 | Real-time alert messaging |
| Dashboard | Web App | 8090 | Data visualization & monitoring |

## 🚀 Technologies Used

- **Backend**: Java 17, Maven
- **Web Services**: JAX-RS (Jersey), JAX-WS
- **Messaging**: Apache Kafka
- **Database**: MySQL
- **Frontend**: HTML, CSS, JavaScript
- **Server**: Jetty

## 📦 Prerequisites

- JDK 17 or higher
- Apache Maven
- MySQL Server
- Apache Kafka
- IntelliJ IDEA (recommended) or any Java IDE

## ⚙️ Installation & Setup

### 1. Database Setup
```sql
CREATE DATABASE sunflower_farm;
-- Run the provided database-setup.sql script
```

### 2. Start Kafka
```bash
# Start Zookeeper
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Start Kafka (in new terminal)
.\bin\windows\kafka-server-start.bat .\config\server.properties

# Create topic
.\bin\windows\kafka-topics.bat --create --topic sunflower-alerts --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### 3. Configure Database Password
Update the password in each service's `DatabaseManager.java`:
```java
private static final String PASSWORD = "your_password_here";
```

### 4. Run Services

Each service can be run independently:

**Humidity Service (REST):**
```bash
cd humidity-service
mvn clean install
# Run HumidityServer.java
```

**Temperature Service (SOAP):**
```bash
cd temperature-service
# Run TemperatureServer.java
```

**pH Service (RMI):**
```bash
cd ph-service
# Run PhRMIServer.java
```

**Weather Service (TCP):**
```bash
cd weather-service
# Run WeatherTCPServer.java
```

**Alert System (Kafka):**
```bash
cd alert-service
# Run AlertConsumer.java (listener)
# Run AlertProducer.java (sender)
```

**Dashboard:**
```bash
cd dashboard
mvn jetty:run
```

### 5. Access Dashboard
Open browser: `http://localhost:8090`

## 🧪 Testing Services

Each service includes test clients:

- **REST**: Test in browser or Postman at `http://localhost:8081/api/humidity/current`
- **SOAP**: Run `TemperatureClient.java`
- **RMI**: Run `PhRMIClient.java`
- **TCP**: Run `WeatherInteractiveClient.java`
- **Kafka**: Use `AlertProducer.java` to send test alerts

## 📊 Optimal Sunflower Conditions

The system monitors these parameters:

- **Temperature**: 20-30°C (air), 15-25°C (soil)
- **Humidity**: 60-80% (soil), 40-70% (air)
- **pH Level**: 6.0-7.5
- **Sunlight**: 6-8 hours/day
- **Wind**: < 40 km/h

## 🎓 Academic Context

This project was developed as part of the Distributed Systems course (2025-2026) to demonstrate understanding of:

- Distributed system architecture
- Multiple communication protocols
- Service-oriented architecture (SOA)
- Message queue systems
- Real-time data processing
- Full-stack development

## 📝 Project Structure

```
sunflower-farm/
├── humidity-service/      # JAX-RS REST API
├── temperature-service/   # JAX-WS SOAP Service
├── ph-service/           # Java RMI Service
├── weather-service/      # TCP Socket Server
├── alert-service/        # Kafka Producer/Consumer
└── dashboard/            # Web Dashboard
```

## 🤝 Contributing

This is an academic project, but suggestions and improvements are welcome!

## 📄 License

This project is for educational purposes.

## 👤 Author

[Laajoul Aya -- Fadil Aboubakar Sidik]  
[https://www.linkedin.com/in/ayalaajoul/]  
[ISMAGI RABAT] - Distributed Systems Course

---

⭐ If you found this project helpful, please give it a star!
