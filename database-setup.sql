-- Schema for the Smart Sunflower Farm Monitoring System.
-- Run once:  mysql -u root -p < database-setup.sql
-- (docker compose runs it automatically on first start.)

CREATE DATABASE IF NOT EXISTS sunflower_farm;
USE sunflower_farm;

CREATE TABLE IF NOT EXISTS humidity_readings (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    sensor_id     VARCHAR(50)  NOT NULL,
    soil_moisture DOUBLE       NOT NULL,
    air_humidity  DOUBLE       NOT NULL,
    status        VARCHAR(50),
    timestamp     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_humidity_ts (timestamp)
);

CREATE TABLE IF NOT EXISTS temperature_readings (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    sensor_id        VARCHAR(50) NOT NULL,
    air_temperature  DOUBLE      NOT NULL,
    soil_temperature DOUBLE      NOT NULL,
    status           VARCHAR(50),
    timestamp        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_temperature_ts (timestamp)
);

CREATE TABLE IF NOT EXISTS ph_readings (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    sensor_id VARCHAR(50) NOT NULL,
    ph_level  DOUBLE      NOT NULL,
    status    VARCHAR(50),
    timestamp DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ph_ts (timestamp)
);

CREATE TABLE IF NOT EXISTS weather_readings (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    sensor_id      VARCHAR(50) NOT NULL,
    wind_speed     DOUBLE      NOT NULL,
    rainfall       DOUBLE      NOT NULL,
    sunlight_hours DOUBLE      NOT NULL,
    timestamp      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_weather_ts (timestamp)
);

CREATE TABLE IF NOT EXISTS alerts (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    alert_type VARCHAR(50)  NOT NULL,
    severity   VARCHAR(20)  NOT NULL,
    message    VARCHAR(500) NOT NULL,
    sensor_id  VARCHAR(50),
    timestamp  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved   BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_alerts_ts (timestamp)
);
