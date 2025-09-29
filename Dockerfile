# Simple single-stage build
FROM maven:3.8.6-openjdk-11 AS builder

WORKDIR /app

# Copy and build
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Runtime stage
FROM openjdk:11-jre-slim

# Install curl and create user
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN useradd -r -s /bin/false appuser

# Set working directory
WORKDIR /app

# Copy jar
COPY --from=builder /app/target/grok-sdr-system-1.0.0.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/evaluation/health || exit 1

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]