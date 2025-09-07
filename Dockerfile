# Multi-stage build
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Copiar settings.xml para GitHub Packages
COPY settings.xml /root/.m2/settings.xml

# Copiar parent POM
COPY parent/pom.xml /app/parent/pom.xml

# Copiar código fuente
COPY payment-microservice/pom.xml /app/
COPY payment-microservice/src /app/src

WORKDIR /app

# Compilar aplicación
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar JAR desde build stage
COPY --from=build /app/target/payment-microservice-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8087

# Comando de ejecución
CMD ["java", "-jar", "app.jar"]