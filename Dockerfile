FROM alpine:3.20

RUN apk update && \
    apk upgrade && \
    apk add --no-cache openjdk21

COPY time-capsule-service-logic/target/time-capsule-service-logic-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]