# syntax=docker/dockerfile:1

# --- Build stage ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first for dependency caching
COPY --link pom.xml mvnw ./
COPY --link .mvn .mvn

# Make sure the Maven wrapper is executable and download dependencies with retry
RUN chmod +x mvnw && ./mvnw dependency:go-offline -Dmaven.wagon.http.retryHandler.count=3

# Copy the rest of the source code
COPY --link src ./src

# Build the application with offline mode
RUN ./mvnw package -DskipTests -o || ./mvnw package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a non-root user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Set permissions
RUN chown -R appuser:appgroup /app
USER appuser

# Expose port for JWT service
EXPOSE 8092

# JVM options for container awareness and memory limits
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80.0", "-jar", "/app/app.jar"]
