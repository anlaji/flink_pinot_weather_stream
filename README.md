# Flink Pinot Weather Streaming Application

This project is a **real-time weather data streaming application** built with **Apache Flink** and **Apache Pinot**, using **Java** and **Maven**. The application processes streaming weather events and ingests them into Pinot for low-latency analytical queries.

The project is developed and run using **IntelliJ IDEA**, with a custom Maven source directory configuration.

---

## 🛠️ Technologies

* **Java (OpenJDK)**
* **Flink**: Reads weather data from an API, transforms it into events.
* **Kafka**: Serves as the message broker for real-time events.
* **Pinot**: Stores and indexes events for real-time analytics.
* **Docker**: All components run in containers for easy setup.


---

## 🌦️ Application Overview

The application:

* Ingests streaming weather data (e.g. temperature, humidity, wind, timestamp)
* Processes events using **Apache Flink**
* Performs transformations and filtering on the stream
* Writes processed data into **Apache Pinot** real-time tables
* Enables fast analytical queries on weather metrics

---

## 📁 Project Structure

```
flink_pinot_weather_stream/
│
├── docker-compose.yml
│
├── flink-job/
│   ├── pom.xml
│   └── src/main/java/
│       └── WeatherStreamingJob.java
│       └── WeatherSource.java
│
├── pinot/
│   ├── schema.json
│   ├── realtime-table.json
│
└── README.md


> ⚠️ **Note**: The `pom.xml` explicitly defines the source directories because `src` is located inside an additional folder.

---

## ⚙️ Maven Configuration

Because the source code is located under `backend/src`, Maven is configured explicitly:

```xml
<build>
    <sourceDirectory>flink_job/src/main/java</sourceDirectory>
    <testSourceDirectory>flink_job/src/test/java</testSourceDirectory>
</build>
```

---
## Requirements

- Docker Desktop (Windows/Mac/Linux)
- Java 21 (for Flink job)
- Maven 3.8+
- Internet access (for public weather API)

Optional: WSL or Git Bash for smoother curl commands on Windows.

---
## ▶️ Build the Project

From the directory containing `pom.xml`:

```bash
mvn clean package
```

---

## 🚀 Run the Flink Job

### Local Execution

You can run the Flink streaming job directly from IntelliJ:

1. Open `WeatherStreamingJob.java`
2. Right-click → **Run**
3. Start docker: docker compose up -d
4. Check the containers health: docker ps
4. Create a topic with kafka in the container: 
```
   docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --create --topic weather-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   ```
5. Create a topic with kafka local: 
``` 
kafka-topics.bat --create --topic weather-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```
6. Create Pinot schema: 
```
curl -X POST "http://localhost:9000/schemas" \
   -H "Content-Type: application/json" \
   -d @pinot/schema.json
```
7. Create Pinot table:
```
curl -X POST "http://localhost:9000/tables" \
   -H "Content-Type: application/json" \
   -d @pinot/realtime-table.json
```
8. Open Pinot: http://localhost:9000/#/
9. Query: 
```
SELECT * FROM weather_event_REALTIME LIMIT 10;
```

10. Check topic:
```
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --topic weather-events \
  --from-beginning
```
