FROM openjdk:17-jdk-alpine as builder
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} log-app.jar
RUN java -Djarmode=layertools -jar log-app.jar extract

FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java","-cp","app:app/lib/*","-Dspring.profiles.active=docker","eg.com.demo.LogApplication"]