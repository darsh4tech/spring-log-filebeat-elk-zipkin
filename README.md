# Spring Boot ELK Stack with Distributed Tracing

A comprehensive distributed logging and tracing solution combining **Spring Boot**, **Elasticsearch**, **Logstash**, **Kibana (ELK)**, **Filebeat**, **Spring Cloud Sleuth**, and **Zipkin**.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Services](#services)
  - [Log Service](#log-service)
  - [Second Service](#second-service)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Features](#features)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This project demonstrates a production-ready microservices logging and monitoring solution with:

- **Centralized Logging**: All application logs are collected, parsed, and indexed using the ELK Stack
- **Distributed Tracing**: Cross-service request tracking using Spring Cloud Sleuth and Zipkin
- **Container Monitoring**: Automatic log collection from Docker containers using Filebeat
- **Real-time Visualization**: Interactive dashboards in Kibana for log analysis
- **Trace Correlation**: Trace IDs and Span IDs automatically added to logs for request correlation

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Requests                          │
└────┬────────────────────────────────────────┬───────────────────┘
     │                                        │
     ▼                                        ▼
┌──────────────────────┐          ┌──────────────────────┐
│   Log Service       │          │  Second Service      │
│  (Port 8001)        │          │   (Port 8002)        │
│                      │          │                      │
│ - Employee API      │         │ - Employee Endpoint  │
│ - Send to Service   │         │                      │
│ - Tracing Enabled   │         │ - Tracing Enabled    │
└──────┬───────────────┘          └──────┬───────────────┘
       │                                 │
       │ Log Output (JSON)              │ Log Output (JSON)
       │                                 │
       └────────────┬────────────────────┘
                    │
                    ▼
          ┌──────────────────────┐
          │     Filebeat         │
          │  Docker Autodiscover │
          └──────────┬───────────┘
                     │
                     ▼
          ┌──────────────────────┐
          │      Logstash        │
          │  (Parse & Enrich)    │
          └──────────┬───────────┘
                     │
                     ▼
          ┌──────────────────────┐
          │    Elasticsearch     │
          │   (Indexing)         │
          └──────────┬───────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
         ▼                       ▼
    ┌─────────┐           ┌──────────┐
    │ Kibana  │           │  Zipkin  │
    │  (UI)   │           │ (Traces) │
    └─────────┘           └──────────┘
```

---

## 📋 Prerequisites

- **Docker** and **Docker Compose** (v1.29+)
- **Java 21** (for local development)
- **Maven** (for building)
- **Git**

---

## 📁 Project Structure

```
spring-log-filebeat-elk-zipkin/
├── compose.yaml                          # Docker Compose orchestration
├── filebeat/
│   └── filebeat.logstash.yml            # Filebeat autodiscover config
├── logstash/
│   ├── logstash.conf                    # Logstash pipeline config
│   └── logstash.yml                     # Logstash settings
├── kibana/
│   └── kibana.yml                       # Kibana configuration
├── spring-log-elk-sleuth-zipkin/        # Log Service (Port 8001)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       └── main/
│           ├── java/eg/com/demo/
│           │   ├── LogApplication.java
│           │   ├── controller/EmployeeController.java
│           │   ├── service/EmployeeService.java
│           │   └── model/Employee.java
│           └── resources/
│               ├── application-docker.properties
│               └── logback-spring.xml
└── spring-test-got-trace-id/            # Second Service (Port 8002)
    ├── pom.xml
    ├── Dockerfile
    └── src/
        └── main/
            ├── java/com/mine/test/springtestgottraceid/
            │   ├── SpringTestGotTraceIdApplication.java
            │   └── Employee.java
            └── resources/
                ├── application-docker.properties
                └── logback-spring.xml
```

---

## 🔧 Services

### Log Service

**Purpose**: Primary API service that demonstrates distributed tracing and logging  
**Port**: `8001`  
**Framework**: Spring Boot 3.2.1 with Spring Cloud Sleuth & Zipkin  
**Key Features**:
- Employee management API
- Inter-service communication (calls Second Service)
- Automatic trace context propagation
- JSON-formatted structured logging

### Second Service

**Purpose**: Secondary API service that receives requests from Log Service  
**Port**: `8002`  
**Framework**: Spring Boot 3.2.1 with Spring Cloud Sleuth & Zipkin  
**Key Features**:
- Endpoint to receive employee data
- Automatic trace context reception
- JSON-formatted structured logging
- Independent tracing capability

---

## 📡 API Endpoints

### Log Service (`http://localhost:8001`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `GET` | `/employees` | Get all employees | - | Array of Employee objects |
| `GET` | `/employees/{id}` | Get employee by ID | - | Employee object |
| `POST` | `/employees/send` | Send employee to Second Service | Employee JSON | void |

**Log Service - Employee Model**:
```json
{
  "empId": 1,
  "name": "John Doe",
  "age": 30
}
```

**Example Requests**:

```bash
# Get all employees
curl -X GET http://localhost:8001/employees

# Get employee by ID
curl -X GET http://localhost:8001/employees/1

# Send employee to second service (propagates trace context)
curl -X POST http://localhost:8001/employees/send \
  -H "Content-Type: application/json" \
  -d '{"empId": 1, "name": "Medo", "age": 34}'
```

---

### Second Service (`http://localhost:8002`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/second` | Receive and process employee data | Employee JSON | "SUCCESS" |

**Example Request**:

```bash
curl -X POST http://localhost:8002/second \
  -H "Content-Type: application/json" \
  -d '{"empId": 1, "name": "Medo", "age": 34}'
```

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
cd d:\projects\All_working_spaces\Spring_belk_stack_ws\spring-log-filebeat-elk-zipkin
```

### 2. Start All Services

```bash
docker compose -f compose.yaml up -d
```

This will start:
- ✅ Elasticsearch (Port 9200)
- ✅ Kibana (Port 5601)
- ✅ Logstash (Ports 5000, 5044)
- ✅ Filebeat
- ✅ Zipkin (Port 9411)
- ✅ Log Service (Port 8001)
- ✅ Second Service (Port 8002)

### 3. Verify Services are Running

```bash
docker compose ps
```

### 4. Generate Traffic

```bash
# Call Log Service endpoint
curl -X POST http://localhost:8001/employees/send \
  -H "Content-Type: application/json" \
  -d '{"empId": 1, "name": "Test User", "age": 25}'
```

### 5. View Logs in Kibana

Open `http://localhost:5601` and navigate to **Discover** to view logs

### 6. View Traces in Zipkin

Open `http://localhost:9411` and search for traces by service name

---

## ⚙️ Configuration

### Trace Settings (Both Services)

File: `src/main/resources/application-docker.properties`

```properties
# Trace all requests (1.0 = 100%)
management.tracing.sampling.probability=1.0

# Send traces to Zipkin
management.zipkin.tracing.endpoint=http://zipkin_elk:9411/api/v2/spans
```

### Logback Configuration

File: `src/main/resources/logback-spring.xml`

Includes trace context in JSON logs:
```json
{
  "@timestamp": "2026-05-14T10:30:45.123Z",
  "level": "INFO",
  "message": "Processing employee request",
  "traceId": "6a05036ecb18f3b27fbaa6fe635a968d",
  "spanId": "7fbaa6fe635a968d",
  "service": "log-management-service"
}
```

---

## ✨ Features

### 1. **Distributed Tracing**
- Automatic trace ID and span ID generation
- Cross-service correlation using Spring Cloud Sleuth
- Request context propagation via HTTP headers

### 2. **Centralized Logging**
- JSON-formatted structured logs
- Automatic collection from all containers
- Parsed and indexed in Elasticsearch

### 3. **Real-time Monitoring**
- Kibana dashboards for log analysis
- Zipkin UI for distributed trace visualization
- Filebeat autodiscovery for new containers

### 4. **Error Tracking**
- Stack traces captured and indexed
- Easy filtering by trace ID in Kibana
- Root cause analysis via correlated logs

---

## 🔍 Log Visualization in Kibana

Once logs are flowing:

1. Go to `http://localhost:5601`
2. Navigate to **Discover**
3. Select the `spring-logs-*` index pattern
4. Filter by `traceId` to view all logs for a request
5. Use `message` field to search for specific events

**Example Kibana Query**:
```
traceId: "6a05036ecb18f3b27fbaa6fe635a968d"
```

---

## 📊 Trace Visualization in Zipkin

1. Go to `http://localhost:9411`
2. Select service: `log-management-service` or `second-mgmt-service`
3. Click **Find Traces**
4. Click on a trace to view the request flow
5. Inspect span details and timing information

---

## 🐛 Troubleshooting

### Issue: No logs appearing in Kibana

**Solution**:
1. Verify Filebeat is running: `docker compose logs filebeat`
2. Check Logstash pipeline: `docker compose logs logstash`
3. Ensure services have the correct profile: `SPRING_PROFILES_ACTIVE=docker`

### Issue: TraceId/SpanId not appearing in logs

**Causes & Solutions**:
- **Missing actuator dependency**: Add `spring-boot-starter-actuator` to `pom.xml`
- **Incorrect MDC configuration**: Verify `logback-spring.xml` uses `%mdc{traceId}` and `%mdc{spanId}`
- **Profile not active**: Ensure `SPRING_PROFILES_ACTIVE=docker` is set

### Issue: Cross-service traces not connected

**Solution**:
- Use auto-configured HTTP client (`RestTemplate`/`WebClient`)
- Or manually propagate trace headers in custom HTTP clients
- Verify `management.tracing.sampling.probability=1.0` in both services

### Issue: Container hostname not resolving

**Solution**:
- Verify both services are on the same Docker network
- Use container name as hostname inside the network
- Example: `http://second-service:8002` (not `localhost:8002`)

---

## 📚 References

- [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth)
- [Elastic Stack Documentation](https://www.elastic.co/guide/index.html)
- [Zipkin](https://zipkin.io/)
- [Filebeat](https://www.elastic.co/guide/en/beats/filebeat/current/index.html)

---

## 📝 Notes

- **Java Version**: Requires Java 17+ (aligned across Dockerfile and pom.xml)
- **Spring Boot Version**: 3.2.1
- **Docker Network**: Uses custom bridge network `BELK` for inter-container communication
- **Trace Sampling**: Set to 100% for development; reduce for production

---

**Last Updated**: May 14, 2026
