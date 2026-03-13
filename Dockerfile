# Build stage with Mandrel
FROM quay.io/mandrel/mandrel:24.1-jdk21 AS build
WORKDIR /build

# Copy all project files
COPY gradlew build.gradle settings.gradle gradle.properties ./
COPY gradle gradle/
COPY src src/

# Make gradlew executable
RUN chmod +x gradlew

# Set environment variables for Mandrel
ENV GRAALVM_HOME=/opt/mandrel
ENV PATH="${GRAALVM_HOME}/bin:${PATH}"

# Build the application using gradle wrapper with native-image tool
RUN ./gradlew clean nativeCompile --no-daemon

# Runtime stage - use mandrel minimal runtime
FROM registry.access.redhat.com/ubi9/ubi-minimal:9.4
WORKDIR /app

# Create non-root user
RUN useradd -r -s /bin/sh appuser

# Copy native binary from build stage
COPY --from=build /build/build/native/native-compile/ai app

# Change ownership and switch to non-root user
RUN chown appuser app && chmod 755 app
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS=""
ENV APP_LANGUAGE=zh
ENV AI_DEFAULT_MODEL=deepseek
ENV DEEPSEEK_ENABLED=false
ENV QWEN_ENABLED=false
ENV QWEN3CODER_ENABLED=false

ENTRYPOINT ["./app"]
