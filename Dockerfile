FROM alpine:3.20

RUN apk update && \
    apk upgrade && \
    apk add --no-cache openjdk21

# Set working directory
WORKDIR /time-capsule-service

# Copy the JAR file from the Maven build output to the container
COPY time-capsule-service-logic/target/time-capsule-service-logic-1.0.0-rc3.jar app.jar
# COPY time-capsule-service-logic-1.0.0-rc3.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
