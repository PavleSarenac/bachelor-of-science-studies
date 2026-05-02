# === Stage 1: Build the JAR ===
FROM gradle:7.5.1-jdk17 AS builder

# Set work directory
WORKDIR /app

# Copy Gradle config and dependencies first (for better caching)
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Download dependencies
RUN gradle build -x test || return 0

# Copy the actual source code
COPY . .

# Build the fat JAR using the Shadow plugin
RUN gradle shadowJar --no-daemon

# === Stage 2: Run the app ===
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/build/libs/*-all.jar barberbooker-server.jar

# Expose Ktor's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "barberbooker-server.jar"]
