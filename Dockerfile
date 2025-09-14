# Multi-stage Docker build for Payment Service

# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy all POM files
COPY parent/pom.xml /app/parent/pom.xml
COPY payment-microservice/pom.xml /app/payment-microservice/pom.xml

# Install parent POM
RUN cd /app/parent && mvn install -N

# Download microservice dependencies
RUN mkdir -p /app/payment-microservice/src/main/java/temp && \
    echo "public class Temp {}" > /app/payment-microservice/src/main/java/temp/Temp.java

RUN cd /app/payment-microservice && mvn dependency:go-offline -DskipTests

# Clean temp files
RUN rm -rf /app/payment-microservice/src/main/java/temp

# Build payment service
COPY payment-microservice/src /app/payment-microservice/src
RUN cd /app/payment-microservice && mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/payment-microservice/target/payment-microservice-*.jar app.jar

# Expose port
EXPOSE 8086

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
