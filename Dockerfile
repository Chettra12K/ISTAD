# Multi-stage production image for the DevFlow Spring Boot REST API.
# Stage 1: build the executable Spring Boot JAR with Java 21 and Gradle Wrapper.
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle ./gradle
COPY settings.gradle build.gradle ./
RUN chmod +x gradlew

# Resolve dependencies separately to improve Docker layer caching.
RUN ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN ./gradlew bootJar --no-daemon \
    && find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -exec cp {} /workspace/app.jar \; \
    && test -s /workspace/app.jar

# Stage 2: run with a smaller JRE image and a non-root user.
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --uid 10001 --create-home appuser
COPY --from=builder --chown=appuser:appuser /workspace/app.jar /app/app.jar

USER appuser
EXPOSE 8080
ENV SERVER_PORT=8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
