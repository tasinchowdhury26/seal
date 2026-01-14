# Dockerfile for Seal Wallet Application
# This file tells Docker how to build a container for your Spring Boot app

# Step 1: Use Java 21 as base image (Eclipse Temurin is the official OpenJDK)
FROM eclipse-temurin:21-jre-alpine

# Step 2: Create a non-root user for security (required for Kubernetes)
RUN addgroup -g 1001 -S seal && adduser -u 1001 -S seal -G seal

# Step 3: Set the working directory inside the container
WORKDIR /app

# Step 4: Copy the JAR file from target folder into container
# The * wildcard handles version numbers automatically
COPY target/seal-*.jar app.jar

# Step 5: Change file ownership to the seal user
RUN chown seal:seal app.jar

# Step 6: Switch to non-root user (security best practice)
USER seal

# Step 7: Expose port 8080 (where Spring Boot runs)
EXPOSE 8080

# Step 8: Define the command to run when container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
