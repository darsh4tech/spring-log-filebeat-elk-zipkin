# Stage 1: Build the JAR AND Extract layers
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /application
# 1. Copy the pom and download dependencies (Layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy source and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# 3. Extract layers immediately in this stage
# We find the jar and extract it to a subfolder called 'extracted'
RUN mkdir extracted && java -Djarmode=layertools -jar target/*.jar extract --destination extracted

# Stage 2: Final Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /application
ENV SPRING_PROFILES_ACTIVE=docker
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
COPY --from=builder application/extracted/dependencies/ ./
COPY --from=builder application/extracted/spring-boot-loader/ ./
COPY --from=builder application/extracted/snapshot-dependencies/ ./
COPY --from=builder application/extracted/application/ ./
ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]