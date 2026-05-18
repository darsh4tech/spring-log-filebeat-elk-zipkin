# Spring Boot ELK Stack with Distributed Tracing

A production-style observability stack using:

* Spring Boot microservices
* Elasticsearch + Logstash + Kibana (ELK)
* Filebeat for log shipping
* Zipkin for distributed tracing
* Spring Cloud Sleuth / Micrometer Tracing

---

# Overview

This project demonstrates centralized logging and distributed tracing for Spring Boot microservices running in Docker or k8s.

Features include:

* Centralized log aggregation with ELK
* Distributed request tracing with Zipkin
* TraceId and SpanId correlation inside logs
* Filebeat autodiscovery for containers
* Real-time monitoring with Kibana dashboards

---

# Architecture

```text
    Client Request
          |
          v
  +-------------------+
  |   Log Service     | ---> calls ---> Second Service
  |      :8001        |                 :8002
  +-------------------+
   |                |
   v                v
-------------------------
| Filebeat  |   Metricbeat |
-------------------------
            |
            v
         Logstash
            |
            v
       Elasticsearch
          /        \
         v          v
      Kibana      Zipkin
```

---

# Services

| Service        | Port        | Description              |
| -------------- | ----------- | ------------------------ |
| Log Service    | 8001        | Main API service         |
| Second Service | 8002        | Downstream service       |
| Elasticsearch  | 9200        | Log indexing             |
| Kibana         | 5601        | Log visualization        |
| Zipkin         | 9411        | Trace visualization      |
| Logstash       | 5000 / 5044 | Log processing           |
| Filebeat       | -           | Container log collection |

---

# Project Structure

```text
spring-log-filebeat-elk-zipkin/
│
├── compose.yaml
├── k8s/
│   ├── elasticsearch/
│   ├── kibana/
│   ├── logstash/
│   ├── filebeat/
│   ├── zipkin/
│   ├── log-service/
│   └── second-service/
│
├── filebeat/
├── logstash/
├── kibana/
├── spring-log-elk-sleuth-zipkin/
└── spring-test-got-trace-id/
```

---

# API Endpoints

## Log Service

Base URL:

```bash
http://localhost:8001
```

| Method | Endpoint        | Description                     |
| ------ | --------------- | ------------------------------- |
| GET    | /employees      | Get all employees               |
| GET    | /employees/{id} | Get employee by ID              |
| POST   | /employees/send | Send employee to second service |

Example:

```bash
curl -X POST http://localhost:8001/employees/send \
-H "Content-Type: application/json" \
-d '{"empId":1,"name":"Medo","age":34}'
```

---

## Second Service

Base URL:

```bash
http://localhost:8002
```

| Method | Endpoint |
| ------ | -------- |
| POST   | /second  |

---

# Running with Docker Compose

## Start Stack

```bash
docker compose -f compose.yaml up -d
```

## Verify Containers

```bash
docker compose ps
```

## View Logs

```bash
docker compose logs -f
```

---

# k8s Deployment

## Build Docker Images

```bash
docker build -t log-service:latest ./spring-log-elk-sleuth-zipkin
```
```bash
docker build -t second-service:latest ./spring-test-got-trace-id
```

Load the image into Minikube:

```bash
minikube image load log-service:latest
```
```bash
minikube image load second-service:latest
```

---

## Deploy ELK + Zipkin

```bash
kubectl apply -f k8s/elasticsearch/
kubectl apply -f k8s/logstash/
kubectl apply -f k8s/kibana/
kubectl apply -f k8s/filebeat/
kubectl apply -f k8s/metricbeat.yml
kubectl apply -f k8s/zipkin/
kubectl apply -f k8s/spring-service.yaml
kubectl apply -f k8s/second-service.yaml
```
---

# Notes

* `minikube image load` avoids pushing images to Docker Hub
* Rebuild and reload images after code changes
* Ensure Kubernetes manifests use:

```yaml
imagePullPolicy: Never
```

## Verify Resources

```bash
kubectl get pods -n observability

kubectl get svc -n observability
```

---

# k8s Notes

## Filebeat

Deploy Filebeat as a DaemonSet to collect logs from all cluster nodes.

## Elasticsearch

For production:

* Use persistent volumes
* Configure resource limits
* Enable authentication and TLS

## Kibana Access

Use port-forwarding:

```bash
kubectl port-forward svc/kibana 5601:5601 -n observability
```

Then open:

```text
http://localhost:5601
```

## Zipkin Access

```bash
kubectl port-forward svc/zipkin 9411:9411 -n observability
```

---

# Tracing Configuration

```properties
management.tracing.sampling.probability=1.0

management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans
```

---

# Log Format Example

```json
{
  "@timestamp":"2026-05-14T10:30:45.123Z",
  "level":"INFO",
  "message":"Processing employee request",
  "traceId":"6a05036ecb18f3b27fbaa6fe635a968d",
  "spanId":"7fbaa6fe635a968d",
  "service":"log-management-service"
}
```

---

# Kibana Usage

1. Open Kibana
2. Go to Discover
3. Create index pattern:

```text
spring-logs-*
```

4. Search by TraceId:

```text
traceId:"6a05036ecb18f3b27fbaa6fe635a968d"
```

---

# Troubleshooting

## No Logs in Kibana

```bash
kubectl logs daemonset/filebeat -n observability
```

Check:

* Filebeat connectivity
* Logstash pipeline
* Elasticsearch health

---

## TraceId Missing

Verify:

* `spring-boot-starter-actuator`
* tracing dependencies
* MDC configuration in `logback-spring.xml`

---

## Services Cannot Communicate

Use k8s service names instead of localhost:

```text
http://second-service:8002
```

---

# Production Recommendations

* Reduce tracing sampling in production
* Add Elasticsearch persistence
* Configure authentication and TLS
* Add Grafana + Prometheus for metrics
* Use resource requests/limits
* Add readiness and liveness probes

---

# Tech Stack

* Java 21
* Spring Boot 3.2.1
* Elasticsearch 8.x
* Kibana 8.x
* Logstash 8.x
* Filebeat 8.x
* Zipkin

---

# References

* Spring Boot
* Elasticsearch
* Kibana
* Filebeat
* Logstash
* Zipkin
* k8s
