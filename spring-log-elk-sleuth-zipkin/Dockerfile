FROM openjdk:17-jdk-alpine as builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} log-app.jar
RUN java -Djarmode=layertools -jar log-app.jar extract

FROM openjdk:17-jdk-alpine
WORKDIR /application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]