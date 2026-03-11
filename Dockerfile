# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

# Copy all project files
COPY gradlew build.gradle settings.gradle gradle.properties ./
COPY gradle gradle/
COPY src src/

# Make gradlew executable
RUN chmod +x gradlew

# Verify gradle-wrapper.jar exists
RUN ls -la gradle/wrapper/

# Build the application using gradle wrapper
RUN ./gradlew clean bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy jar from build stage
COPY --from=build /build/build/libs/*.jar app.jar

# Change ownership and switch to non-root user
RUN chown -R appgroup:appuser /app && chmod -R 755 /app
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV APP_LANGUAGE=zh
ENV AI_DEFAULT_MODEL=deepseek
ENV DEEPSEEK_ENABLED=false
ENV QWEN_ENABLED=false
ENV QWEN3CODER_ENABLED=false

# JVM argument to handle container memory limits
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
